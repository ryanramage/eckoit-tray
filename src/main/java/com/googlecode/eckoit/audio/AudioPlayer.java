/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.audio;

import com.googlecode.eckoit.AudioPlayerListener;
import java.io.File;

/**
 *
 * @author ryan
 */
public interface AudioPlayer {


    /**
     * Start a file playing.
     * @param audioFile
     * @return the total seconds of the file
     */
    long play(File audioFile);

    void pause();

    void stop();

    void resume();

    /**
     * Seek to to seconds into track
     * @param percent
     */
    void seek(long seconds);

    void addListener(AudioPlayerListener listener);
    void removerListener(AudioPlayerListener listener);

}
