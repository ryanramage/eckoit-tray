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
public class RecordingSplitEvent {
    private File finishedFile;

    public RecordingSplitEvent(File finishedFile) {
        this.finishedFile = finishedFile;
    }

    public File getFinishedFile() {
        return finishedFile;
    }

}
