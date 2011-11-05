/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import com.googlecode.eckoit.TimeUtils;
import com.googlecode.eckoit.audio.FFMpegSplitter;
import com.googlecode.eckoit.audio.RecordingFinishedHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author ryan
 */
public class FFMpegRecordingTasks implements RecordingTasks {

    private FFMpegSplitter splitter;

    public FFMpegRecordingTasks(FFMpegSplitter splitter) {
        this.splitter = splitter;
    }

    @Override
    public RecordingWithInterval[] split(SplitOperation split) throws TaskException {
        try {
            // generate the first file
            RecordingWithInterval firstRecording = new RecordingWithInterval();
            System.out.println("Base File: " + split.getRecording().getFile().getName());

            long start1 = 0;


            long duration1 = SplitHelper.getFirstSplitDuration(split.getSplitTime(), split.getRecording());
            System.out.println("Duration: " + duration1);
            DateTime end1 = split.getRecording().getInterval().getStart().plus(duration1 * 1000);
            Interval i1 = new Interval(split.getRecording().getInterval().getStart(), end1);
            firstRecording.setInterval(i1);

            File firstFile = nameSplitFile(i1, split.getRecording().getFile());
            
            System.out.println("Split: " + TimeUtils.formatTimeBySec(duration1, false));
            System.out.println("file1: " + firstFile.getName());
            splitter.split(split.getRecording().getFile(), start1, duration1, firstFile);
            
            firstRecording.setFile(firstFile);

            // generate the second file
            RecordingWithInterval secondRecording = new RecordingWithInterval();
            long start2 = duration1;
            Interval i2 = new Interval(end1, split.getRecording().getInterval().getEnd());
            secondRecording.setInterval(i2);
            File secondFile = nameSplitFile(i2, split.getRecording().getFile());
            System.out.println("file2: " + secondFile.getName());
            //System.out.println("Split: " + TimeUtils.formatTimeBySec(start2,false));
            splitter.split(split.getRecording().getFile(), start2, secondFile);
            secondRecording.setFile(secondFile);


            return new RecordingWithInterval[]{firstRecording, secondRecording};
        } catch (Exception ex) {
            Logger.getLogger(FFMpegRecordingTasks.class.getName()).log(Level.SEVERE, null, ex);
            throw new TaskException(ex.getMessage());
        }


    }

    protected File nameSplitFile(Interval interval, File fileToSplit) {
        File parent = fileToSplit.getParentFile();
        String name = splitFileName(interval);
        return new File(parent, name);
    }

    protected String splitFileName(Interval interval) {
        String start = dateTimeToString(interval.getStart());
        String end = dateTimeToString(interval.getEnd());
        return  start + "-" + end + ".mp3";
    }

    protected String dateTimeToString(DateTime dt) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd.hhmmss");
        return fmt.print(dt);
        //return dt.getYear() + dt.getMonthOfYear() + dt.getDayOfMonth() + "-" + dt.getHourOfDay() + dt.getMinuteOfHour() + dt.getSecondOfMinute();
    }


    @Override
    public RecordingWithInterval join(JoinOperation join) {
        File file1 = join.getFirstRecording().getFile();
        File file2 = join.getSecondRecording().getFile();

        System.out.println("Join: " + file1.getName() + " and " + file2.getName());
        RecordingFinishedHelper help = new RecordingFinishedHelper(null, null);
        Interval joinDuration = new Interval(join.getFirstRecording().getInterval().getStart(), join.getSecondRecording().getInterval().getEnd());
        File joinFile = nameSplitFile(joinDuration, file1);
        System.out.println("Result: " + joinFile);

        RecordingWithInterval joinRWI = new RecordingWithInterval();
        joinRWI.setFile(joinFile);
        joinRWI.setInterval(joinDuration);
        try {
            help.mergeFiles(joinFile, Arrays.asList(file1, file2));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FFMpegRecordingTasks.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FFMpegRecordingTasks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return joinRWI;

    }



}
