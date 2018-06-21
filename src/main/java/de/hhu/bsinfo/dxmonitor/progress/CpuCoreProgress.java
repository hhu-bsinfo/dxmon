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

package de.hhu.bsinfo.dxmonitor.progress;

import de.hhu.bsinfo.dxmonitor.state.CpuCoreState;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

/**
 * Progress for a single CPU core.
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class CpuCoreProgress implements Progress {
    private CpuCoreState m_lastState;
    private CpuCoreState m_currentState;

    private boolean m_first;

    private float m_cpuUsage;
    private float m_idleUsage;
    private float m_usrUsage;
    private float m_sysUsage;
    private float m_niceUsage;
    private float m_softIrqUsage;
    private float m_irqUsage;
    private float m_ioWaitUsage;

    /**
     * Constructor
     *
     * @param p_coreId Id of the core to track progress of
     */
    public CpuCoreProgress(final int p_coreId) {
        m_lastState = new CpuCoreState(p_coreId);
        m_currentState = new CpuCoreState(p_coreId);
        m_first = true;
    }

    /**
     * Get the total core load to total core cycles (delta of the previous two update calls) ratio
     */
    public float getCpuUsage() {
        return m_cpuUsage;
    }

    /**
     * Get the idle usage to total core cycles (delta of the previous two update calls) ratio
     */
    public float getIdleUsage() {
        return m_idleUsage;
    }

    /**
     * Get the usr usage to total core cycles (delta of the previous two update calls) ratio
     */
    public float getUsrUsage() {
        return m_usrUsage;
    }

    /**
     * Get the sys usage to total core cycles (delta of the previous two update calls) ratio
     */
    public float getSysUsage() {
        return m_sysUsage;
    }

    /**
     * Get the nice usage to total core cycles (delta of the previous two update calls) ratio
     */
    public float getNiceUsage() {
        return m_niceUsage;
    }

    /**
     * Get the soft irq usage to total core cycles (delta of the previous two update calls) ratio
     */
    public float getSoftIrqUsage() {
        return m_softIrqUsage;
    }

    /**
     * Get the irq usage to total core cycles (delta of the previous two update calls) ratio
     */
    public float getIrqUsage() {
        return m_irqUsage;
    }

    /**
     * Get the io wait usage to total core cycles (delta of the previous two update calls) ratio
     */
    public float getIoWaitUsage() {
        return m_ioWaitUsage;
    }

    /**
     * Get the total core load to total core cycles (delta of the previous two update calls) ratio in percent
     */
    public float getCpuUsagePercent() {
        return getCpuUsage() * 100;
    }

    /**
     * Get the idle usage to total core cycles (delta of the previous two update calls) ratio in percent
     */
    public float getIdleUsagePercent() {
        return getIdleUsage() * 100;
    }

    /**
     * Get the usr usage to total core cycles (delta of the previous two update calls) ratio in percent
     */
    public float getUsrUsagePercent() {
        return getUsrUsage() * 100;
    }

    /**
     * Get the sys usage to total core cycles (delta of the previous two update calls) ratio in percent
     */
    public float getSysUsagePercent() {
        return getSysUsage() * 100;
    }

    /**
     * Get the nice usage to total core cycles (delta of the previous two update calls) ratio in percent
     */
    public float getNiceUsagePercent() {
        return getNiceUsage() * 100;
    }

    /**
     * Get the soft irq usage to total core cycles (delta of the previous two update calls) ratio in percent
     */
    public float getSoftIrqUsagePercent() {
        return getSoftIrqUsage() * 100;
    }

    /**
     * Get the io wait usage to total core cycles (delta of the previous two update calls) ratio in percent
     */
    public float getIrqUsagePercent() {
        return getIrqUsage() * 100;
    }

    /**
     * Get the io wait usage to total core cycles (delta of the previous two update calls) ratio in percent
     */
    public float getIoWaitUsagePercent() {
        return getIoWaitUsage() * 100;
    }

    @Override
    public String toString() {
        return String.format("core %d: usage %2.2f, idle %2.2f, usr %2.2f, sys %2.2f, nice %2.2f, soft irq %2.2f, "
                + "irq %2.2f, io wait %2.2f", m_lastState.getCoreId(), getCpuUsagePercent(), getIdleUsagePercent(),
                getUsrUsagePercent(), getSysUsagePercent(), getNiceUsagePercent(), getSoftIrqUsagePercent(),
                getIrqUsagePercent(), getIoWaitUsagePercent());
    }

    @Override
    public void update() throws StateUpdateException {
        CpuCoreState tmp = m_lastState;
        m_lastState = m_currentState;
        m_currentState = tmp;

        if (m_first) {
            m_first = false;
            m_lastState.update();
        }

        m_currentState.update();

        float totalDiff = m_currentState.getTotal() - m_lastState.getTotal();

        if (totalDiff <= 0) {
            m_cpuUsage = 0;
            m_idleUsage = 0;
            m_sysUsage = 0;
            m_usrUsage = 0;
            m_niceUsage = 0;
            m_softIrqUsage = 0;
            m_irqUsage = 0;
            m_ioWaitUsage = 0;
        } else {
            m_cpuUsage = 1.0f - ((float) m_currentState.getIdle() - m_lastState.getIdle()) / totalDiff;
            m_idleUsage = ((float) m_currentState.getIdle() - m_lastState.getIdle()) / totalDiff;
            m_sysUsage = ((float) m_currentState.getSys() - m_lastState.getSys()) / totalDiff;
            m_usrUsage = ((float) m_currentState.getUsr() - m_lastState.getUsr()) / totalDiff;
            m_niceUsage = ((float) m_currentState.getNice() - m_lastState.getNice()) / totalDiff;
            m_softIrqUsage = ((float) m_currentState.getSoftIrq() - m_lastState.getSoftIrq()) / totalDiff;
            m_irqUsage = ((float) m_currentState.getIrq() - m_lastState.getIrq()) / totalDiff;
            m_ioWaitUsage = ((float) m_currentState.getIoWait() - m_lastState.getIoWait()) / totalDiff;
        }
    }

    @Override
    public String generateCSVHeader(final char p_delim) {
        return "core id" + p_delim + "cpu usage %" + p_delim + "idle usage %" + p_delim + "sys usage %" + p_delim +
                "usr usage %" + p_delim + "nice usage %" + p_delim + "soft irq usage %" + p_delim + "irq usage %" +
                p_delim + "io wait usage %";
    }

    @Override
    public String toCSV(final char p_delim) {
        return "" + m_lastState.getCoreId() + p_delim + getCpuUsagePercent() + p_delim + getIdleUsagePercent() +
                p_delim + getSysUsagePercent() + p_delim + getUsrUsagePercent() + p_delim + getNiceUsagePercent() +
                p_delim + getSoftIrqUsagePercent() + p_delim + getIrqUsagePercent() + p_delim + getIoWaitUsagePercent();
    }
}