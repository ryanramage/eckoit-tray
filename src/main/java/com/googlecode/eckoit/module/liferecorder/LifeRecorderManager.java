/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import com.github.couchapptakeout.TrayMessage;
import com.googlecode.eckoit.Slugger;
import com.googlecode.eckoit.events.LifeRecorderAttachedEvent;
import com.googlecode.eckoit.events.LifeRecorderDetachedEvent;
import com.googlecode.eckoit.events.LiferecorderSyncProcessMessage;
import com.googlecode.eckoit.events.ShowDashboardMessage;
import com.googlecode.eckoit.tray.Messages;
import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.AttachmentInputStream;
import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentOperationResult;
import org.ektorp.Revision;
import org.ektorp.http.HttpClient;
import org.ektorp.http.RestTemplate;
import org.joda.time.Instant;
import org.joda.time.Interval;

/**
 *
 * @author ryan
 */
public class LifeRecorderManager {

    public static final String LIFE_RECORDER_SETTINGS = ".liferecorder";
    public static final long checkIntervalMilliSeconds = 5000;
    File recordingDirectory;
    LifeRecorderDaemon daemon;
    Uploader uploader;
    CouchDbConnector wikiConnector;
    HttpClient couchHttpClient;
    Recorder recorder;
    final Messages messages;
    boolean isAttached = false;

    public LifeRecorderManager(Recorder recorder, File recordingDirectory, Uploader uploader, CouchDbConnector wikiConnector, HttpClient couchHttpClient, final Messages messages, List<String> roots) {
        this.messages = messages;
        this.recorder = recorder;
        this.recordingDirectory = recordingDirectory;
        this.uploader = uploader;
        this.wikiConnector = wikiConnector;
        this.couchHttpClient = couchHttpClient;
        daemon = new LifeRecorderDaemon(roots, LIFE_RECORDER_SETTINGS, checkIntervalMilliSeconds);
        EventBus.subscribeStrongly(LifeRecorderAttachedEvent.class, new EventSubscriber<LifeRecorderAttachedEvent>() {
            @Override
            public void onEvent(LifeRecorderAttachedEvent t) {
                attachedEvent(t);
            }
        });

        EventBus.subscribeStrongly(LifeRecorderDetachedEvent.class, new EventSubscriber<LifeRecorderDetachedEvent>() {
            @Override
            public void onEvent(LifeRecorderDetachedEvent t) {
                detachedEvent(t);
            }
        });
        

    }

    private synchronized void attachedEvent(LifeRecorderAttachedEvent t) {
        if (isAttached) return;
        String message = messages.getLocalMessage(Messages.LIFE_RECORDER_ATTACHED_TITLE);
        EventBus.publish(new TrayMessage(message, message, MessageType.INFO));
        new Thread(new SyncRecorder(t)).start();
    }

    private synchronized void detachedEvent(LifeRecorderDetachedEvent t) {
        isAttached = false;
        String message = messages.getLocalMessage(Messages.LIFE_RECORDER_DETATCHED_TITLE);
        EventBus.publish(new TrayMessage(message, message, MessageType.INFO));
    }



    private class SyncRecorder implements Runnable {
        LifeRecorderAttachedEvent event;

        public SyncRecorder(LifeRecorderAttachedEvent event) { this.event = event; }

        @Override
        public void run() {
            offloadFilesToCouch(event.getRoot());
            //syncDocsToFiles(event.getRoot());
            isAttached = false; // just means the attached Event is safe again
        }
    }

    protected void offloadFilesToCouch(File root) {
        File recordDir = recorder.getRecordingDir(root);
        // copy all files off the device to a new folder on disk
        File toDir = new File(recordingDirectory, new Date().getTime() + "");
        
        try {
            Logger.getLogger(LifeRecorderManager.class.getName()).log(Level.INFO, "Making Directory: " + toDir.getName());
            toDir.mkdirs();
            Logger.getLogger(LifeRecorderManager.class.getName()).log(Level.INFO, "Copinging files from: " + recordDir.getName());
            doCopyDirectory(recordDir, toDir, true);

            Logger.getLogger(LifeRecorderManager.class.getName()).log(Level.INFO, "Uploading Liferecorded files to couch");
            uploader.uploadFilesToCouch(toDir, recorder);

            // remove all files from the source dir (this should be an option?)
            // also should verify md5 or something?
            FileUtils.cleanDirectory(recordDir);

            String message = messages.getLocalMessage(Messages.LIFE_RECORDER_SYNC_COMPLETE);
            EventBus.publish(new TrayMessage(message, message, MessageType.INFO));
            EventBus.publish(new ShowDashboardMessage());
        } catch (IOException ex) {
            Logger.getLogger(LifeRecorderManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


     private void doCopyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
         // recurse
         File[] files = srcDir.listFiles() ;
         if (files == null) {  // null if security restricted
             throw new IOException("Failed to list contents of " + srcDir);
         }
         if (destDir.exists()) {
             if (destDir.isDirectory() == false) {
                 throw new IOException("Destination '" + destDir + "' exists but is not a directory");
             }
         } else {
             if (destDir.mkdirs() == false) {
                 throw new IOException("Destination '" + destDir + "' directory cannot be created");
             }
         }
         if (destDir.canWrite() == false) {
             throw new IOException("Destination '" + destDir + "' cannot be written to");
         }
         int i = 0;
         int totalFiles = files.length;
         for (File file : files) {
             File copiedFile = new File(destDir, file.getName());
             if (file.isDirectory()) {
                 // we ignore...for now we know this is flat
             } else {
                 FileUtils.copyFile(file, copiedFile, preserveFileDate);
             }

             float percentComplete = (i++ / totalFiles) / 2;
             EventBus.publish(new LiferecorderSyncProcessMessage("Coping Files : " + i + " of " + totalFiles, i, totalFiles));
         }
         // Do this last, as the above has probably affected directory metadata
         if (preserveFileDate) {
             destDir.setLastModified(srcDir.lastModified());
         }
     }


    protected void syncDocsToFiles(File root) {
        Logger.getLogger(LifeRecorderManager.class.getName()).log(Level.INFO, "Sync topics to life recorder started.");
        File docDir = getDocDir(root);
        List<String> ids = wikiConnector.getAllDocIds();
        for (String id : ids) {
            try {
                String f_id = Slugger.generateSlug(id);
                File docFile = new File(docDir, f_id + ".txt");
                docFile.createNewFile();
                FileOutputStream out = new FileOutputStream(docFile);
                RestTemplate t = new RestTemplate(couchHttpClient);
                InputStream is = t.get("/wikid/_design/text/_show/full/" + id).getContent();
                IOUtils.copy(is, out);
                out.close();
            } catch (Exception ex) {
                Logger.getLogger(LifeRecorderManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        Logger.getLogger(LifeRecorderManager.class.getName()).log(Level.INFO, "Sync topics to life recorder complete.");
    }


    private File getDocDir(File root) {
        File docDir = new File(root, "docs");
        if (!docDir.exists()) {
            docDir.mkdir();
        }
        return docDir;
    }



    public void start() {
        new Thread(daemon).start();
    }

}
