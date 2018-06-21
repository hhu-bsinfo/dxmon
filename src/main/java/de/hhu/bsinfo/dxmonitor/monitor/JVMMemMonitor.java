package de.hhu.bsinfo.dxmonitor.monitor;

import de.hhu.bsinfo.dxmonitor.state.JVMMemState;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

public class JVMMemMonitor implements Monitor {

    private JVMMemState m_state;
    // No callblacks

    public JVMMemMonitor() {
        m_state = new JVMMemState();
    }

    @Override
    public String toString() { return ""; }

    @Override
    public void update() throws StateUpdateException { }

    @Override
    public String generateCSVHeader(char p_delim) { return null; }

    @Override
    public String toCSV(char p_delim) { return null; }

    public JVMMemState getState() {
        return m_state;
    }
}
