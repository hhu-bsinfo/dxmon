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

package de.hhu.bsinfo.dxmonitor.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Helper class to (efficiently) read files from /proc, /sys or similar locations.
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public final class ProcSysFileReader {
    private final FileChannel m_fileChannel;
    private final ByteBuffer m_buffer;
    private final byte[] m_bufferArray;

    /**
     * Constructor
     *
     * @param p_path Path of file to read
     * @throws FileNotFoundException If file does not exist
     */
    public ProcSysFileReader(final String p_path) throws FileNotFoundException {
        FileInputStream fileInputStream;

        fileInputStream = new FileInputStream(p_path);
        m_fileChannel = fileInputStream.getChannel();
        m_buffer = ByteBuffer.allocateDirect(8192);
        m_bufferArray = new byte[4096];
    }

    /**
     * Read the target file, once. Use this static function if you don't have to read
     * the same file repeatedly.
     *
     * @param p_path Path of file to read
     * @return Contents of the file read as String
     * @throws IOException If either the file is not found or reading the file failed
     */
    public static String readCompleteFileOnce(final String p_path) throws IOException {
        StringBuilder builder = new StringBuilder("");

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(p_path);
            FileChannel channel = fileInputStream.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(8192);
            byte[] barray = new byte[4096];

            int nRead;
            int nGet;

            while ((nRead = channel.read(buffer)) != -1) {
                if (nRead == 0) {
                    continue;
                }

                buffer.position(0);
                buffer.limit(nRead);

                while (buffer.hasRemaining()) {
                    nGet = Math.min(buffer.remaining(), 4096);
                    buffer.get(barray, 0, nGet);
                    builder.append(new String(barray, 0, nGet));
                }

                buffer.clear();
            }
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }

        return builder.toString();
    }

    /**
     * Read the contents of the file
     *
     * @return Contents of the file read as String
     * @throws IOException If reading the file failed
     */
    public String readCompleteFile() throws IOException {
        StringBuilder builder = new StringBuilder("");
        int nRead;
        int nGet;

        m_fileChannel.position(0);

        while ((nRead = m_fileChannel.read(m_buffer)) != -1) {
            if (nRead == 0) {
                continue;
            }

            m_buffer.position(0);
            m_buffer.limit(nRead);

            while (m_buffer.hasRemaining()) {
                nGet = Math.min(m_buffer.remaining(), 4096);
                m_buffer.get(m_bufferArray, 0, nGet);

                // seems like we can't avoid copying twice and using new String is faster than encoding chars
                builder.append(new String(m_bufferArray, 0, nGet));
            }

            m_buffer.clear();
        }

        return builder.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        m_fileChannel.close();
    }
}
