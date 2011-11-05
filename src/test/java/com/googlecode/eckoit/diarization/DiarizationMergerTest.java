/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.diarization;

import com.googlecode.eckoit.diarization.DiarizationSegment;
import com.googlecode.eckoit.diarization.DiarizationMerger;
import java.io.File;
import java.util.List;
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
public class DiarizationMergerTest {


    /**
     * Test of parseLine method, of class DiarizationMerger.
     */
    @Test
    public void testParseLine() {
        String id = "abce-das-adsdsa";
        String speaker = "M-S10";
        String start = "15.09";
        String duration = "7.52";

        String line = id + "\t" + speaker + "\t" + start + "\t" + duration;
        long offset = 0L;
        DiarizationMerger instance = new DiarizationMerger();
        DiarizationSegment result = instance.parseLine(line, offset, "conversation");

        //assertEquals(id, result.getId());
        assertEquals("M", result.getGender());
        assertEquals("S10", result.getSpeaker());
        assertEquals(15l, result.getSeconds());
        assertEquals(7l, result.getDuration());

    }
    @Test
    public void testParseLineWithOffset() {
        String id = "abce-das-adsdsa";
        String speaker = "M-S10";
        String start = "15.09";
        String duration = "7.52";

        String line = id + "\t" + speaker + "\t" + start + "\t" + duration;
        long offset = 2000L;
        DiarizationMerger instance = new DiarizationMerger();
        DiarizationSegment result = instance.parseLine(line, offset, "conversation");


        assertEquals(2015l, result.getSeconds());
        assertEquals(7l, result.getDuration());

    }
}