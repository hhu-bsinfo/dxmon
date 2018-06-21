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

package de.hhu.bsinfo.dxmonitor.monitor;

import de.hhu.bsinfo.dxmonitor.progress.CpuProgress;
import de.hhu.bsinfo.dxmonitor.state.CpuState;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

import java.util.ArrayList;

/**
 * Monitor for CPU related data
 *
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class CpuMonitor implements Monitor {
    private final CpuProgress m_progress;
    private final CpuState m_state;

    private final ArrayList<MultipleThresholdDouble> m_thresholdsCpuUsage;

    /**
     * Constructor
     */
    public CpuMonitor() {
        m_progress = new CpuProgress();
        m_state = new CpuState();
        m_thresholdsCpuUsage = new ArrayList<>();
    }

    /**
     * Add a threshold for cpu usage tracking
     *
     * @param p_threshold Threshold to add
     */
    public void addThresholdCpuUsagePercent(final MultipleThresholdDouble p_threshold) {
        m_thresholdsCpuUsage.add(p_threshold);
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public void update() throws StateUpdateException {
        m_progress.update();
        m_state.update();

        for (MultipleThresholdDouble threshold : m_thresholdsCpuUsage) {
            threshold.evaluate(m_progress.getCpuUsagePercent());
        }
    }

    @Override
    public String generateCSVHeader(char p_delim) {
        return null;
    }

    @Override
    public String toCSV(char p_delim) {
        return null;
    }

    public CpuProgress getProgress() {
        return m_progress;
    }

    public float[] getLoads() {
        float load1 = m_state.getLoadAvarage1Min();
        float load5 = m_state.getLoadAvarage5Min();
        float load15 = m_state.getLoadAvarage15Min();

        return new float[]{load1, load5, load15};
    }
}
