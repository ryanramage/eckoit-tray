/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import org.joda.time.Instant;

/**
 *
 * @author ryan
 */
public class SplitOperation {
    private RecordingWithInterval recording;
    private Instant splitTime;

    /**
     * @return the recording
     */
    public RecordingWithInterval getRecording() {
        return recording;
    }

    /**
     * @param recording the recording to set
     */
    public void setRecording(RecordingWithInterval recording) {
        this.recording = recording;
    }

    /**
     * @return the splitTime
     */
    public Instant getSplitTime() {
        return splitTime;
    }

    /**
     * @param splitTime the splitTime to set
     */
    public void setSplitTime(Instant splitTime) {
        this.splitTime = splitTime;
    }

}
