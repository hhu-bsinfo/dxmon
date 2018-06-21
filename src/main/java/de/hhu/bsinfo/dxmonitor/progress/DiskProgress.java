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

import de.hhu.bsinfo.dxmonitor.state.DiskState;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

/**
 * Progress for a specific disk (HDD/SSD, e.g. sda)
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class DiskProgress implements Progress {

    private DiskState m_currentState;
    private DiskState m_lastState;

    private boolean m_first;
    private long m_lastTimeStamp;
    private long m_currentTimeStamp;
    private long m_totalOpCount;

    private long m_rCount;
    private long m_rBytes;
    private float m_rThroughput;

    private long m_wCount;
    private long m_wBytes;
    private float m_wThroughput;

    public DiskProgress(final String p_name) {
        m_lastState = new DiskState(p_name);
        m_currentState = new DiskState(p_name);
        m_first = true;

        m_currentTimeStamp = System.nanoTime();
        m_lastTimeStamp = m_currentTimeStamp;
    }

    public long getTotalOperationCount() {
        return m_totalOpCount;
    }

    /****** Read Methods ******/

    public float getReadThroughput() {
        return m_rThroughput;
    }

    public long getReadCount() {
        return m_rCount;
    }

    public long getReadBytes() {
        return m_rBytes;
    }

    public float getReadUsage() {
        if (m_totalOpCount <= 0) {
            return 0;
        }
        return ((float)m_rCount) / m_totalOpCount;
    }

    public float getReadUsagePercentage() {
        return getReadUsage() * 100;
    }

    /****** Write Methods ******/

    public float getWriteThroughput() {
        return m_wThroughput;
    }

    public long getWriteCount() {
        return m_wCount;
    }

    public long getWriteBytes() {
        return m_wBytes;
    }

    public float getWriteUsage() {
        if (m_totalOpCount <= 0) {
            return 0;
        }
        return ((float)m_wCount) / m_totalOpCount;
    }

    public float getWriteUsagePercentage() {
        return getWriteUsage() * 100;
    }


    /****** Interface Methods ******/

    @Override
    public void update() throws StateUpdateException {
        DiskState tmp = m_lastState;
        m_lastState = m_currentState;
        m_lastTimeStamp = m_currentTimeStamp;
        m_currentState = tmp;

        if (m_first) {
            m_first = false;
            m_lastState.update();
        }

        m_currentState.update();
        m_currentTimeStamp = System.nanoTime();

        float timeDiff = (m_currentTimeStamp - m_lastTimeStamp)/1000.0f/1000.0f/1000.0f;

        m_rCount = m_currentState.getReadCount() - m_lastState.getReadCount();
        if(m_rCount <= 0) {
            m_rBytes = 0;
            m_rThroughput = 0;
        } else {
            m_rBytes = m_currentState.getReadBytes() - m_lastState.getReadBytes();
            m_rThroughput = m_rBytes / timeDiff;
        }

        m_wCount = m_currentState.getWriteCount() - m_lastState.getWriteCount();
        if(m_wCount <= 0) {
            m_wBytes = 0;
            m_wThroughput = 0;
        } else {
            m_wBytes = m_currentState.getWriteBytes() - m_lastState.getWriteBytes();
            m_wThroughput = m_wBytes / timeDiff;
        }

        m_totalOpCount = m_rCount + m_wCount;
    }

    @Override
    public String generateCSVHeader(char p_delim) {

        return "device" + p_delim + "total_ops" + p_delim + "read_cnt" + p_delim + "write_cnt" + p_delim +
                "read_bytes" + p_delim + "write_bytes" + p_delim + "read_throughput" + p_delim + "write_throughput";

    }

    @Override
    public String toCSV(char p_delim) {
        return m_currentState.getName() + p_delim + m_totalOpCount + p_delim + m_rCount + p_delim + m_wCount + p_delim +
                m_rBytes + p_delim + m_wBytes + p_delim + m_rThroughput + p_delim + m_wThroughput;
    }

    @Override
    public String toString() {
        return String.format("%s (total_ops: %d, read_cnt: %d, write_cnt: %d, read_bytes: %d, write_bytes: %s," +
                "read_throughput: %fB/s, write_throughput: %fB/s", m_currentState.getName(), m_totalOpCount, m_rCount, m_wCount,
                m_rBytes, m_wBytes, m_rThroughput, m_wThroughput);
    }
}
