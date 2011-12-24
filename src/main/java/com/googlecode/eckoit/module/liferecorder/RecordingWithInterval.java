/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;
import org.ektorp.util.Base64;
import org.joda.time.Interval;

/**
 *
 * @author ryan
 */
public class RecordingWithInterval {

    private String id;
    private File file;
    private Interval interval;
    private String md5;

    public RecordingWithInterval() {
        id = UUID.randomUUID().toString();
    }


    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return the interval
     */
    public Interval getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }


    public String getMD5() throws IOException {
        if (md5 == null) {
            byte[] bts = DigestUtils.md5(new FileInputStream(file));
            md5 = Base64.encodeBytes(bts);
        }
        return md5;
    }



}
