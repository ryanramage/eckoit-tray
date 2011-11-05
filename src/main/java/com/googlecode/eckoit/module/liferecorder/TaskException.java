/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

/**
 *
 * @author ryan
 */
public class TaskException extends Exception {

    /**
     * Creates a new instance of <code>TaskException</code> without detail message.
     */
    public TaskException() {
    }


    /**
     * Constructs an instance of <code>TaskException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TaskException(String msg) {
        super(msg);
    }
}
