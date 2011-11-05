/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import com.googlecode.eckoit.module.liferecorder.FFMpegRecordingTasks;
import java.io.File;
import org.joda.time.DateTime;
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
public class FFMpegRecordingTasksTest {


    /**
     * Test of nameSplitFile method, of class FFMpegRecordingTasks.
     */
    @Test
    public void testNameSplitFile() {
        System.out.println("nameSplitFile");

        DateTime start = new DateTime(2010, 01, 01, 05, 30, 20, 0);
        DateTime end =   new DateTime(2010, 01, 01, 05, 40, 21, 0);
        Interval interval = new Interval(start, end);

        FFMpegRecordingTasks instance = new FFMpegRecordingTasks(null);
        String expResult = "20100101.053020-20100101.054021.mp3";
        String result = instance.splitFileName(interval);
        assertEquals(expResult, result);

    }


}