/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.events;

/**
 *
 * @author ryan.ramage
 */
public class MeetingFinalProcessingEvent {
    public static final int STARTED = 1;
    public static final int UPDATE = 2;
    public static final int FINISHED = 3;

    private int status;
    private String step;
    private float progress;

    public MeetingFinalProcessingEvent(int status) {
        this.status = status;
    }

    public MeetingFinalProcessingEvent(String step, float progress) {
        this.status = UPDATE;
        this.step = step;
        this.progress = progress;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the step
     */
    public String getStep() {
        return step;
    }

    /**
     * @return the progress
     */
    public float getProgress() {
        return progress;
    }



}
