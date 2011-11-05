/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit;


/**
 *
 * @author ryan
 */
public interface PropertiesStorage {

    public void storeProperty(String property, String value);
    public String loadProperty(String property);

}
