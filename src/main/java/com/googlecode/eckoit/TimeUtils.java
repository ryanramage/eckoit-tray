/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit;

/**
 *
 * @author ryan
 */
public class TimeUtils {


    public static String getHHs(long l) {
        if (l<0) l =0;  //make sure to to get negative values
        long lHours = l/3600;
        StringBuilder sbHours = new StringBuilder(Long.toString(lHours));
        if ( sbHours.length() == 1 ) sbHours.insert(0,'0');
        return sbHours.toString();
    }

    public static long getHHs_long(long l) {
        if (l<0) l =0;  //make sure to to get negative values
        return   l/3600;
    }
     public static String getMMs(long l) {
        if (l<0) l =0;  //make sure to to get negative values
        long lHours = l/3600;
        long lMins = l/60-(lHours*60);
        StringBuilder sbMins = new StringBuilder(Long.toString(lMins));
        if ( sbMins.length() == 1 ) sbMins.insert(0,'0');
        return sbMins.toString();
    }

    public static long getMMs_long(long l) {
        if (l<0) l =0;  //make sure to to get negative values
        long lHours = l/3600;
        return l/60-(lHours*60);

    }
    public static String getSSs(long l) {
        if (l<0) l =0;  //make sure to to get negative values
        long lHours = l/3600;
        long lMins = l/60-(lHours*60);
        long lSecs = l-(lHours*3600)-(lMins*60);

        StringBuilder sbSecs = new StringBuilder(Long.toString(lSecs));
        if ( sbSecs.length() == 1) sbSecs.insert(0,'0');
        return sbSecs.toString();
    }

     public static long getSSs_long(long l) {
        if (l<0) l =0;  //make sure to to get negative values
        long lHours = l/3600;
        long lMins = l/60-(lHours*60);
        return l-(lHours*3600)-(lMins*60);

    }

    public static long getTotalSeconds(long hh, long mm, long ss) {
        long total = ss;
        total += mm * 60;
        total += hh * 60 * 60;
        return total;
    }


    /**Format a time from secs to a human readable format*/
    public static  String formatTimeBySec(long l, boolean bTrimZeros){

        if (l == -1){ //means we are in repeat mode
            return "--:--"; //$NON-NLS-1$
        }
        if (l<0) l =0;  //make sure to to get negative values
        long lHours = l/3600;
        long lMins = l/60-(lHours*60);
        long lSecs = l-(lHours*3600)-(lMins*60);

        StringBuilder sbHours = new StringBuilder(Long.toString(lHours));
        if ( sbHours.length() == 1 && !bTrimZeros) sbHours.insert(0,'0');
        StringBuilder sbMins = new StringBuilder(Long.toString(lMins));

        if ( sbMins.length() == 1 && !bTrimZeros) sbMins.insert(0,'0');
        StringBuilder sbSecs = new StringBuilder(Long.toString(lSecs));
        if ( sbSecs.length() == 1) sbSecs.insert(0,'0');

        StringBuilder sbResult = new StringBuilder("");

         sbResult.append(sbHours.toString());

         sbResult.append(":");

        return sbResult.append(sbMins.toString()).append(":").append(sbSecs.toString()).toString(); //$NON-NLS-1$
    }
}
