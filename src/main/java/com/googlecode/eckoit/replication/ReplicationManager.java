/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.replication;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ReplicationCommand;


import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

/**
 *
 * @author ryan
 */
public class ReplicationManager {

    private Timer timer;
    private CouchDbInstance couch;
    private List<String> spaces;
    private Map<String,JsonNode> nameToReplicationDoc;
    private File storageDir;
    private String hostingUsername;
    private String hostingPassword;

    private HostingManager hostingManager;



    public ReplicationManager(List<String> spaces, CouchDbInstance couch, File storageDir){
        this.couch = couch;
        this.spaces = spaces;
        this.storageDir = storageDir;
        nameToReplicationDoc = new HashMap<String, JsonNode>();
        loadReplications();
    }

    public void setHostingManager(HostingManager hostingManager) {
        this.hostingManager = hostingManager;
    }

    public HostingManager getHostingManager() {
        return hostingManager;
    }

    public void setHostingUserName(String hostingUsername) {
        this.hostingUsername = hostingUsername;
        if (hostingManager != null) {
            hostingManager.setUsername(hostingUsername);
        }
    }

    public void setHostingPassword(String hostingPassword) {
        this.hostingPassword = hostingPassword;
        if (hostingManager != null) {
            hostingManager.setPassword(hostingPassword);
        }
    }

    public boolean isAtLeastOneSpaceHosted() {
        if (nameToReplicationDoc.size() > 0) return true;
        return false;
    }

    public boolean areHostingCredentialsSet() {
        if (this.getHostingUsername() != null  && this.getHostingPassword() != null) return true;
        return false;
    }


    public JsonNode getAllReplicationDocs() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode nodes = mapper.convertValue(nameToReplicationDoc, ObjectNode.class);
        // remove the passwords
        for (JsonNode docs: nodes) {
            
            for (JsonNode doc : docs) {
                System.out.println(doc);
                if (doc.has("password")) {
                    ((ObjectNode)doc).remove("password");
                }
            }
        }
        

        return nodes;
    }

    public JsonNode getReplicationDoc(String space) {
        return nameToReplicationDoc.get(space);
    }

    public void startReplicationThread() {
        if (timer != null) timer.cancel();

        timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                try {
                    triggerReplications();
                } catch (Exception e) {
                    
                }
            }
        };
        timer.schedule(tt, 0, 60000); // 1 minute

    }

    public void stopReplicationThread() {
        if (timer != null) {
            timer.cancel();
        }
    }



    public void triggerReplications() {
        for (String space: nameToReplicationDoc.keySet()) {
            try {
                triggerReplication(space);
            } catch (Exception ex) {
                Logger.getLogger(ReplicationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void triggerReplication(final String space) throws IOException {
        Logger.getLogger(ReplicationManager.class.getName()).log(Level.INFO, "Finding replication info for: " + space);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode replicationDoc = mapper.readValue(new File(storageDir, space + ".json"), JsonNode.class);
        nameToReplicationDoc.put(space, replicationDoc);
        beginReplications(space, replicationDoc);
    }


    protected void beginReplications(String space, JsonNode replicationDoc){
        Iterator<JsonNode> i = replicationDoc.getElements();
        while(i.hasNext()) {
            JsonNode targetReplication = i.next();
            beginBiReplication(space, targetReplication);
        }
    }

    protected void beginBiReplication(String space, JsonNode targetReplication) {
        String target = buildTarget(targetReplication);
        try {
            ReplicationCommand command = new ReplicationCommand.Builder()
                    .source(space)
                    .target(target)
                    .continuous(true)
                    .filter("system/everythingButLR")
                    .build();

            couch.replicate(command);
        } catch (Exception e) {
            // push rep failed
        }
        try {
            ReplicationCommand command2 = new ReplicationCommand.Builder()
                    .source(target)
                    .target(space)
                    //.filter("system/everythingButLR")
                    .continuous(true)
                    .build();

            couch.replicate(command2);
        } catch (Exception e) {
            // pull rep failed
        }
    }

    public String buildTarget(JsonNode targetReplication) {
        String protocol = "http";
        JsonNode jprotocol = targetReplication.get("protocol");
        if (jprotocol != null) {
            protocol = jprotocol.getTextValue();
        }

        StringBuilder builder = new StringBuilder(protocol);
        builder.append("://");

        String username = pickClosestUserName(targetReplication, getHostingUsername());
        if (StringUtils.isNotEmpty(username)) {
            username = URLEncoder.encode(username);
        }


        String password = pickClosestPassword(targetReplication, getHostingPassword());
        

        if (StringUtils.isNotEmpty(username)) {
            builder.append(username);
        }
        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            builder.append(":").append(password);
        }
        if (StringUtils.isNotEmpty(username) || StringUtils.isNotEmpty(password)) {
            builder.append("@");

        }

        builder.append(targetReplication.get("path").getTextValue());
        builder.append(targetReplication.get("name").getTextValue());

        return builder.toString();

    }

    protected String pickClosestUserName(JsonNode targetReplication, String hostingUsername) {
        String username = null;
        JsonNode jusername = targetReplication.get("username");
        if (jusername != null) {
            username = jusername.getTextValue();
        }
        if (username != null) return username;
        if (hostingUsername != null) return hostingUsername;
        return null;
    }

    protected String pickClosestPassword(JsonNode targetReplication, String hostingPassword) {
        String password = null;
        JsonNode jpassword = targetReplication.get("password");
        if (jpassword != null) {
            password = jpassword.getTextValue();
        }
        if (password != null) return password;
        if (hostingPassword != null) return hostingPassword;
        return null;
    }
    private void loadReplications() {
        if (spaces == null) return;
        for (String space: spaces) {
            try {
                loadReplication(space);
            } catch (IOException ex) {
                Logger.getLogger(ReplicationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected void loadReplication(String space) throws IOException {
        Logger.getLogger(ReplicationManager.class.getName()).log(Level.INFO, "Finding replication info for: " + space);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode replicationDoc = mapper.readValue(new File(storageDir, space + ".json"), JsonNode.class);
        nameToReplicationDoc.put(space, replicationDoc);
    }


    public JsonNode getUserAvailableSpaces() {
        return hostingManager.getUserSpacesOnHost();
    }


    public void addShareConnection(String space, String hostedSpaceName) throws IOException {


        ObjectMapper mapper = new ObjectMapper();
        // check if there is info on thie space
        JsonNode replicationdoc = nameToReplicationDoc.get(space);
        if (replicationdoc == null) {
            
            replicationdoc = mapper.createArrayNode();
            nameToReplicationDoc.put(space, replicationdoc);
        }
        JsonNode hostedSpaces = hostingManager.getUserSpacesOnHost();
        ObjectNode repObj = getHostedSpace(hostedSpaces, hostedSpaceName);
        ((ArrayNode) replicationdoc).add(repObj);
        beginReplications(space, replicationdoc);
        mapper.writeValue(new File(storageDir, space + ".json"), replicationdoc);
    }

    protected ObjectNode getHostedSpace(JsonNode hostedSpaces, String hostedSpaceName) {
        ArrayNode arr = (ArrayNode)hostedSpaces;
        for (JsonNode i: arr) {
            ObjectNode o = (ObjectNode)i;
            String thisSpaceName = o.get("name").getTextValue();
            if (hostedSpaceName.equals(thisSpaceName)) {
                return o;
            }
        }
        return null;
    }

    /**
     * @return the hostingUsername
     */
    public String getHostingUsername() {
        return hostingUsername;
    }



    /**
     * @return the hostingPassword
     */
    public String getHostingPassword() {
        return hostingPassword;
    }


}
