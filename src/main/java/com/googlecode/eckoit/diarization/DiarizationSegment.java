/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.diarization;

import org.codehaus.jackson.annotate.*;
import org.ektorp.support.CouchDbDocument;


/**
 *
 * @author ryan
 */
public class DiarizationSegment extends CouchDbDocument {


    private String gender;

    private String speaker;

    private long seconds;

    private long duration;

    private String conversation;

    private String type = "segment";




    /**
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return the speaker
     */
    public String getSpeaker() {
        return speaker;
    }

    /**
     * @param speaker the speaker to set
     */
    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    /**
     * @return the seconds
     */
    public long getSeconds() {
        return seconds;
    }

    /**
     * @param seconds the seconds to set
     */
    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    /**
     * @return the duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * @return the topic
     */
    public String getConversation() {
        return conversation;
    }

    /**
     * @param topic the topic to set
     */
    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
