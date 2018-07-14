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

import de.hhu.bsinfo.dxmonitor.state.NetworkState;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

/**
 * Progress for a specific network interface (e.g. eth0)
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class NetworkProgress implements Progress {
    private NetworkState m_lastState;
    private NetworkState m_currentState;

    private long m_lastTimeStamp;
    private long m_currentTimeStamp;
    private boolean m_first;

    private float m_rThroughput;
    private long m_rBytes;
    private long m_rPacket;
    private long m_rError;
    private long m_rDrop;

    private float m_tThroughput;
    private long m_tBytes;
    private long m_tPacket;
    private long m_tError;
    private long m_tDrop;

    /**
     * Constructor
     * @param p_name NIC Identifier
     */
    public NetworkProgress(final String p_name) {
        m_lastState = new NetworkState(p_name);
        m_currentState = new NetworkState(p_name);
        m_first = true;

        m_currentTimeStamp = System.nanoTime();
        m_lastTimeStamp = m_currentTimeStamp;
    }

    /**
     * Amount of received bytes.
     * @return received byte count
     */
    public long getReceiveBytes() {
        return m_rBytes;
    }

    /**
     * Return the "current" receive Throughput.
     * @return Receive Throughput
     */
    public float getReceiveThroughput() {
        return m_rThroughput;
    }

    /**
     * Returns amount of received packets.
     * @return Receive packet count
     */
    public long getReceivePacketCount() {
        return m_rPacket;
    }

    /**
     * Returns amount of received packets which were faulty.
     * @return received but faulty packet count
     */
    public long getReceivePacketErrorCount() {
        return m_rError;
    }

    /**
     * Returns amount of received packet which were dropped.
     * @return received but dropped packet count
     */
    public long getReceivePacketDropCount() {
        return m_rDrop;
    }

    /**
     * Returns amount of received packets which were correct.
     * @return (successfully) received packet count
     */
    public long getReceivePacketSuccessCount() {
        return m_rPacket - (m_rError + m_rDrop);
    }

    /**
     * Returns Receive Error Usage.
     * @return Receive Error Usage
     */
    public float getReceiveErrorUsage() {
        return ((float) getReceivePacketErrorCount()) / getReceivePacketCount();
    }

    /**
     * Returns Receive Drop Usage.
     * @return Receive Drop Usage
     */
    public float getReceiveDropUsage() {
        return ((float) getReceivePacketDropCount()) / getReceivePacketCount();
    }

    /**
     * Returns Receive Success Usage.
     * @return Receive Success Usage
     */
    public float getReceiveSuccessUsage() {
        return ((float) getReceivePacketSuccessCount()) / getReceivePacketCount();
    }

    /**
     * Returns Receive Error Usage in percent.
     * @return Receive Error Usage in percent
     */
    public float getReceiveErrorUsagePercent() {
        return getReceiveErrorUsage() * 100;
    }

    /**
     * Returns Receive Drop Usage in percent.
     * @return Receive Drop Usage in percent
     */
    public float getReceiveDropUsagePercent() {
        return getReceiveDropUsage() * 100;
    }

    /**
     * Returns Receive Success Usage in percent.
     * @return Receive Success Usage in percent
     */
    public float getReceiveSuccessUsagePercent() {
        return getReceiveSuccessUsage() * 100;
    }

    /**
     * Amount of transmitted bytes.
     * @return transmitted byte count
     */
    public long getTransmitBytes() {
        return m_tBytes;
    }

    /**
     * Return the "current" transmit Throughput.
     * @return Transmit Throughput
     */
    public float getTransmitThroughput() {
        return m_tThroughput;
    }

    /**
     * Return the amount of transmitted packets.
     * @return send packet count
     */
    public long getTransmitPacketCount() {
        return m_tPacket;
    }


    /**
     * Returns amount of transmitted packets which were faulty.
     * @return transmitted but faulty packet count
     */
    public long getTransmitPacketErrorCount() {
        return m_tError;
    }

    /**
     * Returns amount of transmitted packets which were dropped.
     * @return transmitted but dropped packet count
     */
    public long getTransmitPacketDropCount() {
        return m_tDrop;
    }

    /**
     * Returns amount of transmitted packets which were successful.
     * @return successfully transmitted packet count
     */
    public long getTransmitPacketSuccessCount() {
        return m_tPacket - (m_tError + m_tDrop);
    }

    /**
     * Returns Transmit Error Usage.
     * @return Transmit Error Usage
     */
    public float getTransmitErrorUsage() {
        return ((float) getTransmitPacketErrorCount()) / getTransmitPacketCount();
    }

    /**
     * Returns Transmit Drop Usage.
     * @return Transmit Drop Usage
     */
    public float getTransmitDropUsage() {
        return ((float) getTransmitPacketDropCount()) / getTransmitPacketCount();
    }

    /**
     * Returns Transmit Success Usage.
     * @return Transmit Success Usage
     */
    public float getTransmitSuccessUsage() {
        return ((float) getTransmitPacketSuccessCount()) / getTransmitPacketCount();
    }

    /**
     * Returns Transmit Error Usage in percent.
     * @return Transmit Error Usage in percent.
     */
    public float getTransmitErrorUsagePercent() {
        return getTransmitErrorUsage() * 100;
    }


    /**
     * Returns Transmit Drop Usage in percent.
     * @return Transmit Drop Usage in percent.
     */
    public float getTransmitDropUsagePercent() {
        return getTransmitDropUsage() * 100;
    }

    /**
     * Returns Transmit Success Usage in percent.
     * @return Transmit Success Usage in percent.
     */
    public float getTransmitSuccessUsagePercent() {
        return getTransmitSuccessUsage() * 100;
    }

    @Override
    public void update() throws StateUpdateException {
        NetworkState tmp = m_lastState;
        m_lastState = m_currentState;
        m_lastTimeStamp = m_currentTimeStamp;
        m_currentState = tmp;

        if (m_first) {
            m_first = false;
            m_lastState.update();
        }

        m_currentState.update();
        m_currentTimeStamp = System.nanoTime();

        float timeDiff = (m_currentTimeStamp - m_lastTimeStamp)/1000.0f/1000.0f;

        m_rBytes = m_currentState.getRxBytes() - m_lastState.getRxBytes();
        if (m_rBytes <= 0) {
            m_rPacket = 0;
            m_rThroughput = 0;
            m_rError = 0;
            m_rDrop = 0;
        } else {
            m_rThroughput = m_rBytes / timeDiff;
            m_rPacket = m_currentState.getRxPackets() - m_lastState.getRxPackets();
            m_rError = m_currentState.getRxErrors() - m_lastState.getRxErrors();
            m_rDrop = m_currentState.getRxDrops() - m_lastState.getRxDrops();
        }

        m_tBytes = m_currentState.getTxBytes() - m_lastState.getTxBytes();
        if (m_tBytes <= 0) {
            m_tPacket = 0;
            m_tThroughput = 0;
            m_tError = 0;
            m_tDrop = 0;
        } else {
            m_tThroughput = m_tBytes / timeDiff;
            m_tPacket = m_currentState.getTxPackets() - m_lastState.getTxPackets();
            m_tError = m_currentState.getTxErrors() - m_lastState.getTxErrors();
            m_tDrop = m_currentState.getTxDrops() - m_lastState.getTxDrops();
        }
    }

    @Override
    public String generateCSVHeader(char p_delim) {
        return "device" + p_delim + "rThroughput" + p_delim + "rPackets" + p_delim + "rError" + p_delim +
                "rDrop" + p_delim + "rSuccess" + p_delim + "tThroughput" + p_delim + "tPackets" + p_delim +
                "tError" + p_delim + "tDrop" + p_delim + "tSuccess";
    }

    @Override
    public String toCSV(char p_delim) {
        return m_currentState.getName() + p_delim + m_rThroughput + p_delim + m_rPacket + p_delim + m_rError +
                p_delim + m_rDrop + p_delim + getReceivePacketSuccessCount() + p_delim + m_tThroughput + p_delim +
                p_delim + m_tPacket + p_delim + m_tError + p_delim + m_tDrop + p_delim + getTransmitPacketSuccessCount();
    }

    @Override
    public String toString() {
        return String.format("%s (rThroughput: %f, rPackets: %d, rError: %d, rDrop: %d, rSuccess: %d," +
                "tThroughput: %f, tPackets: %d, tError: %d, tDrop: %d, tSuccess: %d)",
                m_currentState.getName(), m_rThroughput, m_rPacket, m_rError,  m_rDrop, getReceivePacketSuccessCount(),
                m_tThroughput, m_tPacket, m_tError, m_tDrop, getTransmitPacketSuccessCount());
    }
}
