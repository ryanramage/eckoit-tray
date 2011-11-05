/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.events;

/**
 *
 * @author ryan
 */
public class DiarizeConfigEvent {

    private boolean diarize;

    public DiarizeConfigEvent(boolean diarize) {
        this.diarize = diarize;
    }

    public boolean getDiarize() {
        return diarize;
    }
}
