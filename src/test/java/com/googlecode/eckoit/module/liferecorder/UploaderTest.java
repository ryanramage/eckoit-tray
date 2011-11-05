/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import org.junit.Ignore;
import com.googlecode.eckoit.module.liferecorder.Uploader;
import com.googlecode.eckoit.module.liferecorder.SansaClipLiferecorder;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.http.StdHttpClient;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import java.io.File;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.joda.time.Interval;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ryan
 */
public class UploaderTest {

    public UploaderTest() {
    }



    /**
     * Test of uploadFilesToCouch method, of class Uploader.
     */
    @Test
    @Ignore
    public void testUploadFilesToCouch() {
        System.out.println("uploadFilesToCouch");

        String host = "192.168.1.101";
        int port = 80;

        HttpClient couchHttpClient = new StdHttpClient.Builder()
                                    .host(host)
                                    .port(port)
                                    .build();

        CouchDbInstance dbInstance = new StdCouchDbInstance(couchHttpClient);
        CouchDbConnector wikiConnector = new StdCouchDbConnector("life", dbInstance);

        SansaClipLiferecorder scl = new SansaClipLiferecorder();

        File homeDir = new File("C:\\rtemp\\audio");

        Uploader uploader = new Uploader(wikiConnector);

        uploader.uploadFilesToCouch(homeDir, scl);


    }



}