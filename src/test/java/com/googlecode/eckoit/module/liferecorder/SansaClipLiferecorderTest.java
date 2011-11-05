/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;



import org.joda.time.Instant;
import java.util.ArrayList;
import java.io.File;
import java.util.List;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ryan
 */
public class SansaClipLiferecorderTest {


    /**
     * Test of sortRecordings method, of class SansaClipLiferecorder.
     */
    @Test
    public void testSortRecordings() {
        System.out.println("sortRecordings");
        List<RecordingWithInterval> recordings = new ArrayList<RecordingWithInterval>();
        {
            RecordingWithInterval r = new RecordingWithInterval();
            r.setInterval(new Interval(34, 40));
            recordings.add(r);
        }
        {
            RecordingWithInterval r = new RecordingWithInterval();
            r.setInterval(new Interval(27, 37));
            recordings.add(r);
        }
        {
            RecordingWithInterval r = new RecordingWithInterval();
            r.setInterval(new Interval(42, 52));
            recordings.add(r);
        }
        


        SansaClipLiferecorder instance = new SansaClipLiferecorder();
        instance.sortRecordings(recordings);

        assertEquals(27, recordings.get(0).getInterval().getStartMillis());
        assertEquals(34, recordings.get(1).getInterval().getStartMillis());
        assertEquals(42, recordings.get(2).getInterval().getStartMillis());
    }
    @Ignore
    @Test
    public void testFindMarks() {
        List<RecordingWithInterval> recordings = new ArrayList<RecordingWithInterval>();
        {
            RecordingWithInterval r = new RecordingWithInterval();
            r.setInterval(new Interval(3400, 4000));
            recordings.add(r);
        }
        {
            RecordingWithInterval r = new RecordingWithInterval();
            r.setInterval(new Interval(2700, 3400));
            recordings.add(r);
        }
        {
            RecordingWithInterval r = new RecordingWithInterval();
            r.setInterval(new Interval(4200, 5200));
            recordings.add(r);
        }
        SansaClipLiferecorder instance = new SansaClipLiferecorder();
        instance.sortRecordings(recordings);
        instance.setSplitMillisecondTollerance(0);
        instance.setSplitTime(600);
        List<Instant> marks = instance.findMarks(recordings);
        assertEquals(2, marks.size());
        Instant instant = new Instant(3400);
        assertEquals(instant, marks.get(0));
    }
    @Ignore
    @Test
    public void testFindMarksWithTolerance() {
        List<RecordingWithInterval> recordings = new ArrayList<RecordingWithInterval>();
        {
            RecordingWithInterval r = new RecordingWithInterval();
            r.setInterval(new Interval(3600, 4000));
            recordings.add(r);
        }
        {
            RecordingWithInterval r = new RecordingWithInterval();
            r.setInterval(new Interval(2700, 3400));
            recordings.add(r);
        }
        {
            RecordingWithInterval r = new RecordingWithInterval();
            r.setInterval(new Interval(4300, 5200));
            recordings.add(r);
        }
        SansaClipLiferecorder instance = new SansaClipLiferecorder();
        instance.sortRecordings(recordings);
        instance.setSplitMillisecondTollerance(200);
        instance.setSplitTime(600);
        List<Instant> marks = instance.findMarks(recordings);
        assertEquals(2, marks.size());
        Instant instant = new Instant(3400);
        assertEquals(instant, marks.get(0));
    }

    /**
     * Test of parseFileStartDate method, of class SansaClipLiferecorder.
     */
    @Test
    public void testParseFileStartDate() {
        System.out.println("parseFileStartDate");
        String filename = "R_MIC_101025-170037.mp3";
        SansaClipLiferecorder instance = new SansaClipLiferecorder();
        LocalDateTime expResult = new LocalDateTime(2010, 10, 25, 17,00,37);
        LocalDateTime result = instance.parseFileStartDate(filename);
        assertEquals(expResult, result);

    }

 

}