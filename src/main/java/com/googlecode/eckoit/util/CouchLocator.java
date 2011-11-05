/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.util;

import com.googlecode.eckoit.PropertiesStorage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

/**
 *
 * @author ryan
 */
public class CouchLocator  {

    PropertiesStorage p_storage;



    public CouchLocator(PropertiesStorage p_storage) {
        this.p_storage = p_storage;
    }

    public String findCouchDB() {

        String specifiedExe = p_storage.loadProperty("couchdb");
        if (StringUtils.isNotEmpty(specifiedExe)) return specifiedExe;

        return locateEmbeddedCouchExe();
    }


    public String findCouchWorkingDir() {
        String workingDir = p_storage.loadProperty("couchdb_workingdir");
        if (StringUtils.isNotEmpty(workingDir)) return workingDir;
        return null;
    }



    protected String locateEmbeddedCouchExe() {
        File couchDbBinFile = null;
        if (SystemUtils.IS_OS_WINDOWS){
            // check the lib dir
            File lib = new File("lib");
            if (!lib.exists() || !lib.isDirectory()) return null;

            File[] dirs = lib.listFiles();
            File couchDBDir = findCouchDBDir(dirs);
            if (couchDBDir == null) return null;
            File couchdbBinDir = new File(couchDBDir, "bin");
            if (!couchdbBinDir.exists() || !couchdbBinDir.isDirectory()) return null;

            couchDbBinFile = findCouchDbBinFile(couchdbBinDir);

        } else if (SystemUtils.IS_OS_MAC) {
            return "couchdb/bin/couchdb";
        }
        if (couchDbBinFile == null || !couchDbBinFile.exists() || !couchDbBinFile.isFile()) return null;
        String fullExe = couchDbBinFile.getAbsolutePath();

        return fullExe;
    }


    public File findCouchDBDir(File[] dirs) {
        for (File file : dirs) {
            if (file.getName().startsWith("couchdb")) return file;
        }
        return null;
    }

    protected File findCouchDbBinFile(File couchdbBinDir) {
        File location = null;
        if (SystemUtils.IS_OS_WINDOWS) {
            location = new File(couchdbBinDir, "couchdb.bat");
        }
        else  {
            location  = new File(couchdbBinDir, "couchdb");

        }
        return location;
    }



}
