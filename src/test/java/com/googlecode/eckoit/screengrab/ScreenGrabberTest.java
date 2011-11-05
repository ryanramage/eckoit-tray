/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.screengrab;

import com.googlecode.eckoit.screengrab.ScreenGrabber;
import com.googlecode.eckoit.events.ScreenGrabFinishEvent;
import org.bushe.swing.event.EventBus;
import com.googlecode.eckoit.events.ScreenGrabStartEvent;
import java.awt.image.BufferedImage;
import java.io.File;
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
public class ScreenGrabberTest {

    public ScreenGrabberTest() {
    }


    /**
     * Test of capture method, of class ScreenGrabber.
     */

    public void testCapture() throws InterruptedException {
        System.out.println("capture");
        File storage = new File("target/images");
        storage.mkdirs();

        ScreenGrabStartEvent start = new ScreenGrabStartEvent(storage, 1000);
        ScreenGrabber instance = new ScreenGrabber();
        EventBus.publish(start);
        // wait 3.5 seconds should have 3 pics
        Thread.sleep(3500);
        EventBus.publish(new ScreenGrabFinishEvent());

        File[] files = storage.listFiles();
        assertNotNull(files);
        assertEquals(3, files.length);

    }

    @Test
    public void testConstructor() {
        ScreenGrabber instance = new ScreenGrabber();

    }




}