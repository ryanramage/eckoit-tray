/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import com.googlecode.eckoit.module.liferecorder.LifeRecordingDiarizationManager;
import java.io.File;
import com.googlecode.eckoit.audio.FFMpegConverter;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.StdHttpClient;
import org.ektorp.http.HttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ryan
 */
public class LifeRecordingDiarizationManagerTest {

    public LifeRecordingDiarizationManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getNonDiarazedRecordings method, of class LifeRecordingDiarizationManager.
     */

    @Test
    public void testConstructor() {
        String host = "192.168.1.101";
        int port = 80;

        HttpClient couchHttpClient = new StdHttpClient.Builder()
                                    .host(host)
                                    .port(port)
                                    .build();

        CouchDbInstance dbInstance = new StdCouchDbInstance(couchHttpClient);
        CouchDbConnector wikiConnector = new StdCouchDbConnector("wikid", dbInstance);

        String ffmpegcmd = "C:\\Program Files\\Participatory Culture Foundation\\Miro Video Converter\\ffmpeg-bin\\ffmpeg.exe";
        FFMpegConverter converter = new FFMpegConverter(ffmpegcmd, FFMpegConverter.ENCODER_MP3);


        LifeRecordingDiarizationManager instance = new LifeRecordingDiarizationManager(host, port, wikiConnector, converter, new File("target"));
    }


    public void doDiarizations() {
        System.out.println("getNonDiarazedRecordings");

        String host = "192.168.1.101";
        int port = 80;

        HttpClient couchHttpClient = new StdHttpClient.Builder()
                                    .host(host)
                                    .port(port)
                                    .build();

        CouchDbInstance dbInstance = new StdCouchDbInstance(couchHttpClient);
        CouchDbConnector wikiConnector = new StdCouchDbConnector("wikid", dbInstance);

        String ffmpegcmd = "C:\\Program Files\\Participatory Culture Foundation\\Miro Video Converter\\ffmpeg-bin\\ffmpeg.exe";
        FFMpegConverter converter = new FFMpegConverter(ffmpegcmd, FFMpegConverter.ENCODER_MP3);


        LifeRecordingDiarizationManager instance = new LifeRecordingDiarizationManager(host, port, wikiConnector, converter, new File("target"));
        instance.doDiarizations();

    }

}