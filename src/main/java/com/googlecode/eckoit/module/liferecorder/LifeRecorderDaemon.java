/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.eckoit.module.liferecorder;

import com.github.couchapptakeout.events.ExitApplicationMessage;
import com.googlecode.eckoit.events.LifeRecorderAttachedEvent;
import com.googlecode.eckoit.events.LifeRecorderDetachedEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.SystemUtils;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

/**
 * The job of this class is to listen for a Life_Recorder to be attached to the computer.
 * When it finds one, it begins the workflow to upload to the wiki.
 *
 * @author ryan
 */
public class LifeRecorderDaemon implements Runnable, EventSubscriber<ExitApplicationMessage> {

    private String settingsFileName;
    private long checkIntervalMilliSeconds;
    private boolean running = true;
    private LifeRecorderAttachedEvent attached;
    private List<String> roots;



    public LifeRecorderDaemon(List<String> roots, String settingsFileName, long checkIntervalMilliSeconds) {
        this.settingsFileName = settingsFileName;
        this.checkIntervalMilliSeconds = checkIntervalMilliSeconds;
        this.roots = roots;
        EventBus.subscribeStrongly(ExitApplicationMessage.class, this);
    }

    @Override
    public void onEvent(ExitApplicationMessage t) {
        running = false;
    }

    public void run() {
        while (running) {
            if (attached != null) {
                if (hasRecorderDetached()) {
                    Logger.getLogger(LifeRecorderDaemon.class.getName()).log(Level.INFO, "Life Recorder Detatched.");
                    attached = null;
                    EventBus.publish(new LifeRecorderDetachedEvent());
                }
            } else {
                File attachementRoot = isLifeRecorderAttached();
                if (attachementRoot != null) {
                    Logger.getLogger(LifeRecorderDaemon.class.getName()).log(Level.INFO, "Life Recorder Attached.");
                    attached = new LifeRecorderAttachedEvent(attachementRoot, null);
                    EventBus.publish(attached);
                }
            }
            sleep();
        }
    }

    public List<File> findRoots(List<String> roots) {
        if (roots == null || roots.isEmpty()) {
            if (SystemUtils.IS_OS_MAC_OSX) {
                File volumes = new File("/Volumes");
                return Arrays.asList(volumes.listFiles());
            }
            else { 
                return Arrays.asList(File.listRoots());
            }
        }

        List<File> files = new ArrayList<File>();
        for (String root : roots) {
            File rootFile = new File(root);
            files.add(rootFile);
        }
        return files;
    }


    public boolean hasRecorderDetached() {
        return !doesRootHaveLifeRecorderFile(attached.getRoot());
    }


    public File isLifeRecorderAttached() {
        List<File> files = findRoots(roots);
        File recorder = queryRootsForLifeRecorder(files);
        return recorder;

    }




    private void sleep() {
        try {
            Thread.sleep(checkIntervalMilliSeconds);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }


    protected File queryRootsForLifeRecorder(List<File> roots) {
        for (File file : roots) {
            if (doesRootHaveLifeRecorderFile(file)) {
                return file;
            }
        }
        return null;
    }

    protected boolean doesRootHaveLifeRecorderFile(File root) {
       // Logger.getLogger(LifeRecorderDaemon.class.getName()).log(Level.INFO, "Checking Root: " + root);
        File settingsFile = new File(root, settingsFileName);
        if (!settingsFile.exists()) {
            return false;
        } else {
            return true;
        }
    }



    /**
     * @return the checkIntervalMilliSeconds
     */
    public long getCheckIntervalMilliSeconds() {
        return checkIntervalMilliSeconds;
    }

    /**
     * @param checkIntervalMilliSeconds the checkIntervalMilliSeconds to set
     */
    public void setCheckIntervalMilliSeconds(long checkIntervalMilliSeconds) {
        this.checkIntervalMilliSeconds = checkIntervalMilliSeconds;
    }

    /**
     * @return the settingsFileName
     */
    public String getSettingsFileName() {
        return settingsFileName;
    }

    /**
     * @param settingsFileName the settingsFileName to set
     */
    public void setSettingsFileName(String settingsFileName) {
        this.settingsFileName = settingsFileName;
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }


}
