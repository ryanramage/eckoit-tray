/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

/**
 *
 * @author ryan
 */
public interface RecordingTasks {


    public RecordingWithInterval[] split(SplitOperation split) throws TaskException;

    public RecordingWithInterval join(JoinOperation join) throws TaskException;

}
