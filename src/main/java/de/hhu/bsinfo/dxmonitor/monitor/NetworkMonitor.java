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

import de.hhu.bsinfo.dxmonitor.progress.NetworkProgress;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

import java.util.ArrayList;

/**
 * Monitor for Network related data
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 14.07.2018
 */
public class NetworkMonitor implements Monitor {
    private final NetworkProgress m_progress;

    private final ArrayList<ThresholdDouble> m_thresholdReceiveThroughput;
    private final ArrayList<ThresholdDouble> m_thresholdTransmitThroughput;

    /**
     * Constructor
     * @param p_name NIC identifier
     */
    public NetworkMonitor(final String p_name) {
        m_progress = new NetworkProgress(p_name);
        m_thresholdReceiveThroughput = new ArrayList<>();
        m_thresholdTransmitThroughput = new ArrayList<>();
    }

    /**
     * Adds a Callback which will be triggered if receive throughput exceeds a certain value
     * @param p_threshold Threshold class which will call the callback
     */
    public void addThresholdReceiveThroughput(final ThresholdDouble p_threshold) {
        m_thresholdReceiveThroughput.add(p_threshold);
    }

    /**
     * Adds a Callback which will be triggered if transmit throughput exceeds a certain value
     * @param p_threshold Threshold class which will call the callback
     */
    public void addThresholdTransmitThroughput(final ThresholdDouble p_threshold) {
        m_thresholdTransmitThroughput.add(p_threshold);
    }

    /**
     * Returns the network progress class.
     * @return network progress
     */
    public NetworkProgress getProgress() {
        return m_progress;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public void update() throws StateUpdateException {
        m_progress.update();

        for (ThresholdDouble threshold : m_thresholdReceiveThroughput) {
            threshold.evaluate(m_progress.getReceiveThroughput());
        }

        for (ThresholdDouble threshold : m_thresholdTransmitThroughput) {
            threshold.evaluate(m_progress.getTransmitThroughput());
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
