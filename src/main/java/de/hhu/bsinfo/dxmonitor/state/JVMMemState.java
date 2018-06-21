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

package de.hhu.bsinfo.dxmonitor.state;

import de.hhu.bsinfo.dxmonitor.util.JVMState;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

/**
 * Memory state of the JVM
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class JVMMemState implements State {

    private JVMState m_jvmState;

    /**
     * Constructor
     */
    public JVMMemState() {
        m_jvmState = new JVMState();
    }

    /**
     * Get the current usage information of the heap memory
     */
    public MemoryUsage getHeapUsage() {
        return m_jvmState.getMemoryMXBean().getHeapMemoryUsage();
    }

    /**
     * Get the current usage information of the non heap memory
     */
    public MemoryUsage getNonHeapUsage() {
        return m_jvmState.getMemoryMXBean().getNonHeapMemoryUsage();
    }

    /**
     * Get the list of currently available and used memory pools (including stats)
     */
    public List<MemoryPoolMXBean> getListMemoryPoolMXBean() {
        return m_jvmState.getListMemoryPoolMXBean();
    }

    /**
     * Get the list of currently available and used garbage collectors (including stats)
     */
    public List<GarbageCollectorMXBean> getListGarbageCollectorMXBean() {
        return m_jvmState.getListGarbageCollectorMXBean();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Heap usage:\n");
        builder.append(getHeapUsage());
        builder.append('\n');
        builder.append("Non heap usage:\n");
        builder.append(getNonHeapUsage());
        builder.append('\n');

        builder.append("Memory pools\n");

        for (MemoryPoolMXBean pool : getListMemoryPoolMXBean()) {
            builder.append(pool);
        }

        builder.append('\n');
        builder.append("Garbage collectors:\n");

        for (GarbageCollectorMXBean gc : getListGarbageCollectorMXBean()) {
            builder.append(gc);
        }

        return builder.toString();
    }

    @Override
    public void update() throws StateUpdateException {
        // nothing to update
    }

    @Override
    public String generateCSVHeader(char p_delim) {
        String header = "heap_init" + p_delim + "heap_commited" + p_delim + "heap_used" + p_delim +
                "non_heap_init" + p_delim + "non_heap_commited" + p_delim + "non_heap_used";

        StringBuilder tmp = new StringBuilder("");
        List<MemoryPoolMXBean> pools = getListMemoryPoolMXBean();
        for (int i=0; i<pools.size(); i++) {
            MemoryPoolMXBean pool = pools.get(i);
            String name = pool.getName();

            tmp.append(name + "_init");
            tmp.append(p_delim);
            tmp.append(name + "_commited");
            tmp.append(p_delim);
            tmp.append(name + "_used");

            if (i < pools.size() - 1) {
                tmp.append(p_delim);
            }
        }

        if (!tmp.toString().isEmpty()) {
           header += p_delim;
           header += tmp.toString();
        }

        return header;
    }

    @Override
    public String toCSV(char p_delim) {
        String csv = "" + getHeapUsage().getInit() + p_delim + getHeapUsage().getCommitted() + p_delim + getHeapUsage().getUsed();
        csv += p_delim + getNonHeapUsage().getInit() + p_delim + getNonHeapUsage().getCommitted() + p_delim + getNonHeapUsage().getUsed();

        StringBuilder tmp = new StringBuilder("");
        List<MemoryPoolMXBean> pools = getListMemoryPoolMXBean();
        for (int i=0; i<pools.size(); i++) {
            MemoryPoolMXBean pool = pools.get(i);

            tmp.append(pool.getUsage().getInit());
            tmp.append(p_delim);
            tmp.append(pool.getUsage().getCommitted());
            tmp.append(p_delim);
            tmp.append(pool.getUsage().getUsed());

            if (i < pools.size() - 1) {
                tmp.append(p_delim);
            }
        }

        if (!tmp.toString().isEmpty()) {
            csv += p_delim;
            csv += tmp.toString();
        }

        return csv;
    }
}
