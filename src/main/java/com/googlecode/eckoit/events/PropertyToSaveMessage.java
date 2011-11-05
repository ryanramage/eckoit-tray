/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.events;



/**
 *
 * @author ryan
 */
public class PropertyToSaveMessage {
    private String name;
    private String value;

    public PropertyToSaveMessage(String name, String value) {
        this.name = name;
        this.value = value;

    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }



}
