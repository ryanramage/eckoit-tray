/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.SystemUtils;


/**
 *
 * @author ryan
 */
public class ChromeManager {

    List<String> windowsLocations = Arrays.asList(
        System.getProperty("user.home") + "",
        "C:\\Program Files\\Participatory Culture Foundation\\Miro Video Converter\\ffmpeg-bin\\ffmpeg.exe",
        "D:\\Program Files\\Participatory Culture Foundation\\Miro Video Converter\\ffmpeg-bin\\ffmpeg.exe",
        "C:\\Program Files\\FFmpeg for Audacity\\ffmpeg.exe",
        "D:\\Program Files\\FFmpeg for Audacity\\ffmpeg.exe"
    );
    List<String> macLocations = Arrays.asList(
        "lib/ffmpeg",
        "/Applications/Miro Video Converter.app/Contents/Resources/ffmpeg"
    );
    List<String> linuxLocations = Arrays.asList(
        "/usr/bin/ffmpeg"
    );


      private String chromeExe;

      public ChromeManager() {
          this.chromeExe = locateChrome();
          if (chromeExe== null) throw new RuntimeException("Can't locate Google Chrome");
      }


      private String locateChrome() {

          if (SystemUtils.IS_OS_WINDOWS) {
               Logger.getLogger(ChromeManager.class.getName()).log(Level.INFO, "Looking for chrome on windows");
               String path = System.getProperty("user.home") + "\\Local Settings\\Application Data" + "\\Google\\Chrome\\Application\\chrome.exe";
               Logger.getLogger(ChromeManager.class.getName()).log(Level.INFO, "Found at {0} ", path);
               if (checkPath(path)) return path;

               path = System.getProperty("user.home") + "\\AppData\\Local" + "\\Google\\Chrome\\Application\\chrome.exe";
               if (checkPath(path)) return path;

               path = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
               if (checkPath(path)) return path;
               
          }
          if (SystemUtils.IS_OS_MAC_OSX) {
              Logger.getLogger(ChromeManager.class.getName()).log(Level.INFO, "Looking for chrome on osx");
              String path = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
              //if (checkPath(path)) return path;
          }
          return null;
    }

      private boolean checkPath(String path) {
          File test = new File(path);
           if (test != null && test.exists() && test.isFile() && test.canExecute() ) {
               return true;
           }
          return false;
      }


    public void showUrl(String url) {
        List cmd = Arrays.asList(chromeExe, url);
        ProcessBuilder pb = new ProcessBuilder(cmd);
        try {
            Process p = pb.start();            
        } catch (IOException ex) {
            Logger.getLogger(ChromeManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
