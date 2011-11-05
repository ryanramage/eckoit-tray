/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import com.googlecode.eckoit.audio.FFMpegConverter;
import fr.lium.spkDiarization.system.Eckoization;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;

/**
 *
 * @author ryan
 */
public class LifeRecordingDiarizationManager {
    CouchDbConnector wikiConnector;
    String host;
    int port;
    FFMpegConverter converter;
    File workingDir;


    public LifeRecordingDiarizationManager(String host, int port, CouchDbConnector wikiConnector, FFMpegConverter converter, File workingDir) {
        this.wikiConnector = wikiConnector;
        this.host = host;
        this.port = port;
        this.converter = converter;
    }

    public void doDiarizations() {
        List<JsonNode> recordings = getNonDiarazedRecordings();
        try {
            File resultFile = convertRecordingToProperFormat(recordings.get(0));
            Eckoization.main(new String[] {resultFile.getAbsolutePath()});
        } catch (Exception ex) {
            Logger.getLogger(LifeRecordingDiarizationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    protected String getAudioURL(String id, String file) {
        String uri = "http://" + host + ":" + port + "/" + wikiConnector.getDatabaseName() + "/" + id + "/" + file;
         return uri;
    }


    protected File convertRecordingToProperFormat(JsonNode node) throws InterruptedException, IOException {
        String file = node.get("_attachments").getFieldNames().next();
        String id = node.get("_id").getTextValue();
        String audioURL =  getAudioURL(id, file);
        File resultFile = new File(workingDir, file);
        converter.convertURL(audioURL, 24000L, 16000L, resultFile, true);
        return resultFile;
    }



    public List<JsonNode> getNonDiarazedRecordings() {
        ViewQuery query = new ViewQuery().designDocId("_design/app")
                .viewName("audio_by_undiarized");
        return wikiConnector.queryView(query, JsonNode.class);

    }

}
