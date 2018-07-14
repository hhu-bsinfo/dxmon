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

import de.hhu.bsinfo.dxmonitor.state.JVMThreadState;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

/**
 * Monitor for a jvm thread related data
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 14.07.2018
 */
public class JVMThreadMonitor implements Monitor {
    private final JVMThreadState m_state;

    /**
     * Constructor
     * @param p_tid ThreadID
     */
    public JVMThreadMonitor(final long p_tid) {
        m_state = new JVMThreadState(p_tid);
    }

    /**
     * Returns JVMThreadState class for certain thread
     * @return JVMThreadState
     */
    public JVMThreadState getState() {
        return m_state;
    }

    @Override
    public void update() throws StateUpdateException { }

    @Override
    public String generateCSVHeader(char p_delim) { return null; }

    @Override
    public String toCSV(char p_delim) { return null; }
}
