/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.upload;

import java.io.File;
import java.util.Map;

/**
 *
 * @author ryan
 */
public interface MediaUploader {

    public File convertMedia(File mp3) throws Exception;
    public Map upload(File mp3, Map properies) throws Exception;
}
