/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import com.googlecode.eckoit.events.LiferecorderSyncProcessMessage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bushe.swing.event.EventBus;
import org.jaudiotagger.audio.mp3.MP3File;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;

/**
 *
 * @author ryan
 */
public class SansaClipLiferecorder implements Recorder {

    private long splitMillisecondTollerance;
    private RecordingTasks recordingTasks;
    private long milliSecondsBeforeMark;
    private long milliSecondsAfterMark;
    private long splitTime = 600;
    private long splitTimeTolerance = 1;


    @Override
    public File getRecordingDir(File root) {
        File recordDir = new File(root, "RECORD");
        if (!recordDir.exists()) return null;
        return new File(recordDir, "VOICE");
    }


    @Override
    public List<RecordingWithInterval> findRecordings(File root) {
        if (!root.isDirectory()) throw new IllegalArgumentException("The file provided is not a directory");
        if (!root.exists()) throw new IllegalArgumentException("The file provided is not a directory");

        List<RecordingWithInterval> recordings = new ArrayList<RecordingWithInterval>();
        for (File file : root.listFiles()) {
            if (file.getName().toLowerCase().endsWith(".mp3")) {
                RecordingWithInterval recording = new RecordingWithInterval();
                recording.setFile(file);
                recording.setInterval(findInterval(file));
                recordings.add(recording);
            }
        }
        return recordings;
    }

    @Override
    public List<Instant> findMarks(List<RecordingWithInterval> recordings) {
        List<Instant> marks = new ArrayList<Instant>();
        sortRecordings(recordings);


        EventBus.publish(new LiferecorderSyncProcessMessage("Processing Tags", 4, 100));
        RecordingWithInterval lastRecording = null;
        for (int i = 0; i < (recordings.size() - 1); i++) {
            RecordingWithInterval recording = recordings.get(i);
            RecordingWithInterval next = null;
            try {next = recordings.get(i + 1);} catch (IndexOutOfBoundsException ex) {}
            if (isAMark(lastRecording, recording, next, splitMillisecondTollerance)) {
                //System.out.println("mark: " + recording.getInterval().getEnd());
                marks.add(new Instant(recording.getInterval().getEnd()));
            }
            
            lastRecording = recording;
        }
        return marks;
    }

    public List<RecordingWithInterval> sliceUp(List<RecordingWithInterval> recordings, List<Instant> marks) throws TaskException {
        List<Interval> markIntervals = findMarkInterval(marks);

        List<RecordingWithInterval> resultRecordings = new ArrayList<RecordingWithInterval>(recordings);



        for (Interval markInterval : markIntervals){
            RecordingWithInterval[] firstSecond = findRecordingsForMark(markInterval, recordings);
            // split the first
            SplitOperation op1 = new SplitOperation();
            op1.setRecording(firstSecond[0]);
            op1.setSplitTime(new Instant(markInterval.getStart()));
            RecordingWithInterval[] firstSplit = recordingTasks.split(op1);


            // split the second
            SplitOperation op2 = new SplitOperation();
            op2.setRecording(firstSecond[1]);
            op2.setSplitTime(new Instant(markInterval.getEnd()));
            RecordingWithInterval[] secondSplit = recordingTasks.split(op2);


            // join the inner
            JoinOperation jop = new JoinOperation();
            jop.setFirstRecording(firstSplit[1]);
            jop.setSecondRecording(secondSplit[0]);
            RecordingWithInterval inner = recordingTasks.join(jop);
            // delete the old temp
            firstSplit[1].getFile().delete();
            secondSplit[0].getFile().delete();

            // remove the altered recordings add add the new ones
            resultRecordings.remove(firstSecond[0]);
            resultRecordings.remove(firstSecond[1]);

            resultRecordings.add(firstSplit[0]);
            resultRecordings.add(inner);
            resultRecordings.add(secondSplit[1]);


        }
        return resultRecordings;
    }

    protected RecordingWithInterval[] findRecordingsForMark(Interval markInterval, List<RecordingWithInterval> recordings) {
        RecordingWithInterval first = null;
        RecordingWithInterval second = null;
        for (RecordingWithInterval interval : recordings) {
            if (markInterval.isAfter(interval.getInterval().getStart())) {
                first = interval;
            }
            if (markInterval.isBefore(interval.getInterval().getEnd())) {
                second = interval;
            }
            if (first != null && second != null) break;
        } 
        if (first != null && second != null && !first.equals(second)) {
            return new RecordingWithInterval[]{first, second};
        }
        return null;
    }



    @Override
    public List<Interval> findMarkInterval(List<Instant> marks) {
        List<Interval> markIntervals = new ArrayList<Interval>();
        for (Instant mark : marks) {
            Instant start = mark.minus(milliSecondsBeforeMark);
            Instant end = mark.plus(milliSecondsAfterMark);
            markIntervals.add(new Interval(start, end));
        }
        return markIntervals;
    }

    
    protected void sortRecordings(List<RecordingWithInterval> recordings) {
        Collections.sort(recordings, new Comparator<RecordingWithInterval>() {
            @Override
            public int compare(RecordingWithInterval o1, RecordingWithInterval o2) {
                if (o1.getInterval().getStart().isBefore(o2.getInterval().getStart())) return -1;
                if (o1.getInterval().getStart().isAfter(o2.getInterval().getStart())) return 1;
                return 0;
            }
        });

    }
    public boolean isAMark(RecordingWithInterval last, RecordingWithInterval recording, RecordingWithInterval next, long tolerenceMilliseconds) {
        // if the file is less than split time, 
        // and (
        //   if the previous recording is null orr
        //   the difference between the next recording end and this begginning is less than the tolerance
        // )
        // and this not the last one
        
        long checkDuration = splitTime - 1;
        if (checkDuration <= 0) checkDuration = 0;

        long duration = recording.getInterval().toDurationMillis();
        //System.out.println("Checking: " + recording.getFile().getName());
        //System.out.println("duration " + TimeUtils.formatTimeBySec(duration/1000, false));
        //System.out.println("less than " + TimeUtils.formatTimeBySec(checkDuration , false) + "?");
        boolean lessThanSplitTime = false;
        if (recording.getInterval().toDurationMillis() < ((checkDuration) * 1000)) {
            lessThanSplitTime = true;
        }
        boolean lastRecordingNull = (last == null);
        boolean gapSmall = false;
        if (next != null) {
            long nextStart   = next.getInterval().getStartMillis();
            long thisEnd = recording.getInterval().getEndMillis();
            long diff = nextStart - thisEnd;
            //System.out.println("diff to next: " + diff);


            if (diff <= tolerenceMilliseconds) gapSmall = true;
        }

        System.out.println(lessThanSplitTime + " " + lastRecordingNull + " " + gapSmall);

        if (lessThanSplitTime && gapSmall) return true;
       // if (lessThanSplitTime && !lastRecordingNull && gapSmall) return true;
        

        return false;
    }



    public Interval findInterval(File file) {
        String fileName = file.getName();
        LocalDateTime start = parseFileStartDate(fileName);
        DateTime end = new DateTime( file.lastModified());

        MP3File mp3;
        try {
            mp3 = new MP3File(file);
            int length = mp3.getAudioHeader().getTrackLength();
            //if ( (length + splitTimeTolerance) > splitTime  ) {
            //    length = (int)splitTime;
           // }
            //Tag tag = mp3.createDefaultTag();
            //tag.
            //mp3.save();
            end = start.plusSeconds(length).toDateTime();

        } catch (Exception ex) {
            Logger.getLogger(SansaClipLiferecorder.class.getName()).log(Level.SEVERE, null, ex);
        }


        



        return new Interval(start.toDateTime(), end);
    }


    public LocalDateTime parseFileStartDate(String filename) {
        Pattern pattern = Pattern.compile("R_MIC_(\\d{2})(\\d{2})(\\d{2})-(\\d{2})(\\d{2})(\\d{2})\\.mp3");
        Matcher m = pattern.matcher(filename);

        if (m.matches()) {
            int year = Integer.parseInt("20" + m.group(1));
            int month = Integer.parseInt(m.group(2));
            int day = Integer.parseInt(m.group(3));
            int hour = Integer.parseInt(m.group(4));
            int min = Integer.parseInt(m.group(5));
            int sec = Integer.parseInt(m.group(6));

            return new LocalDateTime(year, month, day, hour, min, sec);
        }
        throw new IllegalArgumentException("The filename is invalid: " + filename);

    }

    /**
     * @return the splitMillisecondTollerance
     */
    public long getSplitMillisecondTollerance() {
        return splitMillisecondTollerance;
    }

    /**
     * @param splitMillisecondTollerance the splitMillisecondTollerance to set
     */
    public void setSplitMillisecondTollerance(long splitMillisecondTollerance) {
        this.splitMillisecondTollerance = splitMillisecondTollerance;
    }

    /**
     * @return the recordingTasks
     */
    public RecordingTasks getRecordingTasks() {
        return recordingTasks;
    }

    /**
     * @param recordingTasks the recordingTasks to set
     */
    public void setRecordingTasks(RecordingTasks recordingTasks) {
        this.recordingTasks = recordingTasks;
    }

    /**
     * @return the secondsBeforeMark
     */
    public long getSecondsBeforeMark() {
        return milliSecondsBeforeMark;
    }

    /**
     * @param secondsBeforeMark the secondsBeforeMark to set
     */
    public void setSecondsBeforeMark(long secondsBeforeMark) {
        this.milliSecondsBeforeMark = secondsBeforeMark;
    }

    /**
     * @return the secondsAfterMark
     */
    public long getSecondsAfterMark() {
        return milliSecondsAfterMark;
    }

    /**
     * @param secondsAfterMark the secondsAfterMark to set
     */
    public void setSecondsAfterMark(long secondsAfterMark) {
        this.milliSecondsAfterMark = secondsAfterMark;
    }

    /**
     * @return the splitTime
     */
    public long getSplitTime() {
        return splitTime;
    }

    /**
     * @param splitTime the splitTime to set
     */
    public void setSplitTime(long splitTime) {
        this.splitTime = splitTime;
    }

    /**
     * @return the splitTimeTolerance
     */
    public long getSplitTimeTolerance() {
        return splitTimeTolerance;
    }

    /**
     * @param splitTimeTolerance the splitTimeTolerance to set
     */
    public void setSplitTimeTolerance(long splitTimeTolerance) {
        this.splitTimeTolerance = splitTimeTolerance;
    }

    @Override
    public Map<String,String> getMD5s(List<RecordingWithInterval> recordings) {
        Map<String,String> md5s = new HashMap<String,String>();
        for (RecordingWithInterval recording : recordings) {
            try {
                md5s.put(recording.getFile().getName(),recording.getMD5());
            } catch (IOException ex) {
                Logger.getLogger(SimpleAudioDirectory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return md5s;
    }


}
