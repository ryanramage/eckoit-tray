package com.googlecode.eckoit.tray;

import com.github.couchapptakeout.events.AddMenuItemEvent;
import com.github.couchapptakeout.events.ExitApplicationMessage;
import com.github.couchapptakeout.events.TrayMessage;
import com.googlecode.eckoit.PropertiesPropertiesStorage;
import com.googlecode.eckoit.PropertiesStorage;
import com.googlecode.eckoit.bookmarkHelper.BookmarkDropTargetWindow;
import com.googlecode.eckoit.module.liferecorder.LifeRecorderManager;
import com.googlecode.eckoit.module.liferecorder.SansaClipLiferecorder;
import com.googlecode.eckoit.module.liferecorder.Uploader;
import java.awt.AWTException;

import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.ImageIcon;
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
    private BookmarkDropTargetWindow bdtw;


    private TrayIcon trayIcon;

    Image trayImg;



    private Messages messages;

    private LiferecorderSyncDialog lrsd;


    public App() {
        System.setProperty(EventServiceLocator.SERVICE_NAME_EVENT_BUS, ThreadSafeEventService.class.getName());
    }


    /**
     * This method is to be called by the CouchApp-Takeout app
     * @param db
     */
    public void start(CouchDbConnector db, CouchDbInstance instance) {
        

        initWorkingDirectory();
        setupLogging();



        trayImg = createImage("/icon.png");

        SystemTray tray = SystemTray.getSystemTray();
        if (!SystemTray.isSupported()) {
            throw new RuntimeException("Tray Not Supported");
        }
        trayIcon = new TrayIcon(trayImg, "Eckoit");
        final PopupMenu popup = createMenu();

        trayIcon.setPopupMenu(popup);
        try {
            tray.add(trayIcon);
            //registerEvents();
        } catch (AWTException e) {

        }
        registerEvents();
        



        EckoitPlugin plugin = new EckoitPlugin();
        plugin.start(db, instance, homeDir);




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
        int port = 5983;
        String db = "life";
        String username = "admin";
        String password = "admin";

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
                                    .host(host)
                                    .port(port);
        if (StringUtils.isNotBlank(username)) {
            builder.username(username);
            builder.password(password);
        }


        HttpClient httpClient = builder.build();

        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        CouchDbConnector couch = new StdCouchDbConnector(db, dbInstance);
        couch.createDatabaseIfNotExists();
        
        new App().start(couch, dbInstance);
    }

    public static String[] parseUsernamePass(String arg) {
        if (arg == null) return null;
        return arg.split(":");
    }


    //Obtain the image URL
    protected static Image createImage(String path) {
        URL imageURL = App.class.getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL)).getImage();
        }

    }

    private PopupMenu createMenu() {
        final PopupMenu popup = new PopupMenu("Couch Audio Recorder");
        {
            MenuItem item = new MenuItem("Exit");
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EventBus.publish(new ExitApplicationMessage());
                    try {
                        Thread.sleep(1000);
                        System.exit(0);
                    } catch (Exception ex) {
                    }

                }
            });
            popup.add(item);

        }
        return popup;
    }


    protected void registerEvents() {

       EventBus.subscribeStrongly(TrayMessage.class, new EventSubscriber<TrayMessage>() {
            @Override
            public void onEvent(TrayMessage t) {
                trayIcon.displayMessage(t.getTitle(), t.getMessage(), t.getType());
            }
        });

        EventBus.subscribeStrongly(ExitApplicationMessage.class, new EventSubscriber<ExitApplicationMessage>() {
            @Override
            public void onEvent(ExitApplicationMessage t) {
                SystemTray tray = SystemTray.getSystemTray();
                tray.remove(trayIcon);
            }
        });
        EventBus.subscribeStrongly(AddMenuItemEvent.class, new EventSubscriber<AddMenuItemEvent>() {
            @Override
            public void onEvent(AddMenuItemEvent t) {
                trayIcon.getPopupMenu().add(t.getMenuItem());
            }
        });

   }


}
