package de.hhu.bsinfo.dxmonitor.monitor;

import de.hhu.bsinfo.dxmonitor.state.MemState;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

import java.util.ArrayList;

/**
 * Monitor for Memory related data
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 18.03.2018
 */
public class MemMonitor implements Monitor {
    private final MemState m_state;

    private final ArrayList<MultipleThresholdDouble> m_thresholdMemoryFree;

    /**
     * Constructor
     */
    public MemMonitor() {
        m_state = new MemState();
        m_thresholdMemoryFree = new ArrayList<>();
    }

    public void addThresholdMemoryFree(final MultipleThresholdDouble p_threshold) {
        m_thresholdMemoryFree.add(p_threshold);
    }

    @Override
    public void update() throws StateUpdateException {
        m_state.update();

        for (MultipleThresholdDouble threshold : m_thresholdMemoryFree) {
            threshold.evaluate(m_state.getFreePercent());
        }
    }

    @Override
    public String generateCSVHeader(char p_delim) {
        return null;
    }

    @Override
    public String toCSV(char p_delim) {
        return null;
    }

    public MemState getState() {
        return m_state;
    }
}
