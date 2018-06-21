package de.hhu.bsinfo.dxmonitor.monitor;

import de.hhu.bsinfo.dxmonitor.progress.NetworkProgress;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

import java.util.ArrayList;

public class NetworkMonitor implements Monitor {
    private final NetworkProgress m_progress;

    private final ArrayList<ThresholdDouble> m_thresholdReceiveThroughput;
    private final ArrayList<ThresholdDouble> m_thresholdTransmitThroughput;

    public NetworkMonitor(final String p_name) {
        m_progress = new NetworkProgress(p_name);
        m_thresholdReceiveThroughput = new ArrayList<>();
        m_thresholdTransmitThroughput = new ArrayList<>();
    }

    public void addThresholdReceiveThroughput(final ThresholdDouble p_threshold) {
        m_thresholdReceiveThroughput.add(p_threshold);
    }

    public void addThresholdTransmitThroughput(final ThresholdDouble p_threshold) {
        m_thresholdTransmitThroughput.add(p_threshold);
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public void update() throws StateUpdateException {
        m_progress.update();

        for (ThresholdDouble threshold : m_thresholdReceiveThroughput) {
            threshold.evaluate(m_progress.getReceiveThroughput());
        }

        for (ThresholdDouble threshold : m_thresholdTransmitThroughput) {
            threshold.evaluate(m_progress.getTransmitThroughput());
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

    public NetworkProgress getProgress() {
        return m_progress;
    }
}
