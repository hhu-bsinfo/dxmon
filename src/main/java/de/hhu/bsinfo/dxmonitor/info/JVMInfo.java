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

package de.hhu.bsinfo.dxmonitor.info;

import java.lang.management.ManagementFactory;

/**
 * Get some static information about the current JVM.
 *
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 17.07.2018
 */
public final class JVMInfo {
    /**
     * Private constructor, utility class
     */
    private JVMInfo() {

    }

    /**
     * Get the java version
     *
     * @return Java version
     */
    public static String getVersion() {
        return System.getProperty("java.version");
    }

    /**
     * Get the name of the JVM (e.g. OpenJDK)
     *
     * @return JVM name
     */
    public static String getVmName() {
        return ManagementFactory.getRuntimeMXBean().getVmName();
    }

    /**
     * Get the JVM vendor (e.g. Oracle)
     *
     * @return JVM vendor
     */
    public static String getVmVendor() {
        return ManagementFactory.getRuntimeMXBean().getVmVendor();
    }

    /**
     * Get the version of the VM
     *
     * @return VM version
     */
    public static String getVmVersion() {
        return ManagementFactory.getRuntimeMXBean().getVmVersion();
    }
}
