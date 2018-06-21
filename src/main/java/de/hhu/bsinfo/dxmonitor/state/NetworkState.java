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

import java.io.IOException;
import java.util.StringTokenizer;

import de.hhu.bsinfo.dxmonitor.util.ProcSysLineReader;
import de.hhu.bsinfo.dxmonitor.util.ProcSysFileReader;
import de.hhu.bsinfo.dxutils.unit.StorageUnit;

/**
 * State of a network interface (e.g. eth0)
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class NetworkState implements State {
    private static final String PROC_NET_DEV = "/proc/net/dev";

    private final ProcSysLineReader m_reader;

    private final String m_name;
    private final long m_maxBandwidthMbitsPerSec;

    private long[] m_receiveStats;
    private long[] m_transmitStats;

    /**
     * Constructor
     *
     * @param p_name
     *     name of the nic (e.g. eth0)
     */
    public NetworkState(final String p_name) {
        try {
            m_reader = new ProcSysLineReader(PROC_NET_DEV);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        if (p_name.length() < 1) {
            m_name = "lo";
        } else {
            m_name = p_name;
        }

        m_maxBandwidthMbitsPerSec = getSpeed(p_name);

        m_receiveStats = new long[4]; // bytes - packets - errs - drop
        m_transmitStats = new long[5]; // bytes - packets - errs - drop - collisions
    }

    /**
     * Get the name of the nic
     */
    public String getName() {
        return m_name;
    }

    /**
     * Get the max bandwidth of the nic in mbits/sec
     */
    public long getMaxBandwidthMbitsPerSec() {
        return m_maxBandwidthMbitsPerSec;
    }

    /**
     * Returns the total number of received bytes
     */
    public long getRxBytes() {
        return m_receiveStats[0];
    }

    /**
     * Returns the total number of received packets
     */
    public long getRxPackets() {
        return m_receiveStats[1];
    }

    /**
     * Returns the number of received faulty packets
     */
    public long getRxErrors() {
        return m_receiveStats[2];
    }

    /**
     * Returns the number of dropped packets
     */
    public long getRxDrops() {
        return m_receiveStats[3];
    }

    /**
     * Returns the number of transmitted bytes
     */
    public long getTxBytes() {
        return m_transmitStats[0];
    }

    /**
     * Returns the number of transmitted packets
     */
    public long getTxPackets() {
        return m_transmitStats[1];
    }

    /**
     * Returns the number of transmitted faulty packets
     */
    public long getTxErrors() {
        return m_transmitStats[2];
    }

    /**
     * Returns the number of transmitted dropped packets
     */
    public long getTxDrops() {
        return m_transmitStats[3];
    }

    /**
     * Get the max bandwidth in StorageUnit (byte, kb, mb, ...) per second
     */
    public StorageUnit getMaxBandwidthPerSec() {
        return new StorageUnit((getMaxBandwidthMbitsPerSec() / 8) * 1024 * 1024, StorageUnit.BYTE);
    }

    /**
     * Get the total number of bytes received as a StorageUnit object
     */
    public StorageUnit getRx() {
        return new StorageUnit(getRxBytes(), StorageUnit.BYTE);
    }

    /**
     * Get the total number of bytes transferred as a StorageUnit object
     */
    public StorageUnit getTx() {
        return new StorageUnit(getTxBytes(), StorageUnit.BYTE);
    }

    @Override
    public String toString() {
        return m_name + " (" + getMaxBandwidthMbitsPerSec() + " mbits/sec, " + getMaxBandwidthPerSec() + "/sec): rx " +
                getRx() + ", rx packets " + getRxPackets() + ", rx errors " + getRxErrors() + ", rx drops " +
                getRxDrops() + ", tx " + getTx() + ", tx packets " + getTxPackets() + ", tx errors " + getTxErrors() +
                ", tx drops " + getTxDrops();
    }

    @Override
    public void update() throws StateUpdateException {
        String output = null;

        try {
            m_reader.reset();

            // skip headers
            m_reader.readLine();
            m_reader.readLine();

            String tmp;

            while ((tmp = m_reader.readLine()) != null) {
                int idx = tmp.indexOf(':');

                if (idx != -1 && tmp.substring(0, idx).contains(m_name)) {
                    output = tmp;
                    break;
                }
            }
        } catch (IOException e) {
            throw new StateUpdateException("Can't read file " + PROC_NET_DEV + ": " + e.getMessage());
        }

        if (output == null) {
            throw new StateUpdateException("Can't find network device " + m_name);
        }

        StringTokenizer tokenizer = new StringTokenizer(output, " ");
        tokenizer.nextToken();

        for (int i = 0; i < 8; i++) {
            if (i < 4) {
                m_receiveStats[i] = Long.parseLong(tokenizer.nextToken());
            } else {
                tokenizer.nextToken();
            }
        }

        for (int i = 0; i < 5; i++) {
            m_transmitStats[i] = Long.parseLong(tokenizer.nextToken());
        }
    }

    @Override
    public String generateCSVHeader(final char p_delim) {
        return "device" + p_delim + "bw max mbits/sec" + p_delim + "bw max mbytes/sec" + p_delim + "rx bytes" +
                p_delim + "rx packets" + p_delim + "rx errors" + p_delim + "rx drops" + p_delim + "tx bytes" + p_delim +
                "tx packets" + p_delim + "tx errors" + p_delim + "tx drops";
    }

    @Override
    public String toCSV(final char p_delim) {
        return m_name + p_delim + getMaxBandwidthMbitsPerSec() + p_delim + getMaxBandwidthPerSec().getMB() + p_delim +
                getRxBytes() + p_delim + getRxPackets() + p_delim + getRxErrors() + p_delim + getRxDrops() + p_delim +
                getTxBytes() + p_delim + getTxPackets() + p_delim + getTxErrors() + p_delim + getTxDrops();
    }

    /**
     * Reads the maximum speed in mbits/sec of the nic from the /sys/class/net/NIC/speed file.
     *
     * @param p_name Name of the nic
     * @return Speed of the NIC in mbits/sec
     */
    private static long getSpeed(final String p_name) {
        if (p_name.charAt(0) == 'e') {
            String string;

            try {
                string = ProcSysFileReader.readCompleteFileOnce("/sys/class/net/" + p_name + "/speed");
            } catch (IOException e) {
                throw new RuntimeException("Reading net speed of " + p_name + " failed: " + e.getMessage());
            }

            int index = 0;

            while (Character.isDigit(string.charAt(index))) {
                index++;
            }

            // value is in mbits/s -> convert byte / sec
            return Long.parseLong(string.substring(0, index));
        } else if (p_name.charAt(0) == 'w') {
            // wireless interface, unsupported
            return 0;
        } else if (p_name.charAt(0) == 'i') {
            //infiniband interface, unsupported
            return 0;
        } else {
            // unknown
            return 0;
        }
    }
}
