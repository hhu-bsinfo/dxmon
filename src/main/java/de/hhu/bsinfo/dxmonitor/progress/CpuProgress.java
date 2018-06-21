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

import de.hhu.bsinfo.dxmonitor.state.CpuState;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

/**
 * Progress for the full CPU (all cores). All values are aggregated (i.e. on multi core systems: 100% = 1 core on full load)
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class CpuProgress implements Progress {
    private CpuCoreProgress[] m_cores;

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
     */
    public CpuProgress() {
        m_cores = new CpuCoreProgress[CpuState.getTotalCores()];

        for (int i = 0; i < CpuState.getTotalCores(); i++) {
            m_cores[i] = new CpuCoreProgress(i);
        }
    }

    /**
     * Get the aggregated total CPU load to total CPU cycles (delta of the previous two update calls) ratio
     */
    public float getCpuUsage() {
        return m_cpuUsage;
    }

    /**
     * Get the aggregated idle usage to total CPU cycles (delta of the previous two update calls) ratio
     */
    public float getIdleUsage() {
        return m_idleUsage;
    }

    /**
     * Get the aggregated usr usage to total CPU cycles (delta of the previous two update calls) ratio
     */
    public float getUsrUsage() {
        return m_usrUsage;
    }

    /**
     * Get the aggregated sys usage to total CPU cycles (delta of the previous two update calls) ratio
     */
    public float getSysUsage() {
        return m_sysUsage;
    }

    /**
     * Get the aggregated nice usage to total CPU cycles (delta of the previous two update calls) ratio
     */
    public float getNiceUsage() {
        return m_niceUsage;
    }

    /**
     * Get the aggregated soft irq usage to total CPU cycles (delta of the previous two update calls) ratio
     */
    public float getSoftIrqUsage() {
        return m_softIrqUsage;
    }

    /**
     * Get the aggregated irq usage to total CPU cycles (delta of the previous two update calls) ratio
     */
    public float getIrqUsage() {
        return m_irqUsage;
    }

    /**
     * Get the aggregated io wait usage to total CPU cycles (delta of the previous two update calls) ratio
     */
    public float getIoWaitUsage() {
        return m_ioWaitUsage;
    }

    /**
     * Get the aggregated total CPU load to total CPU cycles (delta of the previous two update calls) ratio in percent
     */
    public float getCpuUsagePercent() {
        return getCpuUsage() * 100;
    }

    /**
     * Get the aggregated idle usage to total CPU cycles (delta of the previous two update calls) ratio in percent
     */
    public float getIdleUsagePercent() {
        return getIdleUsage() * 100;
    }

    /**
     * Get the aggregated usr usage to total CPU cycles (delta of the previous two update calls) ratio in percent
     */
    public float getUsrUsagePercent() {
        return getUsrUsage() * 100;
    }

    /**
     * Get the aggregated sys usage to total CPU cycles (delta of the previous two update calls) ratio in percent
     */
    public float getSysUsagePercent() {
        return getSysUsage() * 100;
    }

    /**
     * Get the aggregated nice usage to total CPU cycles (delta of the previous two update calls) ratio in percent
     */
    public float getNiceUsagePercent() {
        return getNiceUsage() * 100;
    }

    /**
     * Get the aggregated soft irq usage to total CPU cycles (delta of the previous two update calls) ratio in percent
     */
    public float getSoftIrqUsagePercent() {
        return getSoftIrqUsage() * 100;
    }

    /**
     * Get the aggregated io wait usage to total CPU cycles (delta of the previous two update calls) ratio in percent
     */
    public float getIrqUsagePercent() {
        return getIrqUsage() * 100;
    }

    /**
     * Get the aggregated io wait usage to total CPU cycles (delta of the previous two update calls) ratio in percent
     */
    public float getIoWaitUsagePercent() {
        return getIoWaitUsage() * 100;
    }

    @Override
    public String toString() {
        return String.format("usage %2.2f, idle %2.2f, usr %2.2f, sys %2.2f, nice %2.2f, soft irq %2.2f, irq %2.2f, " +
                "io wait %2.2f", getCpuUsagePercent(), getIdleUsagePercent(), getUsrUsagePercent(),
                getSysUsagePercent(), getNiceUsagePercent(), getSoftIrqUsagePercent(), getIrqUsagePercent(),
                getIoWaitUsagePercent());
    }

    @Override
    public void update() throws StateUpdateException {
        // reset previous stats
        m_cpuUsage = 0;
        m_idleUsage = 0;
        m_sysUsage = 0;
        m_usrUsage = 0;
        m_niceUsage = 0;
        m_softIrqUsage = 0;
        m_irqUsage = 0;
        m_ioWaitUsage = 0;

        for (CpuCoreProgress core : m_cores) {
            core.update();

            m_cpuUsage += core.getCpuUsage();
            m_idleUsage += core.getIdleUsage();
            m_sysUsage += core.getSysUsage();
            m_usrUsage += core.getUsrUsage();
            m_niceUsage += core.getNiceUsage();
            m_softIrqUsage += core.getSoftIrqUsage();
            m_irqUsage += core.getIrqUsage();
            m_ioWaitUsage += core.getIoWaitUsage();
        }
    }

    @Override
    public String generateCSVHeader(final char p_delim) {
        StringBuilder builder = new StringBuilder();

        builder.append("cpu usage %").append(p_delim).append("idle usage %").append(p_delim).append("sys usage %")
                .append(p_delim).append("usr usage %").append(p_delim).append("nice usage %").append(p_delim)
                .append("soft irq usage %").append(p_delim).append("irq usage %").append(p_delim)
                .append("io wait usage %");

        for (CpuCoreProgress core : m_cores) {
            builder.append(core.generateCSVHeader(p_delim));
        }

        return builder.toString();
    }

    @Override
    public String toCSV(final char p_delim) {
        StringBuilder builder = new StringBuilder();

        builder.append(getCpuUsagePercent()).append(p_delim).append(getIdleUsagePercent()).append(p_delim)
                .append(getSysUsagePercent()).append(p_delim).append(getUsrUsagePercent()).append(p_delim)
                .append(getNiceUsagePercent()).append(p_delim).append(getSoftIrqUsagePercent()).append(p_delim)
                .append(getIrqUsagePercent()).append(p_delim).append(getIoWaitUsagePercent());

        for (CpuCoreProgress core : m_cores) {
            builder.append(core.toCSV(p_delim));
        }

        return builder.toString();
    }
}
