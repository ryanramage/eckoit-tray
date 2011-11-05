/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit;

import java.util.Map;

/**
 *
 * @author ryan
 */
public interface AudioPlayerListener {
    void started();
    void stopped();
    void playing(long currentSecond, long totalSeconds);
    void nodeSelected(long startOffset, long originalStart, long end);

}
