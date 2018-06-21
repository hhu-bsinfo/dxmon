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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

import de.hhu.bsinfo.dxmonitor.util.ProcSysFileReader;
import de.hhu.bsinfo.dxutils.unit.StorageUnit;

/**
 * State of the machine's memory managed by the operating system
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class MemState implements State {
    private static String PROC_MEMINFO = "/proc/meminfo";

    private final ProcSysFileReader m_reader;

    private final long[] m_stats;

    private long m_usedKb;

    /**
     * Constructor
     */
    public MemState() {
        try {
            m_reader = new ProcSysFileReader(PROC_MEMINFO);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }

        m_stats = new long[5];

        for (int i = 0; i < m_stats.length; i++) {
            m_stats[i] = 0;
        }
    }

    /**
     * Get the total memory available (in KB)
     */
    public long getTotalKB() {
        return m_stats[0];
    }

    /**
     * Get the amount of free memory (in KB)
     */
    public long getFreeKB() {
        return m_stats[1];
    }

    /**
     * Get the amount of available memory for an application without swapping (in KB)
     */
    public long getAvailableKB() {
        return m_stats[2];
    }

    /**
     * Get the amount of memory used as file buffer (in KB)
     */
    public long getBufferSizeKB() {
        return m_stats[3];
    }

    /**
     * Get the amount of memory used for cache (in KB)
     */
    public long getCacheSizeKB() {
        return m_stats[4];
    }

    /**
     * Get the amount of memory currently used (in KB)
     */
    public long getUsedKB() {
        return m_usedKb;
    }

    /**
     * Get the total memory available (as a StorageUnit object)
     */
    public StorageUnit getTotal() {
        return new StorageUnit(getTotalKB(), StorageUnit.KB);
    }

    /**
     * Get the amount of free memory (as a StorageUnit object)
     */
    public StorageUnit getFree() {
        return new StorageUnit(getFreeKB(), StorageUnit.KB);
    }

    /**
     * Get the amount of available memory for an application without swapping (as a StorageUnit object)
     */
    public StorageUnit getAvailable() {
        return new StorageUnit(getAvailableKB(), StorageUnit.KB);
    }

    /**
     * Get the amount of memory used as file buffer (as a StorageUnit object)
     */
    public StorageUnit getBufferSize() {
        return new StorageUnit(getBufferSizeKB(), StorageUnit.KB);
    }

    /**
     * Get the amount of memory used for cache (as a StorageUnit object)
     */
    public StorageUnit getCacheSize() {
        return new StorageUnit(getCacheSizeKB(), StorageUnit.KB);
    }

    /**
     * Get the amount of memory currently used (as a StorageUnit object)
     */
    public StorageUnit getUsed() {
        return new StorageUnit(getUsedKB(), StorageUnit.KB);
    }

    /**
     * Get the free memory to total memory ratio
     */
    public float getFreeRatio() {
        return ((float) getFreeKB() + getCacheSizeKB() + getBufferSizeKB()) / getTotalKB();
    }

    /**
     * Get the available memory to total memory ratio
     */
    public float getAvailableRatio() {
        return (float) getAvailableKB() / getTotalKB();
    }

    /**
     * Get the buffer memory to total memory ratio
     */
    public float getBufferRatio() {
        return (float) getBufferSizeKB() / getTotalKB();
    }

    /**
     * Get the cache memory to total memory ratio
     */
    public float getCacheRatio() {
        return (float) getCacheSizeKB() / getTotalKB();
    }

    /**
     * Get the used memory to total memory ratio
     */
    public float getUsedRatio() {
        return (float) getUsedKB() / getTotalKB();
    }

    /**
     * Get the free memory to total memory ratio (in percent)
     */
    public float getFreePercent() {
        return getFreeRatio() * 100;
    }

    /**
     * Get the available memory to total memory ratio (in percent)
     */
    public float getAvailablePercent() {
        return getAvailableRatio() * 100;
    }

    /**
     * Get the buffer memory to total memory ratio (in percent)
     */
    public float getBufferPercent() {
        return getBufferRatio() * 100;
    }

    /**
     * Get the cache memory to total memory ratio (in percent)
     */
    public float getCachePercent() {
        return getCacheRatio() * 100;
    }

    /**
     * Get the used memory to total memory ratio (in percent)
     */
    public float getUsedPercent() {
        return getUsedRatio() * 100;
    }

    @Override
    public String toString() {
        return "total " + getTotal() + ", free " + getFree() + ", avail " + getAvailable() + ", buffer size " +
                getBufferSize() + ", cache size " + getCacheSize() + ", used " + getUsed();
    }

    @Override
    public void update() throws StateUpdateException {
        String fileContent;

        try {
            fileContent = m_reader.readCompleteFile();
        } catch (IOException e) {
            throw new StateUpdateException("Can't read file " + PROC_MEMINFO + ": " + e.getMessage());
        }

        StringTokenizer tokenizer = new StringTokenizer(fileContent, "\n");

        for (int i = 0; i < 5; i++) {
            String tmp = tokenizer.nextToken();

            String t = tmp.substring(0, tmp.lastIndexOf(' '));
            m_stats[i] = Long.parseLong(t.substring(t.lastIndexOf(' ') + 1));
        }

        m_usedKb = getTotalKB() - getFreeKB() - getBufferSizeKB() - getCacheSizeKB();
    }

    @Override
    public String generateCSVHeader(final char p_delim) {
        return "total (kb)" + p_delim + "free (kb)" + p_delim + "avail (kb)" + p_delim + "buffer size (kb)" + p_delim +
                "cache size (kb)" + p_delim + "used (kb)" + p_delim + "free percent" + p_delim + "avail percent" +
                p_delim + "buffer percent" + p_delim + "cache percent" + p_delim + "used percent";
    }

    @Override
    public String toCSV(final char p_delim) {
        return "" + getTotalKB() + p_delim + getFreeKB() + p_delim + getAvailableKB() + p_delim + getBufferSizeKB() +
                p_delim + getCacheSizeKB() + p_delim + getUsedKB() + p_delim + getFreePercent() + p_delim +
                getAvailablePercent() + p_delim + getBufferPercent() + p_delim + getCachePercent() + p_delim +
                getUsedPercent();
    }
}
