/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.audio;

import com.github.couchapptakeout.TrayMessage;
import com.googlecode.eckoit.diarization.DiarizationSegment;
import com.googlecode.eckoit.events.DiarizationFinishedEvent;

import com.googlecode.eckoit.events.MeetingFinalProcessingEvent;
import com.googlecode.eckoit.events.RecordingFinishedEvent;
import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.SystemUtils;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.ektorp.Attachment;
import org.ektorp.AttachmentInputStream;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.Revision;
import org.ektorp.impl.StdCouchDbConnector;

/**
 *
 * @author ryan
 */
public class RecordingFinishedManager implements  EventSubscriber<RecordingFinishedEvent> {



    private RecordingFinishedEvent lastFinished;
    private RecordingFinishedHelper helper;
    private CouchDbInstance dbInstance;

    public RecordingFinishedManager(CouchDbInstance dbInstance, RecordingFinishedHelper recordingFinishedHelper) {
        this.helper = recordingFinishedHelper;
        this.dbInstance = dbInstance;
        EventBus.subscribeStrongly(RecordingFinishedEvent.class, this);





    }

    @Override
    public void onEvent(RecordingFinishedEvent t) {
        System.out.println("Rememberting recording id: " + t.getRecordingId());
        lastFinished = t;

        

    }


    public void DiarizationComplete(File f) {
       try {
            if (!f.getAbsolutePath().contains(lastFinished.getRecordingId())) return;


            CouchDbConnector wikiConnector = new StdCouchDbConnector(lastFinished.getLastRecordingDB(), dbInstance);

            

            System.out.println("Joining Audio");
            EventBus.publish(new MeetingFinalProcessingEvent("Merging Recordings", 0.30f));
            File[] audioFiles = helper.recordingFinished(lastFinished.getRecordingId());
            System.out.println("Joining Audio Complete");

            System.out.println("Putting Audio on Couch");
            String revision;
            {
                System.out.println("Putting mp3 on Couch");
                EventBus.publish(new MeetingFinalProcessingEvent("Uploading Recording .mp3", 0.40f));
                // mp3
                FileInputStream fis = new FileInputStream(audioFiles[0]);
                AttachmentInputStream ais = new AttachmentInputStream("-recording1.mp3",fis, "audio/mp3");
                List<Revision> revisions = wikiConnector.getRevisions(lastFinished.getRecordingId());
                revision = revisions.get(0).getRev();
                revision = wikiConnector.createAttachment(lastFinished.getRecordingId(), revision, ais);
                ais.close();
                fis.close();
                System.out.println("Putting mp3 on Couch complete");
            }
            {
                // this is breaking on the mac...not sure why yet
                if (!SystemUtils.IS_OS_MAC) {
                    EventBus.publish(new MeetingFinalProcessingEvent("Uploading Recording .ogg", 0.50f));
                    System.out.println("Putting ogg on Couch");
                    // ogg
                    FileInputStream fis = new FileInputStream(audioFiles[1]);
                    if (fis != null) {
                        AttachmentInputStream ais = new AttachmentInputStream("-recording1.ogg",fis, "audio/ogg");
                        revision = wikiConnector.createAttachment(lastFinished.getRecordingId(), revision, ais);
                        ais.close();
                        fis.close();
                        System.out.println("Putting ogg on Couch complete");
                    }
                }

            }
            System.out.println("Putting Audio on Couch Complete");

            try {
                List<DiarizationSegment> segments = helper.mergeDiarizations(lastFinished.getRecordingId());
                if (segments.size() > 0) {
                    System.out.println("Loading Diarizations");
                    EventBus.publish(new MeetingFinalProcessingEvent("Uploading Interresting Moments", 0.60f));
                    wikiConnector.executeBulk(segments);
                    System.out.println("Loading Diarizations Complete");
                }
            }
            catch(Exception e) {
                System.out.println("Error duing diarizations");
            }
            // load any screenshots
            List<File> screenShots = helper.findScreenShots(lastFinished.getRecordingId());
            if (screenShots.size() > 0) {
                EventBus.publish(new MeetingFinalProcessingEvent("Uploading Screenshots", 0.80f));
                Map<String,Object> screenShotDoc = new HashMap<String, Object>();
                screenShotDoc.put("type", "screenShots");
                screenShotDoc.put("meeting", lastFinished.getRecordingId());
                wikiConnector.create(screenShotDoc);
                String id = (String) screenShotDoc.get("_id");
                String rev = (String) screenShotDoc.get("_rev");
                System.out.println("Loading Screenshots: " + id);
                for (File file : screenShots) {
                    FileInputStream fis = new FileInputStream(file);
                    String name = file.getName().split("\\.")[0];
                    AttachmentInputStream ais = new AttachmentInputStream(name,fis, "image/png");
                    rev = wikiConnector.createAttachment(id, rev, ais);
                    ais.close();
                    fis.close();
                }
                System.out.println("Loading Screenshots Complete ");
            }
            String id = lastFinished.getRecordingId();
            


            EventBus.publish(new TrayMessage("Meeting Upload Complete", "Meeting upload is complete.", MessageType.INFO));
            EventBus.publish(new MeetingFinalProcessingEvent(MeetingFinalProcessingEvent.FINISHED));
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(RecordingFinishedManager.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } 
    }


}
