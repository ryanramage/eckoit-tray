/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit;

import com.googlecode.eckoit.events.PropertyToSaveMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

/**
 *
 * @author ryan
 */
public class PropertiesPropertiesStorage implements PropertiesStorage {

    File homeDir;
    File propsFile;
    Properties prop;
    Timer timer;
    TimerTask task;

    public PropertiesPropertiesStorage(File homeDir) throws IOException {
        this.homeDir = homeDir;
        this.prop = new Properties();
        propsFile = new File (homeDir, "application.properties");
        if (!propsFile.exists()) {
            propsFile.createNewFile();
        }
        FileInputStream is = new FileInputStream(propsFile);
        prop.load(is);
        is.close();
        timer = new Timer();
        

        EventBus.subscribeStrongly(PropertyToSaveMessage.class, new EventSubscriber<PropertyToSaveMessage>() {
            @Override
            public void onEvent(PropertyToSaveMessage t) {
                prop.setProperty(t.getName(), t.getValue());
                if (task != null) {
                    task.cancel();
                }
                task = new SaveTask();
                timer.schedule(task, 3000);
            }
        });



    }

    @Override
    public void storeProperty(String property, String value) {
        if (value == null) {
            prop.remove(property);
        } else {
            prop.setProperty(property, value);
        }
        try {
            save();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PropertiesPropertiesStorage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PropertiesPropertiesStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String loadProperty(String property) {
        return prop.getProperty(property);
    }

    public void save() throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(propsFile);
        prop.store(fos, "Saved " + new Date());
        fos.close();
    }


    private class SaveTask extends TimerTask {
        @Override
        public void run() {
            try {
                save();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PropertiesPropertiesStorage.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PropertiesPropertiesStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
