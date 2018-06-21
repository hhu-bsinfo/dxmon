package de.hhu.bsinfo.dxmonitor.monitor;

import de.hhu.bsinfo.dxmonitor.state.JVMThreadsState;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

public class JVMThreadsMonitor implements Monitor {
    private final JVMThreadsState m_state;

    public JVMThreadsMonitor() {
        m_state = new JVMThreadsState();
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public void update() throws StateUpdateException {

    }

    @Override
    public String generateCSVHeader(char p_delim) { return null; }

    @Override
    public String toCSV(char p_delim) { return null; }

    public JVMThreadsState getState() {
        return m_state;
    }
}
