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

import de.hhu.bsinfo.dxmonitor.state.MemState;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

import java.util.ArrayList;

/**
 * Monitor for Memory related data
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 18.03.2018
 */
public class MemMonitor implements Monitor {
    private final MemState m_state;

    private final ArrayList<MultipleThresholdDouble> m_thresholdMemoryFree;

    /**
     * Constructor
     */
    public MemMonitor() {
        m_state = new MemState();
        m_thresholdMemoryFree = new ArrayList<>();
    }

    /**
     * Adds a Callback which will be triggered if free memory exceeds/deceeds a certain value
     * @param p_threshold Threshold class which will call the callback
     */
    public void addThresholdMemoryFree(final MultipleThresholdDouble p_threshold) {
        m_thresholdMemoryFree.add(p_threshold);
    }

    /**
     * Returns the memory state class.
     * @return memory state
     */
    public MemState getState() {
        return m_state;
    }

    @Override
    public void update() throws StateUpdateException {
        m_state.update();

        for (MultipleThresholdDouble threshold : m_thresholdMemoryFree) {
            threshold.evaluate(m_state.getFreePercent());
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
