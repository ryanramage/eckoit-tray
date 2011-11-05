/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.bookmarkHelper;

import com.googlecode.eckoit.events.PropertyToSaveMessage;
import com.googlecode.eckoit.events.TargetClickedEvent;
import com.googlecode.eckoit.tray.Messages;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import org.bushe.swing.event.EventBus;
import org.codehaus.jackson.JsonNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ViewQuery;

/**
 *
 * @author ryan
 */
public class BookmarkDropTargetWindow extends JWindow {
    private Dimension windowSizeAsIcon;
    private Dimension windowSizeExpanded;
    private int currentX;
    private int currentY;
    private JPanel display;
    private DropFormWebpage webForm;
    private DropFormFiles filesForm;
    private CouchDbInstance dbInstance;
    JDialog webd ;
    JDialog filesd;
    
    public BookmarkDropTargetWindow(int x, int y, List<String> spaces,  CouchDbInstance dbInstance) {
        this.dbInstance = dbInstance;
        windowSizeAsIcon = new Dimension(16, 16);
        windowSizeExpanded = new Dimension(420, 480);
        this.currentX = x;
        this.currentY = y;
        display = new JPanel();
        display.setLayout(new BorderLayout() );
        display.setBackground(Color.gray);
        display.setToolTipText("Drag items from a webpage into here to bookmark");
        getContentPane().add(display);

        JLabel label = new JLabel(new ImageIcon("mind_small.png"));
        label.setSize(16, 16);
        display.add(label, BorderLayout.CENTER);

        setSize(windowSizeAsIcon);
        setLocation(x, y);
        setAlwaysOnTop(true);
        webForm = new DropFormWebpage();
        webForm.setBookmarkDropTargetWindow(this);
        webForm.setDBInstance(dbInstance);
        webForm.setSpaces(spaces);

        filesForm = new DropFormFiles();
        filesForm.setBookmarkDropTargetWindow(this);
        filesForm.setDBInstance(dbInstance);
        filesForm.setSpaces(spaces);



        display.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                currentX = e.getX();
                currentY = e.getY();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                EventBus.publish(new TargetClickedEvent());
            }
        });
        display.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(getLocation().x + (e.getX() - currentX), getLocation().y + (e.getY() - currentY));
                Point p = getLocation();
                String location = (int)p.getX() + "," + (int)p.getY();
                EventBus.publish(new PropertyToSaveMessage("dropTargetLocation", location));
            }
        });

        DropTarget dt = new DropTarget(this, new DropTargetAdapter() {


            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                display.setBackground(Color.green);
            }

            public void dragExit(DropTargetEvent dte) {
                display.setBackground(Color.gray);
            }


            @Override
            public void drop(DropTargetDropEvent dtde) {
   
                display.setBackground(Color.gray);
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                Transferable t = dtde.getTransferable();

                Object dropObject = getDropObject(t);
                if (dropObject == null) return;
                if (dropObject instanceof List) {
                    System.out.println("Its a list of files");
                    List<File> files = (List<File>)dropObject;
                    showFilesDialog(files);
                    // show the file dialog
                    
                } else {
                    // we should be checking what this, just text, or a url
                    final String url = (String) dropObject;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            showBookmarkDialog(url);
                        }
                    });
                }



            }
        });
    }


    protected Object getDropObject(Transferable t) {
        DataFlavor[] flavors = t.getTransferDataFlavors();
        try {
            for (DataFlavor flavor:flavors) {
                System.out.println(flavor);
                if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                    return t.getTransferData(flavor);
                } else {
                    return t.getTransferData(DataFlavor.stringFlavor);
                }
            }
        } catch(Exception e) {
            
        }
        return null;
    }

    protected void showBookmarkDialog(String url) {
        if (webd == null) {
            webd = new JDialog();
            webd.setTitle("Bookmark");
            webd.setSize(windowSizeExpanded);
            webd.setLocationRelativeTo(null);
            webd.getContentPane().add(webForm);
            webForm.setContainer(webd);
        }

        webForm.setWebsiteAddress(url);
        webd.setVisible(true);
        webForm.getDescriptionField().requestFocus();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              webForm.getDescriptionField().requestFocusInWindow();
            }
          });

    }

    protected void showFilesDialog(List<File> files) {
        if (filesd == null) {
            filesd = new JDialog();
            filesd.setTitle("Add Files");
            filesd.setSize(windowSizeExpanded);
            filesd.setLocationRelativeTo(null);
            filesd.getContentPane().add(filesForm);
            filesForm.setContainer(filesd);
        }

        filesForm.setFiles(files);
        filesd.setVisible(true);
        filesForm.getOkButton().requestFocus();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              filesForm.getOkButton().requestFocusInWindow();
            }
          });
    }



    protected void returnToIcon() {

    }


}
