/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.tray;

import com.github.couchapptakeout.ShowApplicationUrlMessage;
import com.github.couchapptakeout.events.AddMenuItemEvent;
import com.github.couchapptakeout.plugins.Plugin;
import com.googlecode.eckoit.bookmarkHelper.BookmarkDropTargetWindow;
import com.googlecode.eckoit.events.LifeRecorderAttachedEvent;
import com.googlecode.eckoit.events.ShowDashboardMessage;
import com.googlecode.eckoit.module.liferecorder.LifeRecorderManager;
import com.googlecode.eckoit.module.liferecorder.SansaClipLiferecorder;
import com.googlecode.eckoit.module.liferecorder.UploadDirDialog;
import com.googlecode.eckoit.module.liferecorder.Uploader;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;

/**
 *
 * @author ryan
 */
public class EckoitPlugin implements Plugin{

    File workingDir;
    File eckoitFolder;
    private LiferecorderSyncDialog lrsd;
    private BookmarkDropTargetWindow bdtw;

    @Override
    public void start(CouchDbConnector db, CouchDbInstance instance, File workingFolder) {
        this.workingDir = workingFolder;
        eckoitFolder = new File(workingDir, "eckoit");
        Messages messages = loadMessages();
        startLifeRecorderManager(db, workingFolder, messages);

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
        EventBus.publish(new AddMenuItemEvent(createUploadMenuItem(db)));
        EventBus.publish(new AddMenuItemEvent(createBookmarkMenuItem(instance, db, messages)));
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

    private void startLifeRecorderManager(CouchDbConnector db, File recordingInProgressDir, Messages messages) {
        List<String> roots = new ArrayList<String>();
        String recorderRoot = "";//p_storage.loadProperty("lifeRecorderDirs");
        if (StringUtils.isNotEmpty(recorderRoot)) {
            roots = Arrays.asList(recorderRoot.split(","));
        }

        SansaClipLiferecorder scl = new SansaClipLiferecorder();

        Uploader uploader = new Uploader(db);

        LifeRecorderManager lrm = new LifeRecorderManager(scl, recordingInProgressDir, uploader, db,  messages, roots);
        lrm.start();

    }



    protected MenuItem createUploadMenuItem(final CouchDbConnector db) {
        MenuItem item = new MenuItem("Upload Audio");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                    UploadDirDialog dialog = new UploadDirDialog(null, true);
                        dialog.setWikiConnector(db);
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);
                    }
                });
            }
        });
        return item;
    }

    protected MenuItem createBookmarkMenuItem(final CouchDbInstance instance,final CouchDbConnector db,Messages messages) {
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

//                        String locationStr = p_storage.loadProperty("dropTargetLocation");
//                        if (StringUtils.isNotEmpty(locationStr)) {
//                            try {
//                                String[] locArr = locationStr.split(",");
//                                x = Integer.parseInt(locArr[0]);
//                                y = Integer.parseInt(locArr[1]);
//                            } catch (Exception e) {
//
//                            }
//                        }

                        if (bdtw == null) {
                            List<String> spaces = Arrays.asList(db.getDatabaseName());
                            bdtw = new BookmarkDropTargetWindow(x, y, spaces, instance);
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





}
