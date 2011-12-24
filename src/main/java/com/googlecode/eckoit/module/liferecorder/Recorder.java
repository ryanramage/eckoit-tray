/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.joda.time.Instant;
import org.joda.time.Interval;

/**
 *
 * @author ryan
 */
public interface Recorder {

    List<Interval> findMarkInterval(List<Instant> marks);

    List<Instant> findMarks(List<RecordingWithInterval> recordings);

    List<RecordingWithInterval> findRecordings(File root);

    Map <String,String> getMD5s(List<RecordingWithInterval> recordings);

    File getRecordingDir(File root);

}
