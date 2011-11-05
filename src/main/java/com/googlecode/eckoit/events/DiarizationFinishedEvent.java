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
public class DiarizationFinishedEvent {
    private File finishedFile;

    public DiarizationFinishedEvent(File finishedFile) {
        this.finishedFile = finishedFile;
    }

    public File getFinishedFile() {
        return finishedFile;
    }
}
