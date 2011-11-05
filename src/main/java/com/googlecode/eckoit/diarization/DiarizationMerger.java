/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.diarization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ryan
 */
public class DiarizationMerger {

    public List<DiarizationSegment> join(File[] files, long splitTime, String conversation) throws FileNotFoundException, IOException {
        long currentSplitTime = 0;
        List<DiarizationSegment> segments = new ArrayList<DiarizationSegment>();
        for(int i=0; i < files.length; i++) {
            File file = files[i];
            List<DiarizationSegment> local_segments = load(file, currentSplitTime, conversation);
            segments.addAll(local_segments);
            currentSplitTime += splitTime;
        }
        return segments;
    }

    public List<DiarizationSegment> load(File file, long offset, String conversation) throws FileNotFoundException, IOException {
        List<DiarizationSegment> segments = new ArrayList<DiarizationSegment>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line=reader.readLine()) != null) {
            DiarizationSegment segment = parseLine(line, offset, conversation);
            segments.add(segment);
        }
        return segments;
    }

    public DiarizationSegment parseLine(String line, long offset, String conversation) {
        DiarizationSegment segment = new DiarizationSegment();
        segment.setConversation(conversation);

        String[] pieces = line.split("\t");
        //segment.setId(pieces[0]);
        String[] speaker = pieces[1].split("-");
        segment.setGender(speaker[0]);
        segment.setSpeaker(speaker[1]);

        long seconds = (long)Float.parseFloat(pieces[2]);
        segment.setSeconds(offset + seconds);

        float duration = Float.parseFloat(pieces[3]);
        segment.setDuration((long) duration);

        return segment;
    }

}
