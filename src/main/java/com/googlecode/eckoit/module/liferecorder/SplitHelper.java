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
public class SplitHelper {

    public static long getFirstSplitDuration(Instant split, RecordingWithInterval recording) {
        System.out.println("Split: " + split.getMillis());
        System.out.println("Start: " + recording.getInterval().getStartMillis());


        return (split.getMillis() - recording.getInterval().getStartMillis())/1000;
    }


}
