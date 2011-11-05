/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

/**
 *
 * @author ryan
 */
public class JoinOperation {
    private RecordingWithInterval firstRecording;
    private RecordingWithInterval secondRecording;

    /**
     * @return the firstRecording
     */
    public RecordingWithInterval getFirstRecording() {
        return firstRecording;
    }

    /**
     * @param firstRecording the firstRecording to set
     */
    public void setFirstRecording(RecordingWithInterval firstRecording) {
        this.firstRecording = firstRecording;
    }

    /**
     * @return the secondRecording
     */
    public RecordingWithInterval getSecondRecording() {
        return secondRecording;
    }

    /**
     * @param secondRecording the secondRecording to set
     */
    public void setSecondRecording(RecordingWithInterval secondRecording) {
        this.secondRecording = secondRecording;
    }
}
