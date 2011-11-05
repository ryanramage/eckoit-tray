/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import com.googlecode.eckoit.module.liferecorder.RecordingTasks;
import com.googlecode.eckoit.module.liferecorder.RecordingWithInterval;
import com.googlecode.eckoit.module.liferecorder.TaskException;
import com.googlecode.eckoit.module.liferecorder.SplitHelper;
import com.googlecode.eckoit.module.liferecorder.JoinOperation;
import com.googlecode.eckoit.module.liferecorder.SplitOperation;
import com.googlecode.eckoit.TimeUtils;

/**
 *
 * @author ryan
 */
public class MockRecordingTask implements RecordingTasks {

    @Override
    public RecordingWithInterval[] split(SplitOperation split) throws TaskException {
        System.out.println("Split operation requested");
        System.out.println(split.getRecording().getFile());
        long splitSeconds = SplitHelper.getFirstSplitDuration(split.getSplitTime(), split.getRecording());
        System.out.println(TimeUtils.formatTimeBySec(splitSeconds, false));
        return null;

    }

    @Override
    public RecordingWithInterval join(JoinOperation join) throws TaskException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
