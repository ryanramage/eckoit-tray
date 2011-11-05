/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.events;

/**
 *
 * @author ryan
 */
public class RecordingFinishedEvent {
    private String recordingId;
    private String lastRecordingDB;
    private long  duration;
    private boolean diarize;

    public RecordingFinishedEvent(String lastRecordingDB, String recordingId, long duration, boolean diarize) {
        this.lastRecordingDB = lastRecordingDB;
        this.recordingId = recordingId;
        this.duration = duration;
        this.diarize = diarize;

    }

    public String getLastRecordingDB() {
        return lastRecordingDB;
    }

    public String getRecordingId() {
        return recordingId;
    }

    public long getDuration() {
        return duration;
    }

    public boolean getDiarize() {
        return diarize;
    }

}
