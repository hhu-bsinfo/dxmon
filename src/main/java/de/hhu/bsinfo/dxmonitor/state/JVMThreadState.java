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
import de.hhu.bsinfo.dxutils.unit.TimeUnit;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * State of a single thread of the JVM
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class JVMThreadState implements State {

    private long m_tid;
    private ThreadMXBean m_threadMxBean;

    public JVMThreadState(final long p_tid) {
        m_tid = p_tid;
        m_threadMxBean = new JVMState().getThreadMXBean();
    }

    public ThreadMXBean getThreadMXBean() {
        return m_threadMxBean;
    }

    public ThreadInfo getThreadInfo() {
        return m_threadMxBean.getThreadInfo(m_tid);
    }

    public long getThreadID() {
        return m_tid;
    }

    public String getName() {
        return getThreadInfo().getThreadName();
    }

    public String getState() {
        return getThreadInfo().getThreadState().toString();
    }

    public boolean isInNative() {
        return getThreadInfo().isInNative();
    }

    public boolean isSuspended() {
        return getThreadInfo().isSuspended();
    }

    /**
     * Cpu Time in nanoseconds
     * @return
     */

    public long getCpuTimeNanoSeconds() {
        return m_threadMxBean.getThreadCpuTime(m_tid);
    }

    public TimeUnit getCpuTime() {
        return new TimeUnit(getCpuTimeNanoSeconds(), TimeUnit.NS);
    }


    public long getUserTimeNanoSeconds() {
        return m_threadMxBean.getThreadUserTime(m_tid);
    }

    public TimeUnit getUserTime() {
        return new TimeUnit(getUserTimeNanoSeconds(), TimeUnit.NS);
    }

    public long getSystemTimeNanoSeconds() {
        return getCpuTimeNanoSeconds() - getUserTimeNanoSeconds();
    }

    public TimeUnit getSystemTime() {
        return new TimeUnit(getSystemTimeNanoSeconds(), TimeUnit.NS);
    }

    /**
     * Waited Time in microseconds
     * @return
     */
    public long getWaitedTimeMilliSeconds() {
        return getThreadInfo().getWaitedTime();
    }

    public TimeUnit getWaitedTime() {
        return new TimeUnit(getWaitedTimeMilliSeconds(), TimeUnit.MS);
    }

    public long getBlockedTimeMilliSeconds() {
        return getThreadInfo().getBlockedTime();
    }

    public TimeUnit getBlockedTime() {
        return new TimeUnit(getThreadInfo().getBlockedTime(), TimeUnit.NS);
    }

    public long getWaitedCount() {
        return getThreadInfo().getWaitedCount();
    }

    public long getBlockedCount() {
        return getThreadInfo().getBlockedCount();
    }

    @Override
    public String toString() {
        return String.format("tid: %d, name: %s, state: %s, in_native: %b, suspended: %b, cpu_time: %dns, user_time: %dns, " +
                "sys_time: %dns, waited_time: %dms, waited_count: %d, blocked_time: %dns, blocked_count: %d\n",
                getThreadID(), getName(), getState(), isInNative(), isSuspended(), getCpuTimeNanoSeconds(), getUserTimeNanoSeconds(), getSystemTimeNanoSeconds(),
                getWaitedTimeMilliSeconds(), getWaitedCount(), getBlockedTimeMilliSeconds(), getBlockedCount());
    }

    @Override
    public void update() throws StateUpdateException {
        // nothing to update
    }

    @Override
    public String generateCSVHeader(final char p_delim) {
        return "tid" + p_delim + "name" + p_delim + "state" + p_delim + "is_in_native" + p_delim + "is_suspended" + p_delim +
                "cpu_time_ns" + p_delim + "user_time_ns" + p_delim + "sys_time_ns" + p_delim +
                "waited_time_ms" + p_delim + "waited_count" + p_delim + "blocked_time_ms" + p_delim + "blocked_count";
    }

    @Override
    public String toCSV(final char p_delim) {
        return "" +getThreadID() + p_delim + getName() + p_delim + getState() + p_delim + isInNative() + p_delim + isSuspended() + p_delim +
                getCpuTimeNanoSeconds() + p_delim + getUserTime() + p_delim + getSystemTime() + p_delim +
                getWaitedTimeMilliSeconds() + p_delim + getWaitedCount() + p_delim + getBlockedTimeMilliSeconds() + p_delim + getBlockedCount();
    }
}
