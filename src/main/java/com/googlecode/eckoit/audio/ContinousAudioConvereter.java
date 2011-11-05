/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.eckoit.audio;

import com.github.couchapptakeout.ExitApplicationMessage;
import com.googlecode.eckoit.PropertiesStorage;
import com.googlecode.eckoit.events.ConversionFinishedEvent;

import com.googlecode.eckoit.events.MeetingFinalProcessingEvent;
import com.googlecode.eckoit.events.RecordingFinishedEvent;
import com.googlecode.eckoit.events.RecordingSplitEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

/**
 * Scans a source directory for any new files, converts them and copies them into the
 * destination directory
 * @author ryan
 */
public class ContinousAudioConvereter extends Thread implements  EventSubscriber<RecordingSplitEvent>  {

    
    private File sourceDir;
    private File destDir;
    private PropertiesStorage properties;
    private boolean running = true;
    private LinkedBlockingQueue<RecordingSplitEvent> completedRecordings;

    public ContinousAudioConvereter(PropertiesStorage properties, File sourceDir, File destDir) {
        this.sourceDir = sourceDir;
        this.destDir = destDir;
        this.properties = properties;
        this.completedRecordings = new LinkedBlockingQueue<RecordingSplitEvent>();
        
        EventBus.subscribeStrongly(RecordingSplitEvent.class, this);
        EventBus.subscribeStrongly(ExitApplicationMessage.class, new EventSubscriber<ExitApplicationMessage>() {
            @Override
            public void onEvent(ExitApplicationMessage t) {
                running = false;
            }
        });

        
    }

    @Override
    public void run() {

        while (running) {
            try {
                RecordingSplitEvent recordingFinished = completedRecordings.take();
                File wav = recordingFinished.getFinishedFile();
                if (tooFresh(wav)) {
                    completedRecordings.add(recordingFinished);
                } else {
                    doConversion(wav);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ContinousAudioConvereter.class.getName()).log(Level.SEVERE, null, ex);
            }
            sleep();
        }
        
        
    }

    protected void doConversion(File wav) {
        File mp3 = null;
        File ogg = null;
        try {
            EventBus.publish(new MeetingFinalProcessingEvent("Converting to mp3", 0.02f));
            mp3 = convertToMP3(wav);
            EventBus.publish(new MeetingFinalProcessingEvent("Converting to ogg", 0.10f));
            ogg = convertToOGG(wav);
        } catch (Exception ex) {
            Logger.getLogger(ContinousAudioConvereter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (ogg != null && ogg.exists() && mp3 != null && mp3.exists()) {
            EventBus.publish(new ConversionFinishedEvent(wav));
        }
    }

    /**
     * If the file is too fresh, we dont want to begin conversion
     * @param wav
     * @return
     */
    private boolean tooFresh(File wav) {
        long now = System.currentTimeMillis();
        long timestamp = wav.lastModified();
        if ((now - timestamp) < 2000) return true;
        else return false;
    }


    private synchronized File convertToMP3(File wav) throws InterruptedException, IOException {
        File mp3 = getFileForDocument(wav, ".mp3");
        if (mp3.exists()) return mp3;

        String ffmpegcmd = properties.loadProperty("ffmpegcmd");
        if (ffmpegcmd == null || "".equals(ffmpegcmd)) {
            return null;
        }
        long bitrate;
        try {
            bitrate = Long.parseLong(properties.loadProperty("bitrate")) * 1000;
        } catch (Exception e) {
            bitrate = 24000L;
        }
        long frequency = 16000L;

        File mp3Temp = getFileForDocument(wav, ".mp3.tmp");
        FFMpegConverter converter = new FFMpegConverter(ffmpegcmd, FFMpegConverter.ENCODER_MP3);
        //System.out.println("Converting to mp3");
        converter.convert(wav, bitrate, frequency, mp3Temp, true);
        //System.out.println("Renaming");
        mp3Temp.renameTo(mp3);
        return mp3;
    }
    private synchronized File convertToOGG(File wav) throws InterruptedException, IOException {
        File ogg = getFileForDocument(wav, ".ogg");
        if (ogg.exists()) return ogg;

        String ffmpegcmd = properties.loadProperty("ffmpegcmd");
        if (ffmpegcmd == null || "".equals(ffmpegcmd)) {
            return null;
        }
        long bitrate;
        try {
            bitrate = Long.parseLong(properties.loadProperty("bitrate")) * 1000;
        } catch (Exception e) {
            bitrate = 24000L;
        }
        long frequency;
        try {
            frequency = Long.parseLong(properties.loadProperty("frequency"));
        } catch (Exception e) {
            frequency = 22050L;
        }
        File oggTemp = getFileForDocument(wav, "tmp.ogg");
        FFMpegConverter converter = new FFMpegConverter(ffmpegcmd, FFMpegConverter.ENCODER_VORBIS);

        converter.convert(wav, bitrate, frequency, oggTemp, true);
        oggTemp.renameTo(ogg);
        return ogg;
    }

    private File getFileForDocument(File wav, String suffix) {
        // just do sibblings
        File parent = wav.getParentFile();
        String recordingId = wav.getName().substring(0, wav.getName().lastIndexOf('.'));
        return new File(parent, recordingId + suffix);
    }




    private void sleep() {
        try {
            sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ContinousAudioConvereter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onEvent(RecordingSplitEvent recordingSplit) {
        completedRecordings.add(recordingSplit);
    }

}
