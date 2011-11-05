/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.tray;

import java.util.ResourceBundle;

/**
 *
 * @author ryan
 */
public class Messages {

    public static final String APP_NAME = "APP_NAME";
    public static final String TRAY_NOT_SUPPORTED = "TRAY_NOT_SUPPORTED";
    public static final String EXIT_MENU_ITEM = "EXIT_MENU_ITEM";
    public static final String NEW_MEETING_ITEM = "NEW_MEETING_ITEM";
    public static final String BOOKMARK_TOOL_MENU_ITEM = "BOOKMARK_TOOL_MENU_ITEM";
    public static final String GRAPH_TOOL_MENU_ITEM = "GRAPH_TOOL_MENU_ITEM";

    public static final String LIFE_RECORDER_ATTACHED_TITLE = "LIFE_RECORDER_ATTACHED_TITLE";
    public static final String LIFE_RECORDER_DETATCHED_TITLE = "LIFE_RECORDER_DETATCHED_TITLE";
    public static String LIFE_RECORDER_SYNC_COMPLETE = "LIFE_RECORDER_SYNC_COMPLETE";


    private ResourceBundle bundle;

    public Messages(ResourceBundle bundle) {
        this.bundle = bundle;
    }


    public String getLocalMessage(String key) {
        return bundle.getString(key);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

}
