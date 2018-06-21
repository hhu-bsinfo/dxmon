/*
 * Copyright (C) 2017 Heinrich-Heine-Universitaet Duesseldorf, Institute of Computer Science, Department Operating Systems
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package de.hhu.bsinfo.dxmonitor.util;

import com.sun.tools.attach.VirtualMachine;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility class to simplify access to states of the currently running JVM
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class JVMState {
    private MBeanServerConnection m_mbs;

    private MemoryMXBean m_memoryMXBean;
    private ThreadMXBean m_threadMXBean;
    private List<MemoryPoolMXBean> m_memoryPoolMXBean;
    private List<GarbageCollectorMXBean> m_garbageCollectorMXBeans;

    /**
     * Constructor
     */
    public JVMState() {
        try {
            // returns format 12345@hostname
            String[] tmp = ManagementFactory.getRuntimeMXBean().getName().split("@");

            if (tmp.length != 2) {
                throw new IllegalStateException();
            }

            VirtualMachine vm = VirtualMachine.attach(String.valueOf(tmp[0]));
            m_mbs = getMBSrvConn(vm);
            vm.detach();

            m_memoryMXBean = ManagementFactory.newPlatformMXBeanProxy(m_mbs, ManagementFactory.MEMORY_MXBEAN_NAME,
                    MemoryMXBean.class);
            m_threadMXBean = ManagementFactory.newPlatformMXBeanProxy(m_mbs, ManagementFactory.THREAD_MXBEAN_NAME,
                    ThreadMXBean.class);

            m_garbageCollectorMXBeans = getGarbageCollectorMXBeansFromRemote();
            m_memoryPoolMXBean = getMemoryPoolMXBeansFromRemote();
        } catch (Exception e) {
            throw new RuntimeException("Initializing JVMStateOld state failed", e);
        }
    }

    /**
     * Get the MemoryMXBean object which describes the current state of the heap/non-heap memory
     */
    public MemoryMXBean getMemoryMXBean() {
        return m_memoryMXBean;
    }

    /**
     * Get the ThreadMXBean object which describes the current state of all threads of the JVM
     */
    public ThreadMXBean getThreadMXBean() {
        return m_threadMXBean;
    }

    /**
     * Get the list of MemoryPoolMXBean objects which describe the memory pools used by the JVM
     */
    public List<MemoryPoolMXBean> getListMemoryPoolMXBean() {
        return m_memoryPoolMXBean;
    }

    /**
     * Get the GarbageCollectorMXBean object which describes the state of the garbage collectors of the JVM
     */
    public List<GarbageCollectorMXBean> getListGarbageCollectorMXBean() {
        return m_garbageCollectorMXBeans;
    }

    /**
     * Connects via JMX and RMI to the (remote) java virtual machine.
     *
     * @param p_vm attached virtual machine object
     * @return MBeanServerConnection to fetch the needed MXBean objects
     * @throws IOException Thrown by JMXConnectorFactory
     */
    private MBeanServerConnection getMBSrvConn(final VirtualMachine p_vm) throws IOException {
        String connectorAddress = p_vm.getAgentProperties().getProperty(
                "com.sun.management.jmxremote.localConnectorAddress");

        if (connectorAddress == null) {
            p_vm.startLocalManagementAgent();
            connectorAddress = p_vm.getAgentProperties().getProperty(
                    "com.sun.management.jmxremote.localConnectorAddress");
        }

        JMXServiceURL url = new JMXServiceURL(connectorAddress);
        JMXConnector connector = JMXConnectorFactory.connect(url);
        return connector.getMBeanServerConnection();
    }

    /**
     * Returns a list of information about the memory pools.
     */
    private List<MemoryPoolMXBean> getMemoryPoolMXBeansFromRemote()
            throws MalformedObjectNameException, IOException {
        Set<ObjectName> gcnames = m_mbs.queryNames(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE +
                ",name=*"), null);
        List<MemoryPoolMXBean> mBeans = new ArrayList<>(gcnames.size());

        for (ObjectName on : gcnames) {
            mBeans.add(ManagementFactory.newPlatformMXBeanProxy(m_mbs, on.toString(), MemoryPoolMXBean.class));
        }

        return mBeans;
    }

    /**
     * Returns a list of garbage collector information
     */
    private List<GarbageCollectorMXBean> getGarbageCollectorMXBeansFromRemote()
            throws MalformedObjectNameException, IOException {
        Set<ObjectName> gcnames = m_mbs.queryNames(new ObjectName(
                ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=*"), null);
        List<GarbageCollectorMXBean> gcBeans = new ArrayList<>(gcnames.size());

        for (ObjectName on : gcnames) {
            gcBeans.add(ManagementFactory.newPlatformMXBeanProxy(m_mbs, on.toString(), GarbageCollectorMXBean.class));
        }

        return gcBeans;
    }
}