/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import java.util.List;
import java.io.File;
import org.joda.time.LocalDateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ryan
 */
public class SimpleAudioDirectoryTest {

    public SimpleAudioDirectoryTest() {
    }

    /**
     * Test of findMarkInterval method, of class SimpleAudioDirectory.
     */
    @Test
    public void testFindMarkInterval() {
    }

    /**
     * Test of findMarks method, of class SimpleAudioDirectory.
     */
    @Test
    public void testFindMarks() {
    }

    /**
     * Test of findRecordings method, of class SimpleAudioDirectory.
     */
    @Test
    public void testFindRecordingsStandard() {
        SimpleAudioDirectory sad = new SimpleAudioDirectory();
        File root = new File("src/test/resources/audio/standard");
        System.out.println(root.getAbsoluteFile());
        List<RecordingWithInterval> recordings = sad.findRecordings(root);
        assertEquals(1, recordings.size());
    }

    @Test
    public void testStandardFilenameParse() {
        SimpleAudioDirectory sad = new SimpleAudioDirectory();
        LocalDateTime lad = sad.parseFileStartDate("2000-01-01-12-00-01.wav");
        assertEquals(2000, lad.getYear());
        assertEquals(1, lad.getMonthOfYear());
        assertEquals(1, lad.getDayOfMonth());
        assertEquals(12, lad.getHourOfDay());
        assertEquals(0, lad.getMinuteOfHour());
        assertEquals(1, lad.getSecondOfMinute());
    }

    @Test
    public void testStandardFilenameParseMp3() {
        SimpleAudioDirectory sad = new SimpleAudioDirectory();
        LocalDateTime lad = sad.parseFileStartDate("2000-01-01-12-00-01.mp3");
        assertEquals(2000, lad.getYear());
        assertEquals(1, lad.getMonthOfYear());
        assertEquals(1, lad.getDayOfMonth());
        assertEquals(12, lad.getHourOfDay());
        assertEquals(0, lad.getMinuteOfHour());
        assertEquals(1, lad.getSecondOfMinute());
    }

    /**
     * Test of getRecordingDir method, of class SimpleAudioDirectory.
     */
    @Test
    public void testFindOGGDuration() {
        File root = new File("src/test/resources/audio/trombone.ogg");
        SimpleAudioDirectory sad = new SimpleAudioDirectory();
        int duration = sad.findOGGDuration(root);
        assertEquals(28, duration);
    }

}