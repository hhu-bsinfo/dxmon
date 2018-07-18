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

package de.hhu.bsinfo.dxmonitor.monitor;

import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

/**
 * Test for testing various monitoring classes
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 20.02.2018
 */
public class MonitorTest {
    /**
     * Entry point
     *
     * @param p_args Cmd args
     */
    public static void main(final String[] p_args) {
        MonitorTest test = new MonitorTest();
        test.run();
    }

    private final CpuMonitor m_cpuMonitor;
    private final MemMonitor m_memMonitor;

    /**
     * Constructor
     */
    public MonitorTest() {
        m_cpuMonitor = new CpuMonitor();
        m_memMonitor = new MemMonitor();

        //m_cpuMonitor.addThresholdCpuUsagePercent(new ThresholdDouble("CpuUsage1", 10.0, true,
        //        this::callbackCpuUsageThresholdExceed));
        //m_cpuMonitor.addThresholdCpuUsagePercent(new ThresholdDouble("CpuUsage2", 10.0, false,
        //        this::callbackCpuUsageThresholdDeceed));
    }

    /**
     * Run the tests by driving the set up monitoring
     */
    public void run() {
        while (true) {
            try {
                m_cpuMonitor.update();
                m_memMonitor.update();
            } catch (StateUpdateException e) {
                e.printStackTrace();
            }

            System.out.println("Cpu: " + m_cpuMonitor.getProgress().toString() + "\n");
            System.out.println("Memory: " + m_memMonitor.getState().toString() + "\n");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Example callback for CPU usage threshold exceeded
     *
     * @param p_currentValue Current value that exceeded the threshold
     * @param p_threshold Threshold exceeded
     */
    private void callbackCpuUsageThresholdExceed(final double p_currentValue, final ThresholdDouble p_threshold) {
        System.out.println("CPU threshold exceeded: " + p_currentValue);
        System.out.println(p_threshold);
    }

    /**
     * Example callback for CPU usage threshold deceeded
     *
     * @param p_currentValue Current value that deceeded the threshold
     * @param p_threshold Threshold deceeded
     */
    private void callbackCpuUsageThresholdDeceed(final double p_currentValue, final ThresholdDouble p_threshold) {
        System.out.println("CPU threshold deceeded: " + p_currentValue);
        System.out.println(p_threshold);
    }
}
