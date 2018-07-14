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

/**
 * A threshold defined by a long value with an installed callback (In this case the Callback Function will be called if
 * the threshold is exceeded multiple times.
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 14.07.2018
 */
public class MultipleThresholdDouble {

    /**
     * CallbackFunction for threshold callback
     */
    @FunctionalInterface
    public interface CallbackFunction {
        /**
         * Function signature for callback on threshold trigger
         *
         * @param p_currentValue Current value provided for evaluation (that triggered the callback)
         * @param p_threshold The threshold that triggered the callback
         */
        void handle(final double p_currentValue, final MultipleThresholdDouble p_threshold);
    }

    private static final int CALLBACK_LIMIT_UNLIMITED = 0;

    private final String m_name;
    private final double m_threshold;
    private final boolean m_exceed;
    private final int m_cnt;
    private final long m_callbackLimit;

    private final CallbackFunction m_callback;

    private int m_exceedCnt;
    private long m_hitCount;

    /**
     * Constructor
     *
     * The callback limit is set to unlimited
     *
     * @param p_name Name of the threshold (for debugging)
     * @param p_value Threshold value
     * @param p_exceed True to trigger callback on exceeding the value, false on deceeding it
     * @param p_callback Callback to call if the threshold is hit
     * @param p_cnt how often does the threshold need to be exceed
     */
    public MultipleThresholdDouble(final String p_name, final double p_value, final boolean p_exceed, final int p_cnt,
                           final CallbackFunction p_callback) {
        this(p_name, p_value, p_exceed, p_cnt, CALLBACK_LIMIT_UNLIMITED, p_callback);
    }

    /**
     * Constructor
     *
     * @param p_name Name of the threshold (for debugging)
     * @param p_value Threshold value
     * @param p_exceed True to trigger callback on exceeding the value, false on deceeding it
     * @param p_callbackLimit Callback limit count (0 unlimited) on threshold hit
     * @param p_callback Callback to call if the threshold is hit and the limit not exceeded
     * @param p_cnt how often does the threshold need to be exceed
     */
    public MultipleThresholdDouble(final String p_name, final double p_value, final boolean p_exceed,
                                   final int p_cnt, final long p_callbackLimit, final CallbackFunction p_callback) {
        m_name = p_name;
        m_threshold = p_value;
        m_exceed = p_exceed;
        m_callbackLimit = p_callbackLimit;
        m_callback = p_callback;
        m_exceedCnt = 0;
        m_cnt = p_cnt;
    }

    /**
     * Get the name of this threshold
     */
    public String getName() {
        return m_name;
    }

    /**
     * Get the threshold value
     */
    public double getThreshold() {
        return m_threshold;
    }

    /**
     * True if the callback triggered once the threshold is exceeded
     */
    public boolean triggerOnExceed() {
        return m_exceed;
    }

    /**
     * True if the callback is triggered once the threshold is deceeded
     */
    public boolean triggerOnDeceed() {
        return !m_exceed;
    }

    /**
     * Get the number of times the callback has been trigger thus far
     */
    public long getHitCount() {
        return m_hitCount;
    }


    /**
     * Evaluate this threshold and test it against a value. Calls the callback if the threshold's conditions
     * are met
     *
     * @param p_other Other value to test against threshold
     */
    public void evaluate(final double p_other) {
        if (m_exceed && p_other > m_threshold || !m_exceed && p_other < m_threshold) {
            m_exceedCnt++;
        } else {
            m_exceedCnt = 0;
        }

        if (m_exceedCnt >= m_cnt) {
            m_exceedCnt = 0;
            m_hitCount++;
            m_callback.handle(p_other, this);
        }
    }

    @Override
    public String toString() {
        return m_name + ", m_threshold " + m_threshold + ", m_exceed " + m_exceed + ", m_hitCount " + m_hitCount +
                ", m_callbackLimit " + m_callbackLimit + ", m_callback " + m_callback + " , m_cnt " + m_cnt + ", m_exceedCnt " + m_exceedCnt;
    }

}
