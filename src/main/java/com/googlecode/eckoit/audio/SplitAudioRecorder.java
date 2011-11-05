/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.audio;

import com.googlecode.eckoit.events.RecordingFinishedEvent;
import com.googlecode.eckoit.events.RecordingSplitEvent;
import java.io.IOException;
import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import org.bushe.swing.event.EventBus;

/**
 *
 * @author ryan
 */
public class SplitAudioRecorder implements AudioRecorder {
    private static long splitTime = 300000L; // 5 minutes
    private static SplitAudioRecorder singletonObject;

    private boolean isRecording = false;
    private long recordingStart;
    private TargetDataLine m_line;
    private AudioFileFormat.Type m_targetType;
    private SplitableAudioInputStream m_audioInputStream;
    private File m_outputFile;
    private Timer timer;
    private int sectionCount;
    private File root;
    private File section;


    public static void setSplitTime(long splitTime) {
        SplitAudioRecorder.splitTime = splitTime;
    }

    public static long getSplitTime() {
        return splitTime;
    }

    @Override
    public synchronized void startRecording(File root, final String mixer, float gain) throws LineUnavailableException {


        if (isRecording || m_line != null || (m_line != null && m_line.isOpen())) {
            throw new LineUnavailableException();
        }
        if (!root.isDirectory()) {
            throw new RuntimeException("A directory is expected");
        }
        this.root = root;
        sectionCount = 0;
        isRecording = true;
        recordingStart = System.currentTimeMillis();
        section = nextSectionFile();

        AudioFormat audioFormat = new AudioFormat(16000.0F, 16, 1, true, true);
        audioFormat = SimpleAudioRecorder.getBestAudioFormat(audioFormat, mixer);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        Mixer selectedMixer = SimpleAudioRecorder.getSelectedMixer(mixer);
        if (selectedMixer == null) {

            m_line = (TargetDataLine) AudioSystem.getLine(info);
            selectedMixer = AudioSystem.getMixer(null);

        } else {
            m_line = (TargetDataLine) selectedMixer.getLine(info);
        }
        m_line.open(audioFormat);

        //FloatControl fc = (FloatControl) selectedMixer.getControl(FloatControl.Type.MASTER_GAIN);
        //System.out.println("Master Gain min: " + fc.getMinimum());
        //System.out.println("Master Gain min: " + fc.getMaximum());
        //ystem.out.println("Master Gain cur: " + fc.getValue());
        //fc.setValue(MIN_PRIORITY);
        AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;
        m_audioInputStream = new SplitableAudioInputStream(new AudioInputStream(m_line));
        m_targetType = targetType;
         m_outputFile = section;
        new Recorder().start();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                sectionCount++;
                File oldsection = section;
                section = nextSectionFile(); 
                split(oldsection, section);
 
            }
        }, splitTime, splitTime);
    }

    private File nextSectionFile() {
        return new File(root, sectionCount + ".wav");
    }


    private void split(File oldsection, File newsectiom) {
        m_outputFile = newsectiom;
        m_audioInputStream = m_audioInputStream.clone();
        new Recorder().start();
        EventBus.publish(new RecordingSplitEvent(oldsection));
    }

    /** Stops the recording.
     */
    @Override
    public synchronized long stopRecording() {
        if (isRecording) {
            m_line.stop();
            m_line.close();
            m_line = null;
            timer.cancel();
            isRecording = false;
            long now = System.currentTimeMillis();
            // final section
            EventBus.publish(new RecordingSplitEvent(section));
            return (now - recordingStart)/1000;
        }
        return -1;
    }

    @Override
    public boolean isRecording() {
        return isRecording;
    }







    private SplitAudioRecorder() {

    }
    public static synchronized SplitAudioRecorder getSingletonObject() {
            if (singletonObject == null) {
                    singletonObject = new SplitAudioRecorder();
            }
            return singletonObject;
    }


    private class Recorder extends Thread {

        @Override
        public void run() {
            try {
                AudioSystem.write(m_audioInputStream, m_targetType, m_outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /** Starts the recording.
        To accomplish this, (i) the line is started and (ii) the
        thread is started.
         */
        @Override
        public void start() {
            m_line.start();
            super.start();
        }
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException();
    }
}
