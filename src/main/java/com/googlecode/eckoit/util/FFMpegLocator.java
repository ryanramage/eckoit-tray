/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.util;

import com.googlecode.eckoit.PropertiesStorage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

/**
 *
 * @author ryan
 */
public class FFMpegLocator implements Runnable {

    PropertiesStorage p_storage;
    List<String> windowsLocations = Arrays.asList(
        "lib\\ffmpeg.exe",
        "C:\\Program Files\\Participatory Culture Foundation\\Miro Video Converter\\ffmpeg-bin\\ffmpeg.exe",
        "D:\\Program Files\\Participatory Culture Foundation\\Miro Video Converter\\ffmpeg-bin\\ffmpeg.exe",
        "C:\\Program Files\\FFmpeg for Audacity\\ffmpeg.exe",
        "D:\\Program Files\\FFmpeg for Audacity\\ffmpeg.exe"
    );
    List<String> macLocations = Arrays.asList(
        "/Applications/eckoit/lib/ffmpeg",
        "lib/ffmpeg",
        "/Applications/Miro Video Converter.app/Contents/Resources/ffmpeg"
    );
    List<String> linuxLocations = Arrays.asList(
        "/usr/bin/ffmpeg"
    );


    public FFMpegLocator(PropertiesStorage p_storage) {
        this.p_storage = p_storage;
    }

    public void run() {


        String ffmpeg = findFFmepg();
        if (ffmpeg != null) {
            System.out.println("ffmpeg found: " + ffmpeg);
            p_storage.storeProperty("ffmpegcmd", ffmpeg);
        } else {
            System.out.println("ffmpeg not found: ");
            throw new RuntimeException("FFMpeg not found");
        }

    }


    public String findFFmepg() {
        String location = null;

        String ffmpegCmd = p_storage.loadProperty("ffmpegcmd");
        if (StringUtils.isNotEmpty(ffmpegCmd)) {
            File f = new File(ffmpegCmd);
            if (f != null && f.exists() && f.isFile() && f.canExecute()) {
                return ffmpegCmd;
            }
        }

        
        if (SystemUtils.IS_OS_WINDOWS) {
            location = checkLocations(windowsLocations);
        }
        else if (SystemUtils.IS_OS_MAC_OSX) {
            location = checkLocations(macLocations);

        } else if (SystemUtils.IS_OS_LINUX) {

        }
        return location;
        // else in future do some crazy hd scan....
    }


    public boolean ffmpegCheck(String location) {
        File f = new File(location);
        if (f.exists() && f.isFile()) return true;
        else return false;
    }

    private String checkLocations(List<String> windowsLocations) {
        for(String location : windowsLocations) {
            if (ffmpegCheck(location)) return location;
        }
        return null;
    }
}
