/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import com.github.couchapptakeout.ExitApplicationMessage;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.googlecode.eckoit.module.liferecorder.LifeRecorderDaemon;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.ThreadSafeEventService;
import org.bushe.swing.event.EventServiceLocator;
import com.googlecode.eckoit.events.LifeRecorderAttachedEvent;
import com.googlecode.eckoit.events.LifeRecorderDetachedEvent;
import java.io.File;
import java.io.IOException;
import org.bushe.swing.event.EventSubscriber;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ryan
 */
public class LifeRecorderDaemonTest implements EventSubscriber<LifeRecorderAttachedEvent>{

    public LifeRecorderDaemonTest() {
    }

    LifeRecorderAttachedEvent testEventReceived;
    LifeRecorderDetachedEvent detatchedEvent;
    String lrf = ".liferecorder";


    @Override
    public void onEvent(LifeRecorderAttachedEvent t) {
        this.testEventReceived = t;
    }


    /**
     * Test of run method, of class LifeRecorderDaemon.
     */
    @Test
    public void testLifeRecorderPresent() throws InterruptedException, IOException {
        System.setProperty(EventServiceLocator.SERVICE_NAME_EVENT_BUS, ThreadSafeEventService.class.getName());
        EventBus.subscribe(LifeRecorderAttachedEvent.class, this);

        detatchedEvent = null;
        EventBus.subscribe(LifeRecorderDetachedEvent.class, new EventSubscriber<LifeRecorderDetachedEvent>() {
            @Override
            public void onEvent(LifeRecorderDetachedEvent t) {
               detatchedEvent = t;
            }
        });

        List<String> roots = setupFakeRoot(lrf);
        LifeRecorderDaemon instance = new LifeRecorderDaemon(roots, lrf, 500);



        new Thread(instance).start();
        Thread.sleep(1500);
        instance.onEvent(new ExitApplicationMessage());

        assertNotNull(testEventReceived);
        assertEquals(new File(roots.get(0)), testEventReceived.getRoot());
        assertNull(detatchedEvent);
        tearDownFakeRoot(roots, lrf);

    }

    private List<String> setupFakeRoot(String lifeRecorderFile) throws IOException {
        String[] roots = {new String("target")};
        if (roots == null || roots.length == 0) fail("System has no roots to use as a test");
        String fakeRoot = roots[0];
        System.out.println("Fake Root: " + fakeRoot);

        File tempFile = new File(fakeRoot, lifeRecorderFile);
        tempFile.createNewFile();
        assertTrue(tempFile.exists());
        return Arrays.asList(roots);
    }

    private void tearDownFakeRoot(List<String> roots, String lifeRecorderFile) {
        File tempFile = new File(new File(roots.get(0)), lifeRecorderFile);
        tempFile.delete();
    }


    /**
     * Test of run method, of class LifeRecorderDaemon.
     */
    @Test
    public void testLifeRecorderNotPresent() throws InterruptedException, IOException {

        EventBus.subscribe(LifeRecorderAttachedEvent.class, this);
        this.testEventReceived = null;
        String lifeRecorderFile = ".liferecorder";
        List<String> roots = Arrays.asList("target");

        LifeRecorderDaemon instance = new LifeRecorderDaemon(roots, lifeRecorderFile, 300);


        new Thread(instance).start();
        Thread.sleep(1500);
        instance.onEvent(new ExitApplicationMessage());
        // downgrade this test. We cant differentiate in case where there is a
        // real life recorder present.
        if (testEventReceived != null) {

            Logger.getLogger(LifeRecorderDaemonTest.class.getName()).log(Level.WARNING, "Life recorder not Present Test detected life recorder...do you have on attached?");
        }

    }

    /**
     * Test of run method, of class LifeRecorderDaemon.
     */

    public void testLifeRecorderDetatchedDetected() throws InterruptedException, IOException {

        detatchedEvent = null;
        EventBus.subscribe(LifeRecorderDetachedEvent.class, new EventSubscriber<LifeRecorderDetachedEvent>() {
            @Override
            public void onEvent(LifeRecorderDetachedEvent t) {
               detatchedEvent = t;
            }
        });



        List<String> roots = setupFakeRoot(lrf);

        LifeRecorderDaemon instance = new LifeRecorderDaemon(roots, lrf, 300);
        new Thread(instance).start();
        Thread.sleep(650); // should have checked 2 times
        tearDownFakeRoot(roots, lrf);
        Thread.sleep(650); // should have checked 2 times
        instance.onEvent(new ExitApplicationMessage());

        assertNotNull(detatchedEvent);
    }

}