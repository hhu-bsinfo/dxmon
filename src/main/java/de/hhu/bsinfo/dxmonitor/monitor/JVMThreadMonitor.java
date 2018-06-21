package de.hhu.bsinfo.dxmonitor.monitor;

import de.hhu.bsinfo.dxmonitor.state.JVMThreadState;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

public class JVMThreadMonitor implements Monitor {
    private final JVMThreadState m_state;

    public JVMThreadMonitor(final long p_tid) {
        m_state = new JVMThreadState(p_tid);
    }

    @Override
    public void update() throws StateUpdateException { }

    @Override
    public String generateCSVHeader(char p_delim) { return null; }

    @Override
    public String toCSV(char p_delim) { return null; }

    public JVMThreadState getState() {
        return m_state;
    }
}
