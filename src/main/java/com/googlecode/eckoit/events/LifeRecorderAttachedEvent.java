/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.events;

import java.io.File;

/**
 *
 * @author ryan
 */
public class LifeRecorderAttachedEvent {
    private File root;
    private File recordingDir;

    public LifeRecorderAttachedEvent(File root, File recordingDir) {
        this.root = root;
        this.recordingDir = recordingDir;
    }


    /**
     * @return the root
     */
    public File getRoot() {
        return root;
    }

    /**
     * @return the recordingDir
     */
    public File getRecordingDir() {
        return recordingDir;
    }

}
