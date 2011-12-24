/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import com.googlecode.eckoit.events.LiferecorderSyncProcessMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.bushe.swing.event.EventBus;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.AttachmentInputStream;
import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentOperationResult;
import org.ektorp.Revision;
import org.joda.time.Instant;
import org.joda.time.Interval;

/**
 *
 * @author ryan
 */
public class Uploader {

    CouchDbConnector wikiConnector;
    

    public Uploader (CouchDbConnector wikiConnector){

        this.wikiConnector = wikiConnector;
    }

    public Map<String,String> uploadFilesToCouch(File dir, Recorder recorder) {
        List<RecordingWithInterval> recordings = recorder.findRecordings(dir);
        List<Instant> marks = recorder.findMarks(recordings);
        if (marks != null) {
            Logger.getLogger(Uploader.class.getName()).log(Level.INFO, "Uploading Final Recordings: " + marks.size());
        }
        //recorder.getMD5s(recordings);
        Map<String,String> md5s = uploadFinalRecordings(recordings);
        List<Interval> markIntervals = recorder.findMarkInterval(marks);
        if (markIntervals != null) {
            Logger.getLogger(Uploader.class.getName()).log(Level.INFO, "Uploading Marks: " + markIntervals.size());
        }
        uploadMarks(markIntervals);
        return md5s;
    }

    protected void uploadMarks(List<Interval>  marks) {
        List<JsonNode> bulkJsonFormat = translateMarks(marks);
        wikiConnector.executeBulk(bulkJsonFormat);
    }
    protected List<JsonNode> translateMarks(List<Interval>  marks) {
        List<JsonNode> results = new ArrayList<JsonNode>();
        for (Interval mark : marks) {
            JsonNode translation = translate(mark);
            results.add(translation);
        }
        return results;
    }
    protected JsonNode translate(Interval mark) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.put("liferecorder", true);


        root.put("type", "com.eckoit.liferecorder.mark");
        root.put("timestamp", mark.getStartMillis());



        ObjectNode markObj = mapper.createObjectNode();
        markObj.put("start", mark.getStartMillis());
        markObj.put("length", mark.toDurationMillis() / 1000);
        root.put("mark", markObj);
        return root;
    }

    protected Map<String,String> uploadFinalRecordings(List<RecordingWithInterval> finalRecordings) {
        Map<String,String> finalMD5s = new HashMap<String, String>();
        List<JsonNode> bulkJsonFormat = translate(finalRecordings);
        List<DocumentOperationResult> uploadResults = wikiConnector.executeBulk(bulkJsonFormat);
        for(int i=0; i < finalRecordings.size(); i++) {
            RecordingWithInterval recording = finalRecordings.get(i);
            //DocumentOperationResult result = uploadResults.get(i);

            // these should match based on order!
            FileInputStream fis;
            try {
                List<Revision> revisions = wikiConnector.getRevisions(recording.getId());
                String revision = revisions.get(0).getRev();
                fis = new FileInputStream(recording.getFile());
                AttachmentInputStream ais = new AttachmentInputStream(recording.getFile().getName(),fis, "audio/mp3");
                Logger.getLogger(Uploader.class.getName()).log(Level.INFO, "Uploading: " + recording.getFile().getName());
                wikiConnector.createAttachment(recording.getId(), revision, ais);
                ais.close();
                fis.close();
                Logger.getLogger(Uploader.class.getName()).log(Level.INFO, "Uploading Complete: " + recording.getFile().getName());

                float percentComplete = ((i / finalRecordings.size()) / 2) + .5f;
                EventBus.publish(new LiferecorderSyncProcessMessage("Uploading Locally: " + i + " of " + finalRecordings.size(), i, finalRecordings.size()));

                String serverMD5 = findRecordingMD5(recording.getId());
                //assert serverMD5.equals(recording.getMD5());
                
                finalMD5s.put(recording.getFile().getName(), serverMD5);


                // slow this down for a mac?
                if (SystemUtils.IS_OS_MAC) {
                    Thread.sleep(500);
                }

            } catch (Exception ex) {
                Logger.getLogger(Uploader.class.getName()).log(Level.SEVERE, null, ex);
            }



        }
        return finalMD5s;
    }


    protected String findRecordingMD5(String docId) {
        ObjectNode doc = wikiConnector.get( ObjectNode.class, docId);
        JsonNode attach = doc.get("_attachments").getElements().next();
        String serverMD5 = attach.get("digest").getTextValue();
        return serverMD5.substring(4); // remove the 'md5-' prefix couch has
    }


    protected List<JsonNode> translate(List<RecordingWithInterval> finalRecordings) {
        List<JsonNode> results = new ArrayList<JsonNode>();
        for (RecordingWithInterval recording : finalRecordings) {
            JsonNode translation = translate(recording);
            results.add(translation);
        }
        return results;
    }

    protected JsonNode translate(RecordingWithInterval recordingInterval) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.put("_id", recordingInterval.getId());
        root.put("liferecorder", true);

        ObjectNode recording = mapper.createObjectNode();
        recording.put("start", recordingInterval.getInterval().getStartMillis());
        recording.put("length", recordingInterval.getInterval().toDurationMillis() / 1000);
        root.put("recording", recording);
        return root;
    }
}
