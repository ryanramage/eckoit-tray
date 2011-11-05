/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.audio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;

/**
 *
 * @author ryan
 */
public class FFMpegSplitter {

    String ffmpegFullCommand;

    public FFMpegSplitter( String ffmpegcmd) {
        this.ffmpegFullCommand =  ffmpegcmd;

    }
    public void split(File wav, long start, long duration, File outputfile) throws InterruptedException, IOException {

        ProcessBuilder pb = new ProcessBuilder(new String[] {ffmpegFullCommand, "-i", wav.getAbsolutePath(), "-acodec", "copy", "-ss", start + "", "-t", duration + "", outputfile.getAbsolutePath()} );

        pb.redirectErrorStream(true);
        Process p = pb.start();
        InputStream stream = p.getInputStream();
        IOUtils.copy(stream, new NullOutputStream());

    }
    public void split(File wav, long start, File outputfile) throws InterruptedException, IOException {

        ProcessBuilder pb = new ProcessBuilder(new String[] {ffmpegFullCommand, "-i", wav.getAbsolutePath(), "-acodec", "copy", "-ss", start + "", outputfile.getAbsolutePath()} );

        pb.redirectErrorStream(true);
        Process p = pb.start();
        InputStream stream = p.getInputStream();
        IOUtils.copy(stream, new NullOutputStream());

    }

}
