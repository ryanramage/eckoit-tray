package com.googlecode.eckoit.tray;

import com.github.couchapptakeout.AddMenuItemEvent;
import com.github.couchapptakeout.ShowApplicationUrlMessage;
import com.googlecode.eckoit.PropertiesPropertiesStorage;
import com.googlecode.eckoit.PropertiesStorage;
import com.googlecode.eckoit.audio.ContinousAudioConvereter;
import com.googlecode.eckoit.audio.FFMpegSplitter;
import com.googlecode.eckoit.audio.RecordingFinishedHelper;
import com.googlecode.eckoit.audio.RecordingFinishedManager;
import com.googlecode.eckoit.audio.SplitAudioRecorder;
import com.googlecode.eckoit.bookmarkHelper.BookmarkDropTargetWindow;
import com.googlecode.eckoit.diarization.Diarizer;
import com.googlecode.eckoit.events.LifeRecorderAttachedEvent;
import com.googlecode.eckoit.events.MeetingFinalProcessingEvent;
import com.googlecode.eckoit.events.ShowDashboardMessage;
import com.googlecode.eckoit.module.liferecorder.FFMpegRecordingTasks;
import com.googlecode.eckoit.module.liferecorder.LifeRecorderManager;
import com.googlecode.eckoit.module.liferecorder.SansaClipLiferecorder;
import com.googlecode.eckoit.module.liferecorder.UploadDirDialog;
import com.googlecode.eckoit.module.liferecorder.Uploader;
import com.googlecode.eckoit.replication.ReplicationManager;
import com.googlecode.eckoit.screengrab.ScreenGrabber;
import com.googlecode.eckoit.util.FFMpegLocator;
import java.awt.Desktop;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventServiceLocator;
import org.bushe.swing.event.EventSubscriber;
import org.bushe.swing.event.ThreadSafeEventService;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;


/**
 * Hello world!
 *
 */
public class App  {

    private PropertiesStorage p_storage;
    FileHandler handler;
    Logger logger;
    private File homeDir;
    private File logDir;

    private File recordingInProgressDir;
    private File recordingCompleteDir;
    private CouchDbConnector wikiConnector;
    private CouchDbInstance dbInstance;
    private HttpClient couchHttpClient;
    private Diarizer diarizer;
    RecordingFinishedManager manager;
    private BookmarkDropTargetWindow bdtw;


    private Messages messages;

    private ScreenGrabber screenGrabber;
    private LiferecorderSyncDialog lrsd;
    private MeetingUploadDialog mud;


    public App() {
        System.setProperty(EventServiceLocator.SERVICE_NAME_EVENT_BUS, ThreadSafeEventService.class.getName());
        messages = loadMessages();
    }


    /**
     * This method is to be called by the CouchApp-Takeout app
     * @param db
     */
    public void start(CouchDbConnector db, CouchDbInstance instance) {
        this.wikiConnector = db;
        this.dbInstance = instance;
        this.couchHttpClient = instance.getConnection();

        Logger.getLogger(App.class.getName()).log(Level.INFO, "Init Working Dir");
        initWorkingDirectory();
        Logger.getLogger(App.class.getName()).log(Level.INFO, "Loading Property Storage");
        loadPropertyStorage();
        setupLogging();
        EventBus.subscribeStrongly(ShowDashboardMessage.class, new EventSubscriber<ShowDashboardMessage> () {
            @Override
            public void onEvent(ShowDashboardMessage t) {
                EventBus.publish(new ShowApplicationUrlMessage("/_design/app/dashboard.html"));
            }
        });

        EventBus.subscribeStrongly(LifeRecorderAttachedEvent.class, new EventSubscriber<LifeRecorderAttachedEvent> () {
            @Override
            public void onEvent(LifeRecorderAttachedEvent t) {
                // show in new thread?
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (lrsd == null) {
                            lrsd = new LiferecorderSyncDialog(null, false);
                        }
                        lrsd.setAlwaysOnTop(true);
                        lrsd.setSize(300, 150);
                        lrsd.setVisible(true);
                    }
                });
            }
        });
        EventBus.subscribeStrongly(MeetingFinalProcessingEvent.class, new EventSubscriber<MeetingFinalProcessingEvent> () {
            @Override
            public void onEvent(MeetingFinalProcessingEvent t) {
                if (t.getStatus() != MeetingFinalProcessingEvent.STARTED) return;
                // show in new thread?
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (mud == null) {
                            mud = new MeetingUploadDialog(null, false);
                        }
                        mud.setAlwaysOnTop(true);
                        mud.setSize(300, 150);
                        mud.setVisible(true);
                    }
                });
            }
        });


        Logger.getLogger(App.class.getName()).log(Level.INFO, "Start Liferecorder Manager");
        startLifeRecorderManager();
        //Logger.getLogger(App.class.getName()).log(Level.INFO, "Init Recording Components");
        //initRecordingComponents();
        //Logger.getLogger(App.class.getName()).log(Level.INFO, "Start Clustering");
        //clustering();
        Logger.getLogger(App.class.getName()).log(Level.INFO, "App loading finished");

        EventBus.publish(new AddMenuItemEvent(createBookmarkMenuItem()));
        EventBus.publish(new AddMenuItemEvent(createUploadMenuItem()));


    }



    protected Messages loadMessages() {
        ResourceBundle bundle = null;
        try {
            bundle = ResourceBundle.getBundle("messages");
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle("messages", Locale.ENGLISH);
        }
        return new Messages(bundle);
    }





    protected MenuItem createBookmarkMenuItem() {
        MenuItem item = new MenuItem(messages.getLocalMessage(Messages.BOOKMARK_TOOL_MENU_ITEM));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                        int x = 600;
                        int y;
                        if (SystemUtils.IS_OS_WINDOWS) {
                            y = 0;
                        } else {
                            y = 100;
                        }

                        String locationStr = p_storage.loadProperty("dropTargetLocation");
                        if (StringUtils.isNotEmpty(locationStr)) {
                            try {
                                String[] locArr = locationStr.split(",");
                                x = Integer.parseInt(locArr[0]);
                                y = Integer.parseInt(locArr[1]);
                            } catch (Exception e) {

                            }
                        }

                        if (bdtw == null) {
                            List<String> spaces = Arrays.asList(wikiConnector.getDatabaseName());
                            bdtw = new BookmarkDropTargetWindow(x, y, spaces, dbInstance);
                        }
                        if (bdtw.isVisible()) {
                            bdtw.setVisible(false);
                        } else {
                            bdtw.setVisible(true);
                        }
                        
                    }
                });
            }
        });
        return item;
    }













    protected MenuItem createUploadMenuItem() {
        MenuItem item = new MenuItem("Upload Audio");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                    UploadDirDialog dialog = new UploadDirDialog(null, true);
                        dialog.setWikiConnector(wikiConnector);
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);
                    }
                });
            }
        });
        return item;
    }












    private void startLifeRecorderManager() {
        List<String> roots = new ArrayList<String>();
        String recorderRoot = p_storage.loadProperty("lifeRecorderDirs");
        if (StringUtils.isNotEmpty(recorderRoot)) {
            roots = Arrays.asList(recorderRoot.split(","));
        }

        SansaClipLiferecorder scl = new SansaClipLiferecorder();
        // fix this. this prop may be set later
        FFMpegSplitter splitter = new FFMpegSplitter(p_storage.loadProperty("ffmpegcmd"));
        FFMpegRecordingTasks recordingTasks = new FFMpegRecordingTasks(splitter);
        scl.setRecordingTasks(recordingTasks);
        scl.setSecondsAfterMark(20000);
        scl.setSecondsBeforeMark(10000);
        scl.setSplitMillisecondTollerance(2000);

        Uploader uploader = new Uploader(wikiConnector);

        LifeRecorderManager lrm = new LifeRecorderManager(scl, recordingInProgressDir, uploader, wikiConnector, couchHttpClient, messages, roots);
        lrm.start();
    }

    protected void showUrl(URL dest) {


        Desktop desktop = null;
        // Before more Desktop API is used, first check
        // whether the API is supported by this particular
        // virtual machine (VM) on this particular host.
        if (Desktop.isDesktopSupported()) {
            Logger.getLogger(App.class.getName()).log(Level.INFO, "Getting Desktop");
            desktop = Desktop.getDesktop();
            try {
                Logger.getLogger(App.class.getName()).log(Level.INFO, "Browse Command");
                desktop.browse(dest.toURI());
                Logger.getLogger(App.class.getName()).log(Level.INFO, "showURl Complete");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                "Exception: " + ex.getMessage());
            }
        }
        
    }

    protected void findFFMpeg() {
        // if not set, find ffmpeg
        String ffmpegCmd = p_storage.loadProperty("ffmpegcmd");
        if (true) {
            Logger.getLogger(App.class.getName()).log(Level.INFO, "Starting FFmpeg locator");
            FFMpegLocator locator = new FFMpegLocator(p_storage);
            new Thread(locator).start();
        }
    }



    protected void initWorkingDirectory() {
       String userHome = System.getProperty("user.home");
       homeDir = new File(userHome, ".eckoit");
       if (!homeDir.exists()) {
           homeDir.mkdirs();
       }
       File recordingDir = new File(homeDir, "recordings");
       if (!recordingDir.exists()) {
           recordingDir.mkdirs();
       }

       recordingInProgressDir = new File(recordingDir, "in_progress");
       if (!recordingInProgressDir.exists()) {
           recordingInProgressDir.mkdirs();
       }

       recordingCompleteDir = new File(recordingDir, "complete");
       if (!recordingCompleteDir.exists()) {
           recordingCompleteDir.mkdirs();
       }

       logDir = new File(homeDir, "log");
       if (!logDir.exists()) {
           logDir.mkdirs();
       }

    }

    protected void loadPropertyStorage() {
        try {
            p_storage = new PropertiesPropertiesStorage(homeDir);
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void initRecordingComponents() {
        String splitTime = p_storage.loadProperty("splitTime");
        if (StringUtils.isNotBlank(splitTime)) {
            try {
                SplitAudioRecorder.setSplitTime(Long.parseLong(splitTime));
            } catch (Exception e) {}
        }

        ContinousAudioConvereter cac = new ContinousAudioConvereter(p_storage, recordingInProgressDir, recordingCompleteDir);
        cac.start();

        RecordingFinishedHelper helper = new RecordingFinishedHelper(recordingInProgressDir, recordingCompleteDir);
        manager = new RecordingFinishedManager(dbInstance, helper);

        diarizer = new Diarizer(manager);
    }


    private void setupLogging() {
         logger = Logger.getLogger("com.googlecode.eckoit");
         try {
              handler = new FileHandler("%h/.eckoit/log/log%g.out",true);
              logger.addHandler(handler);
              logger.setLevel(Level.ALL);
              SimpleFormatter formatter = new SimpleFormatter();
              handler.setFormatter(formatter);
              logger.info("Logger started");
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 5984;
        String db = "life";
        String username = null;
        String password = null;

        if (args.length >= 1) {
            String[] hostport = parseUsernamePass(args[0]);
            host = hostport[0];
            if (hostport.length == 2) {
                try {
                    port = Integer.parseInt(hostport[1]);
                } catch (Exception e) {}
            }
        }
        if (args.length >= 2) {
            db = args[1];
        }
        if (args.length == 3) {
            String[] up = parseUsernamePass(args[2]);
            username = up[0];
            if (up.length == 2) password = up[1];
        }

        StdHttpClient.Builder builder= new StdHttpClient.Builder()
                                    .host("localhost")
                                    .port(5984);
        if (StringUtils.isNotBlank(username)) {
            builder.username(username);
            builder.password(password);
        }


        HttpClient httpClient = builder.build();

        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        CouchDbConnector couch = new StdCouchDbConnector("mydatabase", dbInstance);
        new App().start(couch, dbInstance);
    }

    public static String[] parseUsernamePass(String arg) {
        if (arg == null) return null;
        return arg.split(":");
    }
}
