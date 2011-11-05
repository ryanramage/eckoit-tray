/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit;

import com.googlecode.eckoit.audio.AudioPlayer;
import java.io.IOException;
import java.io.File;
import java.util.Map;
import javax.sound.sampled.LineUnavailableException;

/**
 *
 * @author ryan
 */
public interface ConversationController {



    void startRecording() throws LineUnavailableException;

    void stopRecording();

    void startPlayback();

    void pausePlayback();

    void resumePlayback();

    void stopPlayback();

    void seekPlayback(long seconds);

    AudioPlayer getAudioPlayer();
}
