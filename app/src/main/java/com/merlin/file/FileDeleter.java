/**
 * FileName: FileDeleter
 * Author: LuckMerlin <a href="http://www.luckmerlin.com">LuckMerlin</a>
 * CreateDate: 2019\1\14 0014 15:08
 */
package com.merlin.file;

import com.merlin.debug.Debug;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (C), 2018-2019, 偶家科技 <a href="http://www.oplushome.com">O+</a>
 * TODO 
 * Version: 0.0
 * History:
 */
public class FileDeleter {


    public boolean delete(File ...files){
        final int count=null!=files?files.length:0;
        File file;
        for (int i = 0; i < count; i++) {
              if (null!=(file=files[i])&&file.exists()){
                      List<File> folders=new ArrayList<>();
                      doDelete(file,folders);
                      final int length=null!=folders?folders.size():0;
                      if (length>0){
                          Collections.reverse(folders);
                          File folder;
                          for (int j = 0;j < length ; j++) {
                              if (null!=(folder=folders.get(j))){
                                  folder.delete();
                              }
                          }
                          return !file.exists();
                      }
              }
        }
         return false;
    }

    private boolean doDelete(File file, List<File> folder){
        if (null!=folder){
            if (null!=file&&file.exists()){
                if (file.isFile()){
                    return file.delete();
                }else{
                    folder.add(file);
                    File[] files= file.listFiles();
                    int length=null!=files?files.length:0;
                    File temp;
                    for (int i = 0; i <length ; i++) {
                        if (null!=(temp=files[i])){
                            doDelete(temp,folder);
                        }
                    }
                }
            }
            return false;
        }
        Debug.E(getClass(),"Can't do file delete.folder="+folder);
        return false;
    }

}