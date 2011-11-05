/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.audio;

import java.util.List;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ryan.ramage
 */
public class RecordingFinishedHelperTest {

    public RecordingFinishedHelperTest() {
    }






    /**
     * Test of sortFilesNumerically method, of class RecordingFinishedHelper.
     */
    @Test
    public void testGetFileNameAsInt() {
        File f = new File("target/1.mp3");
        RecordingFinishedHelper rfh = new RecordingFinishedHelper(null, null);
        long result = rfh.getFilenameAsInt(f);
        assertEquals(result, 1l);
    }

    @Test
    public void testSortFilesOutOfOrder() {
        File f0 = new File("target/0.mp3");
        File f1 = new File("target/1.mp3");
        File f2 = new File("target/2.mp3");
        File f3 = new File("target/3.mp3");
        File[] outOfOrder = new File[] {f2, f0, f3,f1};
        RecordingFinishedHelper rfh = new RecordingFinishedHelper(null, null);
        List<File> ordered = rfh.sortFilesNumerically(outOfOrder);

        assertEquals(f0, ordered.get(0));
        assertEquals(f1, ordered.get(1));
        assertEquals(f2, ordered.get(2));
        assertEquals(f3, ordered.get(3));
    }



}