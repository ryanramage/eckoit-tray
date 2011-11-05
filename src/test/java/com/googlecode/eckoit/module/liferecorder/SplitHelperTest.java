/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import com.googlecode.eckoit.module.liferecorder.RecordingWithInterval;
import com.googlecode.eckoit.module.liferecorder.SplitHelper;
import org.joda.time.Instant;
import org.joda.time.Interval;
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
public class SplitHelperTest {



    /**
     * Test of getFirstSplitDuration method, of class SplitHelper.
     */
    @Test
    public void testGetFirstSplitDuration() {
        System.out.println("getFirstSplitDuration");
        Instant split = new Instant(100000);
        RecordingWithInterval recording = new RecordingWithInterval();
        recording.setInterval(new Interval(20000,300000));
        long expResult = 80L;
        long result = SplitHelper.getFirstSplitDuration(split, recording);
        assertEquals(expResult, result);

    }

}