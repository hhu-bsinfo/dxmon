package de.hhu.bsinfo.dxmonitor.monitor;

import de.hhu.bsinfo.dxmonitor.progress.DiskProgress;
import de.hhu.bsinfo.dxmonitor.state.StateUpdateException;

import java.util.ArrayList;

public class DiskMonitor implements Monitor {
    private final DiskProgress m_progress;

    private final ArrayList<ThresholdDouble> m_thresholdsReadThroughput;
    private final ArrayList<ThresholdDouble> m_thresholdsWriteThroughput;

    public DiskMonitor(final String p_name) {
        m_progress = new DiskProgress(p_name);
        m_thresholdsReadThroughput = new ArrayList<>();
        m_thresholdsWriteThroughput = new ArrayList<>();
    }

    public void addThresholdReadThroughput(final ThresholdDouble p_threshold) {
        m_thresholdsReadThroughput.add(p_threshold);
    }

    public void addThresholdWriteThroughput(final ThresholdDouble p_threshold) {
        m_thresholdsWriteThroughput.add(p_threshold);
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public void update() throws StateUpdateException {
        m_progress.update();

        for (ThresholdDouble threshold : m_thresholdsReadThroughput) {
            threshold.evaluate(m_progress.getReadThroughput());
        }

        for (ThresholdDouble threshold : m_thresholdsWriteThroughput) {
            threshold.evaluate(m_progress.getWriteThroughput());
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

    public DiskProgress getProgress() {
        return m_progress;
    }
}