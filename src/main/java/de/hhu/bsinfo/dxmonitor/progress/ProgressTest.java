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

import de.hhu.bsinfo.dxmonitor.state.CpuState;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

/**
 * Test for testing various progress classes
 *
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class ProgressTest {
    /**
     * Entry point
     *
     * @param p_args Cmd args
     */
    public static void main(final String[] p_args) {
        testProgressCpuCore(1000, 5);
        testProgressCpu(500, 10);
    }

    /**
     * Test case for cpu progress
     *
     * @param p_intervalMs Update call interval in ms
     * @param p_timeFrameSec Total time to run the test in seconds
     */
    private static void testProgressCpu(final int p_intervalMs, final int p_timeFrameSec) {
        testProgress(new CpuProgress(), p_intervalMs, p_timeFrameSec, "testProgressCpu");
    }

    /**
     * Common test "interface" for various cases
     *
     * @param p_progress Progress instance to test
     * @param p_intervalMs Update call interval in ms
     * @param p_timeFrameSec Total time to run the test in seconds
     * @param p_name Name of the test
     */
    private static void testProgress(final Progress p_progress, final int p_intervalMs, final int p_timeFrameSec,
            final String p_name) {
        System.out.println("================================================");
        System.out.println(p_name);
        System.out.println("For " + p_timeFrameSec + " seconds, print every " + p_intervalMs + " ms");

        long end = System.currentTimeMillis() + p_timeFrameSec * 1000;

        while (System.currentTimeMillis() < end) {
            try {
                p_progress.update();
            } catch (StateUpdateException e) {
                e.printStackTrace();
            }

            System.out.println(p_progress);

            try {
                Thread.sleep(p_intervalMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Specific test for testing CpuCoreProgress
     *
     * @param p_intervalMs Update call interval in ms
     * @param p_timeFrameSec Total time to run the test in seconds
     */
    private static void testProgressCpuCore(final int p_intervalMs, final int p_timeFrameSec) {
        System.out.println("================================================");
        System.out.println("testProgressCpuCore");
        System.out.println("For " + p_timeFrameSec + " seconds, print every " + p_intervalMs + " ms");

        CpuCoreProgress[] cpu = new CpuCoreProgress[CpuState.getTotalCores()];

        for (int i = 0; i < cpu.length; i++) {
            cpu[i] = new CpuCoreProgress(i);
        }

        long end = System.currentTimeMillis() + p_timeFrameSec * 1000;

        while (System.currentTimeMillis() < end) {
            for (CpuCoreProgress c : cpu) {
                try {
                    c.update();
                } catch (StateUpdateException e) {
                    e.printStackTrace();
                }

                System.out.println(c);
            }

            try {
                Thread.sleep(p_intervalMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
