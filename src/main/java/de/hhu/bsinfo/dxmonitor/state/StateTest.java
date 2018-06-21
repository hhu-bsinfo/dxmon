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

/**
 * Test for testing the various state classes
 *
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class StateTest {
    /**
     * Main entry point
     *
     * @param p_args Cmd args
     */
    public static void main(final String[] p_args) {
        testSystemState();
        testCpuCoreState(1000);
        testCpuState(1000);
        testDiskState(1000);
        testNetworkState(1000);
        testJVMMem();
    }

    /**
     * Test case for JVMMemState
     */
    private static void testJVMMem() {
        testState(new JVMMemState(), 1, "testJVMMem");
    }

    /**
     * Test case for NetworkState
     */
    private static void testNetworkState(final int p_benchmarkCount) {
        testState(new NetworkState("eth0"), p_benchmarkCount, "testNetworkState");
    }

    /**
     * Test case for DiskState
     */
    private static void testDiskState(final int p_benchmarkCount) {
        // sda should be available on any machine
        testState(new DiskState("sda"), p_benchmarkCount, "testDiskState");
    }

    /**
     * Test case for CpuState
     */
    private static void testCpuState(final int p_benchmarkCount) {
        testState(new CpuState(), p_benchmarkCount, "testCpuState");
    }

    /**
     * Common test "interface" for various cases
     *
     * @param p_state State to test
     * @param p_benchmarkCount Number of times to run the update method (for time measuring)
     * @param p_name Name of the test
     */
    private static void testState(final State p_state, final int p_benchmarkCount, final String p_name) {
        System.out.println("================================================");
        System.out.println(p_name);

        long start = System.nanoTime();

        for (int i = 0; i < p_benchmarkCount; i++) {
            try {
                p_state.update();
            } catch (StateUpdateException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("State update benchmark, avg: %f ms\n", ((System.nanoTime() - start) / p_benchmarkCount /
                1000.0 / 1000.0));

        System.out.println("State:");
        System.out.println(p_state);

        System.out.println("State CSV:");

        System.out.println(p_state.generateCSVHeader(';'));
        System.out.println(p_state.toCSV(';'));
    }

    /**
     * Test case for CpuCoreState
     *
     * @param p_benchmarkCount Number of times to run the update method (for time measuring)
     */
    private static void testCpuCoreState(final int p_benchmarkCount) {
        System.out.println("================================================");
        System.out.println("testCpuCoreState");

        int cores = CpuState.getTotalCores();

        System.out.println("Cores: " + cores);

        CpuCoreState[] states = new CpuCoreState[cores];

        for (int i = 0; i < cores; i++) {
            states[i] = new CpuCoreState(i);
        }

        long start = System.nanoTime();

        for (int i = 0; i < p_benchmarkCount; i++) {
            for (CpuCoreState state : states) {
                try {
                    state.update();
                } catch (StateUpdateException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.printf("State update benchmark, avg: %f ms\n", ((System.nanoTime() - start) / p_benchmarkCount *
                cores / 1000.0 / 1000.0));

        System.out.println("CPU States:");

        for (CpuCoreState state : states) {
            System.out.println(state);
        }

        System.out.println("CPU States CSV:");
        System.out.println(states[0].generateCSVHeader(';'));

        for (CpuCoreState state : states) {
            System.out.println(state.toCSV(';'));
        }
    }

    /**
     * Test case for SystemState
     */
    private static void testSystemState() {
        System.out.println("================================================");
        System.out.println("testSystemState");

        System.out.println(SystemState.toStr());
    }
}
