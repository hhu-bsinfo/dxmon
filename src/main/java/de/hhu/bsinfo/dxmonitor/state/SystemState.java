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
import de.hhu.bsinfo.dxutils.unit.TimeUnit;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Various system related states that are not targeted towards a specific piece of hardware/software
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public final class SystemState {
    private static final String PROC_UPTIME = "/proc/uptime";
    private static final String PROC_NET = "/proc/net/dev";
    private static final String PROC_DISK = "/proc/partitions";

    private static String ms_kernelVersion;
    private static String ms_distribution;
    private static String ms_cwd;
    private static String ms_hostName;
    private static String ms_userName;

    static {
        try {
            ms_hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        ms_cwd = System.getProperty("user.dir");
        ms_distribution = System.getProperty("os.name");
        ms_kernelVersion = System.getProperty("os.version");
        ms_userName = System.getProperty("user.name");
    }

    /**
     * Get the kernel version of the current system
     */
    public static String getKernelVersion() {
        return ms_kernelVersion;
    }

    /**
     * Get the name of the current (Linux) distribution
     */
    public static String getDistribution() {
        return ms_distribution;
    }

    /**
     * Get the current working directory
     */
    public static String getCurrentWorkingDirectory() {
        return ms_cwd;
    }

    /**
     * Get the host name of the current machine
     */
    public static String getHostName() {
        return ms_hostName;
    }

    /**
     * Get the name of the user executing this program
     */
    public static String getUserName() {
        return ms_userName;
    }

    /**
     * Get the time since boot in seconds
     */
    public static float getUptimeSec() {
        String tmp;

        try {
            tmp = ProcSysFileReader.readCompleteFileOnce(PROC_UPTIME);
        } catch (IOException e) {
            return Float.NEGATIVE_INFINITY;
        }

        return Float.parseFloat(tmp.substring(0, tmp.indexOf(' ')));
    }

    public static ArrayList<String> getNICs() {
        ArrayList<String> nics = new ArrayList<>();
        String[] tmp;

        try {
            tmp = ProcSysFileReader.readCompleteFileOnce(PROC_NET).split("\n");
            for (int i = 2; i < tmp.length; i++) {
                nics.add(tmp[i].substring(0, tmp[i].indexOf(':')).trim());
            }
        } catch (IOException ignore) {}

        return nics;
    }

    public static ArrayList<String> getDisks() {
        ArrayList<String> nics = new ArrayList<>();
        String[] tmp;

        try {
            tmp = ProcSysFileReader.readCompleteFileOnce(PROC_DISK).split("\n");
            for (int i = 2; i < tmp.length; i++) {
                nics.add(tmp[i].substring(tmp[i].lastIndexOf(' ')).trim());
            }
        } catch (IOException ignore) {}

        return nics;
    }

    /**
     * Get the time since boot as a TimeUnit object
     */
    public static TimeUnit getUptime() {
        return new TimeUnit((long) getUptimeSec(), TimeUnit.SEC);
    }

    /**
     * Create a string with all SystemState information
     */
    public static String toStr() {
        return ms_userName + "@" + ms_hostName + " on " + ms_distribution + ", Kernel " + ms_kernelVersion +
                ", Uptime: " + getUptime() + ", Cwd " + ms_cwd;
    }
}
