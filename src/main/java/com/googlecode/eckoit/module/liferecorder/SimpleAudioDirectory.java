/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.audio.ogg.util.OggInfoReader;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;

/**
 *
 * @author ryan
 */
public class SimpleAudioDirectory implements Recorder{




    @Override
    public List<Interval> findMarkInterval(List<Instant> marks) {
        List<Interval> interval = new ArrayList<Interval>();
        return interval;
    }

    @Override
    public List<Instant> findMarks(List<RecordingWithInterval> recordings) {
        return new ArrayList<Instant>();
    }

    @Override
    public List<RecordingWithInterval> findRecordings(File root) {

        List<RecordingWithInterval> recordings = new ArrayList<RecordingWithInterval>();
        if (root == null) return recordings;
        if (root.isDirectory()) {
            File[] children = root.listFiles();
            for (int i=0; i < children.length; i++) {
                recordings.addAll(findRecordings(children[i]));
            }
            
        } else if (root.isFile()) {
            try {
                recordings.add(buildRecording(root));
            } catch(Exception e) {
                Logger.getLogger(SimpleAudioDirectory.class.getName()).log(Level.INFO, "The file " + root.getAbsolutePath() + " has been ignored");
            }
        }
        return recordings;
    }


    public RecordingWithInterval buildRecording(File single) {
        RecordingWithInterval rwi = new RecordingWithInterval();
        rwi.setFile(single);
        rwi.setInterval(findInterval(single));
        return rwi;
    }

    public Interval findInterval(File file) {
        String fileName = file.getName();
        LocalDateTime start = parseFileStartDate(fileName);
        DateTime end = new DateTime( file.lastModified());

        int length = 0;
        if (file.getName().endsWith(".mp3")) {
           length = findMP3Duration(file);
        }
        if (file.getName().endsWith(".ogg")) {
            length = findOGGDuration(file);
        }
        end = start.plusSeconds(length).toDateTime();
        return new Interval(start.toDateTime(), end);
       
    }


    public int findMP3Duration(File file) {

        try {
            MP3File mp3;
            mp3 = new MP3File(file);
            return mp3.getAudioHeader().getTrackLength();

        } catch (Exception ex) {
            Logger.getLogger(SimpleAudioDirectory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public int findOGGDuration(File file) {

        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");

            OggInfoReader reader = new OggInfoReader();
            GenericAudioHeader header =   reader.read(raf);
            return header.getTrackLength();

        } catch (Exception ex) {
            Logger.getLogger(SimpleAudioDirectory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }







    public LocalDateTime parseFileStartDate(String filename) {
        Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})-(\\d{2})-(\\d{2})-(\\d{2})\\.\\w{3}");
        Matcher m = pattern.matcher(filename);

        if (m.matches()) {
            int year = Integer.parseInt(m.group(1));
            int month = Integer.parseInt(m.group(2));       
            int day = Integer.parseInt(m.group(3));
            int hour = Integer.parseInt(m.group(4));
            int min = Integer.parseInt(m.group(5));
            int sec = Integer.parseInt(m.group(6));

            return new LocalDateTime(year, month, day, hour, min, sec);
        }
        throw new IllegalArgumentException("The filename is invalid: " + filename);

    }


    @Override
    public File getRecordingDir(File root) {
        return root;
    }

    @Override
    public Map<String,String> getMD5s(List<RecordingWithInterval> recordings) {
        Map<String,String> md5s = new HashMap<String,String>();
        for (RecordingWithInterval recording : recordings) {
            try {
                md5s.put(recording.getFile().getName(), recording.getMD5());
            } catch (IOException ex) {
                Logger.getLogger(SimpleAudioDirectory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return md5s;
    }

}
