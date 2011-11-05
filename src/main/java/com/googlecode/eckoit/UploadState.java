/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit;

/**
 *
 * @author ryan
 */
public class UploadState {

    private String minorTaskName;
    private boolean minorTaskIndeterminate;
    private int minorTaskPercentComplete;

    private int overallPercentComplete;

    private String uploadURL;

    /**
     * @return the minorTaskName
     */
    public String getMinorTaskName() {
        return minorTaskName;
    }

    /**
     * @param minorTaskName the minorTaskName to set
     */
    public void setMinorTaskName(String minorTaskName) {
        this.minorTaskName = minorTaskName;
    }

    /**
     * @return the minorTaskIndeterminate
     */
    public boolean isMinorTaskIndeterminate() {
        return minorTaskIndeterminate;
    }

    /**
     * @param minorTaskIndeterminate the minorTaskIndeterminate to set
     */
    public void setMinorTaskIndeterminate(boolean minorTaskIndeterminate) {
        this.minorTaskIndeterminate = minorTaskIndeterminate;
    }

    /**
     * @return the minorTaskPercentComplete
     */
    public int getMinorTaskPercentComplete() {
        return minorTaskPercentComplete;
    }

    /**
     * @param minorTaskPercentComplete the minorTaskPercentComplete to set
     */
    public void setMinorTaskPercentComplete(int minorTaskPercentComplete) {
        this.minorTaskPercentComplete = minorTaskPercentComplete;
    }

    /**
     * @return the overallPercentComplete
     */
    public int getOverallPercentComplete() {
        return overallPercentComplete;
    }

    /**
     * @param overallPercentComplete the overallPercentComplete to set
     */
    public void setOverallPercentComplete(int overallPercentComplete) {
        this.overallPercentComplete = overallPercentComplete;
    }

    /**
     * @return the uploadURL
     */
    public String getUploadURL() {
        return uploadURL;
    }

    /**
     * @param uploadURL the uploadURL to set
     */
    public void setUploadURL(String uploadURL) {
        this.uploadURL = uploadURL;
    }


}
