/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit;

import com.googlecode.eckoit.audio.AudioPlayer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ryan
 */
public class AudioPlayerMock implements AudioPlayer {


    private Tick tick;
    private List<AudioPlayerListener> listeners = new ArrayList<AudioPlayerListener>();

    @Override
    public long play(File audioFile) {
        tick =  new Tick(this);
        tick.start();
        return tick.totalSeconds;
    }

    @Override
    public void pause() {
        tick.pause();

    }

    public void resume() {
        tick.pause();
    }

    @Override
    public void stop() {
        tick.stopTime();
    }

    @Override
    public void seek(long seconds) {
        tick.currentSeconds = (int)seconds;
    }

    @Override
    public void addListener(AudioPlayerListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removerListener(AudioPlayerListener listener) {
        listeners.remove(listener);
    }

    private class Tick extends Thread {
        AudioPlayerMock mock;
        public long currentSeconds = 0;
        public long totalSeconds = 180; // 3 minutes

        public Tick(AudioPlayerMock mock) {
            this.mock = mock;
        }

        public boolean pause = false;
        public boolean stop = false;

        public void pause() {
            pause = !pause;
        }

        public void stopTime() {
            this.stop = true;
        }


        public void run() {
             for (AudioPlayerListener apl : mock.listeners) {
                apl.started();
            }
            while(!stop && currentSeconds < totalSeconds) {
                try {
                    this.sleep(1000);
                } catch (InterruptedException ex){}
                if (!pause) {
                    System.out.println("Tick!");
                    currentSeconds++;
                    for (AudioPlayerListener apl : mock.listeners) {
                        System.out.println("Update listener/: " +currentSeconds);
                        apl.playing(currentSeconds, totalSeconds);
                    }
                }
            }
            for (AudioPlayerListener apl : mock.listeners) {
                apl.stopped();
            }

        }

    }


}
