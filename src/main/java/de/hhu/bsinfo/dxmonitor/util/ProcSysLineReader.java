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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Helper class to (efficiently) read one or multiple lines from files such as /proc/diskstats or similar locations.
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class ProcSysLineReader {
    private final FileInputStream m_fileInputStream;
    private final BufferedReader m_reader;

    /**
     * Constructor
     *
     * @param p_path Path of file to read
     * @throws FileNotFoundException If the target file does not exist
     */
    public ProcSysLineReader(final String p_path) throws FileNotFoundException {
        m_fileInputStream = new FileInputStream(p_path);
        m_reader = new BufferedReader(new InputStreamReader(m_fileInputStream));
    }

    /**
     * Read the next line (of text) from the file
     *
     * @return The read line
     * @throws IOException If reading failed
     */
    public String readLine() throws IOException {
        return m_reader.readLine();
    }

    /**
     * Reset reading of the file to the beginning of it
     *
     * @throws IOException If resetting the position failed
     */
    public void reset() throws IOException {
        m_fileInputStream.getChannel().position(0);
    }

    @Override
    protected void finalize() throws Throwable {
        m_reader.close();
    }
}
