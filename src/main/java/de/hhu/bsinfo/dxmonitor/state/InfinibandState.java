package de.hhu.bsinfo.dxmonitor.state;

import java.io.File;

/**
 * State of Infiniband device
 *
 * Helpful doc link for /sys/class/infiniband - https://www.kernel.org/doc/Documentation/ABI/stable/sysfs-class-infiniband
 */
public class InfinibandState implements State{
    private static final String SYS_INFINIBAND = "/sys/class/infiniband";

    private String m_deviceIdentifier;

    private int m_portCnt;
    // todo check everything with file content in /sys/class/infiniband
    private long m_rate;

    private long[] m_lids;
    private String[] m_states;
    private String[] m_physStates;

    private long[] m_rcvErrors;
    private long[] m_rcvBytes;

    public InfinibandState(final String p_deviceIdentifier) {
        m_deviceIdentifier = p_deviceIdentifier;
        m_portCnt = readPortCount();
    }

    @Override
    public void update() throws StateUpdateException {
        // TODO implement
    }

    @Override
    public String generateCSVHeader(char p_delim) {
        return null;
    }

    @Override
    public String toCSV(char p_delim) {
        return null;
    }

    public int getPortCount() {
        return m_portCnt;
    }

    public long[] getLids() {
        return m_lids;
    }

    public String[] getStates() {
        return m_states;
    }

    public String[] getPhysStates() {
        return m_physStates;
    }






    private int readPortCount() {
        return (new File(SYS_INFINIBAND + "/" + m_deviceIdentifier + "ports/")).listFiles().length; // TODO unsafe - check for nullpointer
    }
}
