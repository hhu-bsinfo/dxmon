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

import java.util.Arrays;

import de.hhu.bsinfo.dxmonitor.state.MemState;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;
import de.hhu.bsinfo.dxmonitor.state.SystemState;
import de.hhu.bsinfo.dxmonitor.util.DeviceLister;

/**
 * Get static information about the current instance running (process, jvm, system info etc)
 *
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 17.07.2018
 */
public class InstanceInfo {
    /**
     * Private constructor, utility class
     */
    private InstanceInfo() {

    }

    /**
     * Compile static information about the current instance as a string
     *
     * @return String to print with information about the current instance
     */
    public static String compile() {
        StringBuilder builder = new StringBuilder();

        builder.append("System\n");
        builder.append("  User: ");
        builder.append(SystemState.getUserName());
        builder.append('\n');
        builder.append("  Cwd: ");
        builder.append(SystemState.getCurrentWorkingDirectory());
        builder.append('\n');
        builder.append("  PID: ");
        builder.append(SystemState.getCurrentPID());
        builder.append('\n');
        builder.append("  Kernel: ");
        builder.append(SystemState.getKernelVersion());
        builder.append('\n');
        builder.append("  Distribution: ");
        builder.append(SystemState.getDistribution());
        builder.append('\n');
        builder.append("  Hostname: ");
        builder.append(SystemState.getHostName());
        builder.append('\n');
        builder.append("  Uptime (sec): ");
        builder.append(SystemState.getUptimeSec());
        builder.append('\n');

        builder.append("Java\n");
        builder.append("  Version: ");
        builder.append(JVMInfo.getVersion());
        builder.append('\n');
        builder.append("  VM Name: ");
        builder.append(JVMInfo.getVmName());
        builder.append('\n');
        builder.append("  VM Vendor: ");
        builder.append(JVMInfo.getVmVendor());
        builder.append('\n');
        builder.append("  VM Version: ");
        builder.append(JVMInfo.getVmVersion());
        builder.append('\n');

        builder.append("Hardware\n");

        try {
            MemState memState = new MemState();
            builder.append("  Memory: ");
            memState.update();
            builder.append(memState);
            builder.append('\n');
        } catch (StateUpdateException ignored) {
        }

        builder.append("  NICs: ");
        builder.append(Arrays.toString(DeviceLister.getNICs().toArray()));
        builder.append('\n');
        builder.append("  Disks: ");
        builder.append(Arrays.toString(DeviceLister.getDisks().toArray()));

        return builder.toString();
    }
}
