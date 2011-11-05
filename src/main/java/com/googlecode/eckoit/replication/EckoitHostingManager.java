/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.replication;

import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

/**
 *
 * @author ryan
 */
public class EckoitHostingManager implements HostingManager{

    private String endpointURL;
    private String email;
    private String password;
    private String returnUrl = "";

    @Override
    public JsonNode getUserSpacesOnHost() {
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            String encEmail = URLEncoder.encode(email);
            StringBuilder urlStr = new StringBuilder(endpointURL);
            if (!endpointURL.endsWith("/")) {
                urlStr.append("/");
            }
            urlStr.append("mine.html?email="+encEmail + "&pass=" + password);
            System.out.println(urlStr.toString());

            URL url = new URL(urlStr.toString());
            return mapper.readValue(url, JsonNode.class);
        } catch (Exception e) {
            ObjectNode node =  mapper.createObjectNode();
            node.put("status", "failed");
            return node;
        }
    }

    /**
     * @return the endpointURL
     */
    public String getEndpointURL() {
        return endpointURL;
    }

    /**
     * @param endpointURL the endpointURL to set
     */
    public void setEndpointURL(String endpointURL) {
        System.out.println("---- setting endpint: " + endpointURL);
        this.endpointURL = endpointURL;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @param password the password to set
     */
    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public JsonNode signUp(String firstName, String lastName, String username, String password, String space) {
        ObjectMapper mapper = new ObjectMapper();
        String encEmail = URLEncoder.encode(username);
        String encReturnUrl = URLEncoder.encode(returnUrl);
        try {
            URL url = new URL(endpointURL +  "/newAccountJson.html?firstName=" + firstName + "&lastName="
                    + lastName + "&email="+encEmail + "&password=" + password + "&space="+space + "&returnUrl=" + encReturnUrl);
            return mapper.readValue(url, JsonNode.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setEndpointUrl(String url) {
        this.endpointURL = url;
    }

    @Override
    public void setUsername(String username) {
        this.email = username;
    }

    /**
     * @param returnUrl the returnUrl to set
     */
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }


}
