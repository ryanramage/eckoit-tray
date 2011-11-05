/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.replication;

import org.codehaus.jackson.JsonNode;

/**
 *
 * @author ryan
 */
public interface HostingManager {

    public void setEndpointUrl(String url);
    public void setUsername(String username);
    public void setPassword(String password);

    // must be of format include
    // [{name: "a432fdsa-fdfsd..", alias: "life", protocol:"http", path: "eckoit.com/"},]

    public JsonNode getUserSpacesOnHost();
    public JsonNode signUp(String firstName, String lastName, String username, String password, String space);


}
