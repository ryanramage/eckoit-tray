/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit;

import java.util.Date;

/**
 *
 * @author ryan
 */
public class AudioRecordingState {

    private Date recordingStarted;
    private Date recordingStopped;
    private String recordingTitle;

    /**
     * @return the recordingStarted
     */
    public Date getRecordingStarted() {
        return recordingStarted;
    }

    /**
     * @param recordingStarted the recordingStarted to set
     */
    public void setRecordingStarted(Date recordingStarted) {
        this.recordingStarted = recordingStarted;
    }

    /**
     * @return the recordingStopped
     */
    public Date getRecordingStopped() {
        return recordingStopped;
    }

    /**
     * @param recordingStopped the recordingStopped to set
     */
    public void setRecordingStopped(Date recordingStopped) {
        this.recordingStopped = recordingStopped;
    }

    /**
     * @return the recordingTitle
     */
    public String getRecordingTitle() {
        return recordingTitle;
    }

    /**
     * @param recordingTitle the recordingTitle to set
     */
    public void setRecordingTitle(String recordingTitle) {
        this.recordingTitle = recordingTitle;
    }
    
}
