/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.audio;

import com.googlecode.eckoit.audio.FFMpegConverter;
import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ryan
 */
public class FFMpegConverterTest {

    public FFMpegConverterTest() {
    }

    @Test
    public void testConstructor() {
        String ffmpegcmd = "C:\\Program Files\\Participatory Culture Foundation\\Miro Video Converter\\ffmpeg-bin\\ffmpeg.exe";
        FFMpegConverter instance = new FFMpegConverter(ffmpegcmd, FFMpegConverter.ENCODER_MP3);
    }

    /**
     * Test of convertURL method, of class FFMpegConverter.
     */

    public void testConvertURL() throws Exception {
        System.out.println("convertURL");
        String url = "http://192.168.1.101/wikid/814efa9c-c477-4622-bac1-a9b254648f7d/R_MIC_101114-170847.mp3";
        long bitrate = 24000L;
        long frequency = 16000L;
        File outputfile = new File("D:\\rtemp\\R_MIC_101108-170233.new.mp3");
        boolean forceOverwrite = true;
        String ffmpegcmd = "C:\\Program Files\\Participatory Culture Foundation\\Miro Video Converter\\ffmpeg-bin\\ffmpeg.exe";
        FFMpegConverter instance = new FFMpegConverter(ffmpegcmd, FFMpegConverter.ENCODER_MP3);
        instance.convertURL(url, bitrate, frequency, outputfile, forceOverwrite);
        // TODO review the generated test code and remove the default call to fail.

    }


}