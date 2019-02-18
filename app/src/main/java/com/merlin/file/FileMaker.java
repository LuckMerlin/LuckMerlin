package com.merlin.file;

import com.merlin.debug.Debug;

import java.io.File;
import java.io.IOException;

public class FileMaker {

    public final  File makeFile(String filePath){
          return makeFile(filePath,null);
    }

    public final  File makeFile(String filePath,String debug){
        return null!=filePath?makeFile(new File(filePath),debug):null;
    }

    public final  File makeFile(File file){
        return makeFile(file,null);
    }

    public final File makeFile(File file,String debug){
        if (null!=file){
            if (!file.exists()){
                File parent=null!=file?file.getParentFile():null;
                if (null!=parent&&(parent.exists()||parent.mkdirs())){//Try make folder parent
                    try {
                        file.createNewFile();
                        Debug.W(getClass(),"Create file "+(null!=debug?debug:".")+" file="+file);
                    } catch (IOException e) {
                        Debug.E(getClass(),"Can't make file "+(null!=debug?debug:".")+" e="+e+" "+file,e);
                    }
                }
            }
            return file.exists()&&file.isFile()?file:null;
        }
        Debug.W(getClass(),"Can't make file "+(null!=debug?debug:".")+" file="+file);
        return null;
    }

    public final File makeFolder(File folder){
        if (null!=folder){
            if (!folder.exists()){
                folder.mkdirs();
            }
            return folder.exists()&&folder.isDirectory()?folder:null;
        }
        return null;
    }

    public final File makeFolder(String folderPath){
        File file=null!=folderPath?new File(folderPath):null;
        return null!=file?makeFolder(file):null;
    }

}
