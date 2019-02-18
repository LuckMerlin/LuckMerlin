package com.merlin.file;

import com.merlin.debug.Debug;

import java.io.File;
import java.io.IOException;

/**
 * @deprecated  replace by FileMaker.java
 */
public class MakeFile {
    /**
     * @deprecated  replace by FileMaker.java
     */
    public final  File makeFile(String filePath){
        return new FileMaker().makeFile(filePath);
    }

    /**
     * @deprecated  replace by FileMaker.java
     */
    public final File makeFile(File file){
        return new FileMaker().makeFile(file);
    }
    /**
     * @deprecated  replace by FileMaker.java
     */
    public final File makeFolder(File folder){
        return new FileMaker().makeFolder(folder);
    }
    /**
     * @deprecated  replace by FileMaker.java
     */
    public final File makeFolder(String folderPath){
        return new FileMaker().makeFolder(folderPath);
    }

}
