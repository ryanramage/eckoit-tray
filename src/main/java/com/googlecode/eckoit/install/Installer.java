/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.install;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author ryan
 */
public class Installer {
    private static final int IO_BUFFER_SIZE = 4 * 1024;

    public void downloadFFMpegWindows() {
        String url = "https://github.com/8planes/mirovideoconverter/raw/master/MSWindows/Windows/ffmpeg-bin/ffmpeg.exe";
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        try {
            HttpResponse resp = httpclient.execute(httpget);
            InputStream in = resp.getEntity().getContent();
            FileOutputStream out = new FileOutputStream("ffmpeg.exe");
            copy(in,out);
            in.close();
            out.close();
        } catch (Exception ex) {
            Logger.getLogger(Installer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }


}
