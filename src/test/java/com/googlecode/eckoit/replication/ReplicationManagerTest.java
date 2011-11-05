/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.replication;

import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ryan
 */
public class ReplicationManagerTest {

    ObjectMapper mapper = new ObjectMapper();

    public ReplicationManagerTest() {
    }










    @Test
    public void testBuildTargetNoPass() throws IOException {
        System.out.println("buildTarget");
        JsonNode targetReplication = mapper.readValue("{\"path\":\"ecko-it.couchone.com/\", \"name\" : \"rr\"  ,     \"username\":\"test@eckoit.com\"}", JsonNode.class);
        ReplicationManager instance = new ReplicationManager(null, null, null);
        String expResult = "http://test%40eckoit.com@ecko-it.couchone.com/rr";
        String result = instance.buildTarget(targetReplication);
        assertEquals(expResult, result);

    }
    @Test
    public void testBuildTargetNoUserCred() throws IOException {
        System.out.println("buildTarget");
        JsonNode targetReplication = mapper.readValue("{\"path\":\"ecko-it.couchone.com/\", \"name\" : \"rr\" }", JsonNode.class);
        ReplicationManager instance = new ReplicationManager(null, null, null);
        String expResult = "http://ecko-it.couchone.com/rr";
        String result = instance.buildTarget(targetReplication);
        assertEquals(expResult, result);

    }

    /**
     * Test of pickClosestUserName method, of class ReplicationManager.
     */
    @Test
    public void testPickClosestUserNameHostingUsername() throws IOException {
        System.out.println("pickClosestUserName");
        String username = null;
        JsonNode targetReplication = mapper.readValue("{\"protocol\": \"https\",\"target\":\"ecko-it.couchone.com/rr\"}", JsonNode.class);
        String hostingUsername = "test%40eckoit.com";
        ReplicationManager instance = new ReplicationManager(null, null, null);
        String expResult = "test%40eckoit.com";
        String result = instance.pickClosestUserName( targetReplication, hostingUsername);
        assertEquals(expResult, result);

    }



    @Test
    public void testPickClosestUserNameFromTarget() throws IOException {
        System.out.println("pickClosestUserName");
        JsonNode targetReplication = mapper.readValue("{\"protocol\": \"https\",\"target\":\"ecko-it.couchone.com/rr\", \"username\" :\"bob\"}", JsonNode.class);
        String hostingUsername = "test@eckoit.com";
        ReplicationManager instance = new ReplicationManager(null, null, null);
        String expResult = "bob";
        String result = instance.pickClosestUserName( targetReplication, hostingUsername);
        assertEquals(expResult, result);

    }

    

}