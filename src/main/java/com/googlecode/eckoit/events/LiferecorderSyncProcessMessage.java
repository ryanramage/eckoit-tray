/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.events;

/**
 *
 * @author ryan
 */
public class LiferecorderSyncProcessMessage {
    private String step;
    private int progress;
    private int totalSteps;
    
    public LiferecorderSyncProcessMessage(String step, int progress, int totalSteps) {
        this.step = step;
        this.progress = progress;
        this.totalSteps = totalSteps;
    }

    /**
     * @return the step
     */
    public String getStep() {
        return step;
    }

    /**
     * @return the percentComplete
     */
    public int getProgress() {
        return progress;
    }

    public int getTotalSteps() {
        return totalSteps;
    }



}
