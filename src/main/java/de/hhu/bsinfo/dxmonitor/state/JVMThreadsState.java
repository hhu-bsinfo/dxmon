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

import java.lang.management.ThreadMXBean;
import java.util.*;

/**
 * State of all (currently) existing threads of the JVM
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class JVMThreadsState implements State {

    private ThreadMXBean m_threadMxBean;
    private List<JVMThreadState> m_threads;

    /**
     * Constructor
     */
    public JVMThreadsState() {
        m_threadMxBean = new JVMState().getThreadMXBean();
        m_threads = new ArrayList<JVMThreadState>();
    }

    /**
     * Returns the amount of daemon threads.
     * @return daemon thread count
     */
    public long getDaemonThreadCount() {
        return m_threadMxBean.getDaemonThreadCount();
    }


    /**
     * Returns the amount threads.
     * @return thread count
     */
    public long getThreadCount() {
        return m_threadMxBean.getThreadCount();
    }

    /**
     * Returns the amount of non-daemon threads.
     * @return non-daemon thread count
     */
    public long getNonDaemonThreadCount() {
        return getThreadCount() - getDaemonThreadCount();
    }


    /**
     * Returns the peak thread count.
     * @return peak thread count
     */
    public long getPeakThreadCount() {
        return m_threadMxBean.getPeakThreadCount();
    }


    /**
     * Returns a list of all threads id's.
     * @return list with all thread id's
     */
    public long[] getThreadIDs() {
        return m_threadMxBean.getAllThreadIds();
    }


    /**
     * Returns all JVMThreadStates.
     * @return list with JVMThreadStates
     */
    public List<JVMThreadState> getJVMThreadStates() {
        return m_threads;
    }

    @Override
    public String toString() {
        String tmp = String.format("thread count: %d, daemon cnt: %d, non-daemon cnt: %d, peak count: %d\n",
                getThreadCount(), getDaemonThreadCount(), getNonDaemonThreadCount(), getPeakThreadCount());
        for(JVMThreadState threadState : m_threads) {
            tmp += threadState.toString();
            tmp += "\n";
        }

        return tmp;
    }

    @Override
    public void update() throws StateUpdateException {
        // remove all JVMThreadStates
        m_threads.clear();
        // need to call getThreadIDs() on each update because new threads can be spawned or old ones cann be destroyed
        for(long tid : getThreadIDs()) {
            m_threads.add(new JVMThreadState(tid));
        }
    }

    @Override
    public String generateCSVHeader(final char p_delim) { // TODO find a way to put thread states also to csv (cpucore needs a solution for this too)
        return "thread_cnt" + p_delim + "daemon_cnt" + p_delim + "non_daemon_cnt" + p_delim + "peak_cnt";
    }

    @Override
    public String toCSV(final char p_delim) {
        return "" + getThreadCount() + p_delim + getDaemonThreadCount() + p_delim + getNonDaemonThreadCount() + p_delim + getPeakThreadCount();
    }
}
