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

import de.hhu.bsinfo.dxmonitor.util.ProcSysLineReader;
import de.hhu.bsinfo.dxmonitor.util.ProcSysFileReader;
import de.hhu.bsinfo.dxutils.unit.StorageUnit;

/**
 * State of a specific disk (HDD/SSD)
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class DiskState implements State {
    private static final String PROC_DISKSTATS = "/proc/diskstats";

    private final ProcSysLineReader m_reader;

    private final String m_name;
    private final long m_sectorSizeBytes;
    private final long m_totalSizeBytes;

    private long m_readCount;
    private long m_readSectors;
    private long m_writeCount;
    private long m_writeSectors;

    /**
     * Constructor
     *
     * @param p_name
     *     Name of the disk (e.g. sda)
     */
    public DiskState(final String p_name) {
        try {
            m_reader = new ProcSysLineReader(PROC_DISKSTATS);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        m_name = p_name;

        m_sectorSizeBytes = getSectorSize(m_name);
        m_totalSizeBytes = getDiskSizeSectors(m_name) * m_sectorSizeBytes;
    }

    /**
     * Get the name of the disk (e.g. sda)
     */
    public String getName() {
        return m_name;
    }

    /**
     * Get the sector size in bytes
     */
    public long getSectorSizeBytes() {
        return m_sectorSizeBytes;
    }

    /**
     * Get the total disk size in bytes
     */
    public long getTotalSizeBytes() {
        return m_totalSizeBytes;
    }

    /**
     * Returns the number of successful read-operations on this disk.
     */
    public long getReadCount() {
        return m_readCount;
    }

    /**
     * Returns the number of  read sector from this disk.
     */
    public long getReadSectors() {
        return m_readSectors;
    }

    /**
     * Returns the number of bytes that have been read from this disk
     */
    public long getReadBytes() {
        return m_readSectors * m_sectorSizeBytes;
    }

    /**
     * Returns the number of successful write-operations on this disk.
     */
    public long getWriteCount() {
        return m_writeCount;
    }

    /**
     * Returns the number of write sector from this disk.
     */
    public long getWriteSectors() {
        return m_writeSectors;
    }

    /**
     * Returns the number of bytes that have been written from this disk
     */
    public long getWriteBytes() {
        return m_writeSectors * m_sectorSizeBytes;
    }

    /**
     * Get the sector size of the disk as a StorageUnit object
     */
    public StorageUnit getSectorSize() {
        return new StorageUnit(m_sectorSizeBytes, StorageUnit.BYTE);
    }

    /**
     * Get the total size of the disk as a StorageUnit object
     */
    public StorageUnit getTotalSize() {
        return new StorageUnit(m_totalSizeBytes, StorageUnit.BYTE);
    }

    /**
     * Get total amount of data read so far as a StorageUnit object
     */
    public StorageUnit getRead() {
        return new StorageUnit(getReadBytes(), StorageUnit.BYTE);
    }

    /**
     * Get total amount of data written so far as a StorageUnit object
     */
    public StorageUnit getWrite() {
        return new StorageUnit(getWriteBytes(), StorageUnit.BYTE);
    }

    @Override
    public String toString() {
        return m_name + ": sector size bytes " + m_sectorSizeBytes + ", total size bytes " + m_totalSizeBytes +
                ", read count " + m_readCount + ", read sectors " + m_readSectors + ", read " + getRead() +
                ", write count " + m_writeCount + ", write sectors " + m_writeSectors + ", write " + getWrite();
    }

    @Override
    public void update() throws StateUpdateException {
        String output = null;

        try {
            String tmp;

            while ((tmp = m_reader.readLine()) != null) {
                if (tmp.contains(m_name)) {
                    output = tmp;
                    break;
                }
            }

            m_reader.reset();
        } catch (IOException e) {
            throw new StateUpdateException("Can't read file " + PROC_DISKSTATS + ": " + e.getMessage());
        }

        if (output == null) {
            throw new StateUpdateException("Could not find disk name " + m_name + " in " + PROC_DISKSTATS);
        }

        String[] fileInput = output.substring(output.indexOf(m_name)).split(" ");

        m_readCount = Long.parseLong(fileInput[1]);
        m_readSectors = Long.parseLong(fileInput[3]);
        m_writeCount = Long.parseLong(fileInput[5]);
        m_writeSectors = Long.parseLong(fileInput[7]);
    }

    @Override
    public String generateCSVHeader(final char p_delim) {
        return "disk" + p_delim + "sector size bytes" + p_delim + "total size bytes" + p_delim + "read count" +
                p_delim + "read sectors" + p_delim + "read bytes" + p_delim + "write count" + p_delim +
                "write sectors" + p_delim + "write bytes";
    }

    @Override
    public String toCSV(final char p_delim) {
        return m_name + p_delim + m_sectorSizeBytes + p_delim + m_totalSizeBytes + p_delim + m_readCount + p_delim +
                m_readSectors + p_delim + getReadBytes() + p_delim + m_writeCount + p_delim + m_writeSectors +
                p_delim + getWriteBytes();
    }

    /**
     * Reads the total disk size from the /sys/block/DISKNAME/size file.
     *
     * @param p_name
     *      Name of the disk (e.g. sda)
     * @return Total disk size in bytes
     */
    private static long getDiskSizeSectors(final String p_name) {
        String tmp = null;

        try {
            tmp = ProcSysFileReader.readCompleteFileOnce("/sys/block/" + p_name + "/size");
        } catch (IOException ignore) {

        }

        if (tmp == null) {
            int index = 0;

            while (Character.isAlphabetic(p_name.charAt(index))) {
                index++;
            }

            try {
                tmp = ProcSysFileReader.readCompleteFileOnce("/sys/block/" + p_name.substring(0, index) + '/' +
                        p_name + "/size");
            } catch (IOException e) {
                throw new RuntimeException("Can't read disk size sectors of disk " + p_name + "failed: " +
                        e.getMessage());
            }
        }

        int index = 0;

        while (Character.isDigit(tmp.charAt(index++))) {

        }

        return Long.parseLong(tmp.substring(0, index - 1));
    }

    /**
     * Reads the sector size from the /sys/block/DISKNAME/queue/hw_sector_size file.
     *
     * @param p_name
     *      Name of the disk (e.g. sda)
     * @return Size of a sector in bytes
     */
    private static long getSectorSize(final String p_name) {
        String tmp = null;

        try {
            tmp = ProcSysFileReader.readCompleteFileOnce("/sys/block/" + p_name + "/queue/hw_sector_size");
        } catch (IOException ignore) {

        }

        if (tmp == null) {
            int index = 0;

            while (Character.isAlphabetic(p_name.charAt(index))) {
                index++;
            }

            try {
                tmp = ProcSysFileReader.readCompleteFileOnce("/sys/block/" + p_name.substring(0, index) +
                        "/queue/hw_sector_size");
            } catch (IOException e) {
                throw new RuntimeException("Can't read sector size of disk " + p_name + ": " + e.getMessage());
            }
        }

        int index = 0;

        while (Character.isDigit(tmp.charAt(index++))) {

        }

        return Long.parseLong(tmp.substring(0, index - 1));
    }
}
