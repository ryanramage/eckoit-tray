/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import java.util.Map;
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



    @Test
    public void testMD5s() {
        SimpleAudioDirectory sad = new SimpleAudioDirectory();
        File root = new File("src/test/resources/audio/standard");

        List<RecordingWithInterval> recordings = sad.findRecordings(root);

        Map<String,String> md5s = sad.getMD5s(recordings);
        assertEquals(1, md5s.size());

        String filename = md5s.keySet().iterator().next();
        assertEquals("2011-06-22-11-58-22.mp3", filename);

        String md5 = md5s.get(filename);
        assertEquals("ApFceNvHKY91UtJWSaxxJw==", md5);


        System.out.println("MD5: " + md5);

    }

}