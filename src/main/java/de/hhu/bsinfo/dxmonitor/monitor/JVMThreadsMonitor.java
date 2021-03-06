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

import de.hhu.bsinfo.dxmonitor.state.JVMThreadsState;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

/**
 * Monitor for JVM Threads related data
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 14.07.2018
 */
public class JVMThreadsMonitor implements Monitor {
    private final JVMThreadsState m_state;

    /**
     * Constructor
     */
    public JVMThreadsMonitor() {
        m_state = new JVMThreadsState();
    }

    /**
     * Returns JVMThreadsState class
     * @return JVMThreadsState
     */
    public JVMThreadsState getState() {
        return m_state;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public void update() throws StateUpdateException {

    }

    @Override
    public String generateCSVHeader(char p_delim) { return null; }

    @Override
    public String toCSV(char p_delim) { return null; }
}
