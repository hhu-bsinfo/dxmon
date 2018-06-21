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
import java.util.StringTokenizer;

/**
 * State of a single CPU core
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class CpuCoreState implements State {
    private static final String PROC_STAT = "/proc/stat";

    private final ProcSysFileReader m_reader;

    private final int m_coreId;
    private final int[] m_stats;

    /**
     * Constructor
     *
     * @param p_coreId
     *     Id of the core: [0, maxCores)
     */
    public CpuCoreState(final int p_coreId) {
        if (p_coreId < 0 || p_coreId > Runtime.getRuntime().availableProcessors()) {
            throw new IllegalArgumentException("Invalid core id " + p_coreId + " for available core count " +
                    Runtime.getRuntime().availableProcessors());
        }

        try {
            m_reader = new ProcSysFileReader(PROC_STAT);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }

        m_coreId = p_coreId;

        // 0 usr - 1 nice - 2 sys - 3 idle - 4 iowait - 5 irq - 6 softirq - 7 total
        m_stats = new int[8];
    }

    /**
     * Get the id of the core
     */
    public int getCoreId() {
        return m_coreId;
    }

    /**
     * Returns the number of clock cycles (in Jiffies) that processes have spent in user mode on this core/cpu
     */
    public int getUsr() {
        return m_stats[0];
    }

    /**
     * Returns the number of clock cycles that niced processes have spent in user mode on this core/cpu
     */
    public int getNice() {
        return m_stats[1];
    }

    /**
     * Returns the number of clock cycles that processes have spent in kernel mode on this core/cpu
     */
    public int getSys() {
        return m_stats[2];
    }

    /**
     * Returns the number of clock cycles where the cpu/core was doing nothing
     */
    public int getIdle() {
        return m_stats[3];
    }

    /**
     * Returns the number of clock cycles this core have spent waiting for I/O to complete
     */
    public int getIoWait() {
        return m_stats[4];
    }

    /**
     * Returns the number of clock cycles that this core have spent for servicing interrupts
     */
    public int getIrq() {
        return m_stats[5];
    }

    /**
     * Returns the number of clock cycles that this core have spent for servicing software interrupts
     */
    public int getSoftIrq() {
        return m_stats[6];
    }

    /**
     * Returns the total number of clock cycles.
     */
    public int getTotal() {
        return m_stats[7];
    }

    @Override
    public String toString() {
        return "core id: " + getCoreId() + ", usr " + getUsr() + ", nice " + getNice() + ", sys " + getSys() +
                ", idle " + getIdle() + ", iowait " + getIoWait() + ", irq " + getIrq() + ", softirq " + getSoftIrq();
    }

    /**
     * Reads the content of the /proc/stat file to generate a cpu state. This method will be called automatically
     * in the construtor. In addition to that this method can be used to refresh the state
     */
    @Override
    public void update() throws StateUpdateException {
        String cpuState;

        try {
            cpuState = m_reader.readCompleteFile();
        } catch (IOException e) {
            throw new StateUpdateException("Can't read file " + PROC_STAT + ": " + e.getMessage());
        }

        StringTokenizer tokenizer = new StringTokenizer(cpuState, "\n");

        for (int i = 0; i < m_coreId; i++) {
            tokenizer.nextToken();
        }

        tokenizer = new StringTokenizer(tokenizer.nextToken(), " ");
        tokenizer.nextElement(); // skip the cpuX token

        // reset previous sum
        m_stats[7] = 0;

        for (int i = 0; i < 7; i++) {
            m_stats[i] = Integer.parseInt(tokenizer.nextToken());

            // sum the total amount of cpu time spent
            m_stats[7] += m_stats[i];
        }
    }

    @Override
    public String generateCSVHeader(final char p_delim) {
        return "core id" + p_delim + "usr" + p_delim + "nice" + p_delim + "sys" + p_delim + "idle" + p_delim +
                "iowait" + p_delim + "irq" + p_delim + "softirq";
    }

    @Override
    public String toCSV(final char p_delim) {
        return "" + getCoreId() + p_delim + getUsr() + p_delim + getNice() + p_delim + getSys() + p_delim + getIdle() +
                p_delim + getIoWait() + p_delim + getIrq() + p_delim + getSoftIrq();
    }
}
