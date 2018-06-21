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

import de.hhu.bsinfo.dxmonitor.util.ProcSysFileReader;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * State of the full CPU (all cores)
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class CpuState implements State {
    private static final String PROC_LOADAVG = "/proc/loadavg";

    private final ProcSysFileReader m_reader;

    private final int m_totalCores;

    private final CpuCoreState[] m_coreStates;
    private final float[] m_loads;

    /**
     * Constructor
     */
    public CpuState() {
        try {
            m_reader = new ProcSysFileReader(PROC_LOADAVG);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }

        m_totalCores = getTotalCores();
        m_coreStates = new CpuCoreState[m_totalCores];

        for (int i = 0; i < m_coreStates.length; i++) {
            m_coreStates[i] = new CpuCoreState(i);
        }

        // avg of 1, 5 and 15 minutes
        m_loads = new float[3];
    }

    /**
     * Get the total number of cores (including virtual/hyper threading cores) of the current instance
     */
    public static int getTotalCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Get the state of a single core
     *
     * @param p_coreId Id of the core to get the state of
     * @return Core state of the specified core
     */
    public CpuCoreState getCoreState(final int p_coreId) {
        if (p_coreId < 0 || p_coreId > m_totalCores) {
            throw new IllegalArgumentException("Invalid core id " + p_coreId + " for available core count " +
                    m_totalCores);
        }

        return m_coreStates[p_coreId];
    }

    /**
     * Get the load average of the last minute
     */
    public float getLoadAvarage1Min() {
        return m_loads[0];
    }

    /**
     * Get the load average of the last 5 minutes
     */
    public float getLoadAvarage5Min() {
        return m_loads[1];
    }

    /**
     * Get the load average of the last 15 minutes
     */
    public float getLoadAvarage15Min() {
        return m_loads[2];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("load avg 1 min %f, load avg 5 min %f, load avg 15 min %f\n", getLoadAvarage1Min(),
                getLoadAvarage5Min(), getLoadAvarage15Min()));

        for (CpuCoreState state : m_coreStates) {
            builder.append(state);
            builder.append('\n');
        }

        return builder.toString();
    }

    @Override
    public void update() throws StateUpdateException {
        for (CpuCoreState coreState : m_coreStates) {
            coreState.update();
        }

        String tmp;

        try {
            tmp = m_reader.readCompleteFile();
        } catch (IOException e) {
            throw new StateUpdateException("Can't read file " + PROC_LOADAVG + ": " + e.getMessage());
        }

        for (int i = 0; i < 3; i++) {
            int index = tmp.indexOf(' ');
            m_loads[i] = Float.parseFloat(tmp.substring(0, index));
            tmp = tmp.substring(index + 1);
        }
    }

    @Override
    public String generateCSVHeader(final char p_delim) {
        StringBuilder builder = new StringBuilder();

        builder.append("load avg 1 min");
        builder.append(p_delim);
        builder.append("load avg 5 min");
        builder.append(p_delim);
        builder.append("load avg 15 min");

        for (CpuCoreState coreState : m_coreStates) {
            builder.append(p_delim);
            builder.append(coreState.generateCSVHeader(p_delim));
        }

        return builder.toString();
    }

    @Override
    public String toCSV(final char p_delim) {
        StringBuilder builder = new StringBuilder();

        builder.append(getLoadAvarage1Min());
        builder.append(p_delim);
        builder.append(getLoadAvarage5Min());
        builder.append(p_delim);
        builder.append(getLoadAvarage15Min());

        for (CpuCoreState coreState : m_coreStates) {
            builder.append(p_delim);
            builder.append(coreState.toCSV(p_delim));
        }

        return builder.toString();
    }
}
