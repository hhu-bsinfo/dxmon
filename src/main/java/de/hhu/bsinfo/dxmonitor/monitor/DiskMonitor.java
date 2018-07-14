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

import de.hhu.bsinfo.dxmonitor.progress.DiskProgress;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

import java.util.ArrayList;

/**
 * Monitor for Disk related data
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 14.07.2018
 */
public class DiskMonitor implements Monitor {
    private final DiskProgress m_progress;

    private final ArrayList<ThresholdDouble> m_thresholdsReadThroughput;
    private final ArrayList<ThresholdDouble> m_thresholdsWriteThroughput;

    /**
     * Constructor
     * @param p_name Disk identifier
     */
    public DiskMonitor(final String p_name) {
        m_progress = new DiskProgress(p_name);
        m_thresholdsReadThroughput = new ArrayList<>();
        m_thresholdsWriteThroughput = new ArrayList<>();
    }

    /**
     * Adds a Callback which will be triggered if read throughput exceeds a certain value
     * @param p_threshold Threshold class which will call the callback
     */
    public void addThresholdReadThroughput(final ThresholdDouble p_threshold) {
        m_thresholdsReadThroughput.add(p_threshold);
    }

    /**
     * Adds a Callback which will be triggered if write throughput exceeds a certain value
     * @param p_threshold Threshold class which will call the callback
     */
    public void addThresholdWriteThroughput(final ThresholdDouble p_threshold) {
        m_thresholdsWriteThroughput.add(p_threshold);
    }

    /**
     * Returns the disk progress class.
     * @return disk class
     */
    public DiskProgress getProgress() {
        return m_progress;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public void update() throws StateUpdateException {
        m_progress.update();

        for (ThresholdDouble threshold : m_thresholdsReadThroughput) {
            threshold.evaluate(m_progress.getReadThroughput());
        }

        for (ThresholdDouble threshold : m_thresholdsWriteThroughput) {
            threshold.evaluate(m_progress.getWriteThroughput());
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
}