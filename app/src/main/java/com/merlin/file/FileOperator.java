package com.merlin.file;


import com.merlin.debug.Debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOperator {

    /**
     * @deprecated  use FileDeleter replace
     */
    public boolean delete(File file){
        if (null!=file&&file.exists()){
            if (file.isFile()){
                return file.delete();
            }else{
               File[] files= file.listFiles();
               int length=null!=files?files.length:0;
               File temp;
                for (int i = 0; i <length ; i++) {
                    if (null!=(temp=files[i])){
                        delete(temp);
                    }
                }
            }
        }
        return false;
    }

    public final boolean writeText(File file,String text){
        if (null!=file&&null!=text){
            createFile(file);
            if (!file.exists()){
                Debug.W(getClass(),"Can't write file for create file failed.file="+file+" text="+text);
               return false;
            }
            byte[] bytes=text.getBytes();
            if (null==bytes){
                Debug.W(getClass(),"Can't write file for create file failed.bytes="+bytes+" file="+file+" text="+text);
                return false;
            }
            FileOutputStream fos=null;
            try {
                fos=new FileOutputStream(file);
                fos.write(bytes);
                fos.flush();
                return true;
            } catch (Exception e) {
                Debug.E(getClass(),"Exception.Write file.e="+e+" file="+file+" text="+text,e);
                e.printStackTrace();
            }finally {
                new Closer().close(fos);
            }
            return false;
        }
        return false;
    }

    public  String readText(File file,String defaultText){
        if (null!=file&&file.exists()){
            FileInputStream fis=null;
            try {
                fis=new FileInputStream(file);
                byte[] bytes=new byte[1024*1024];
                final StringBuilder builder = new StringBuilder();
                int readed;
                while (0<(readed=fis.read(bytes))){
                    builder.append(new String(bytes, 0, readed));
                }
                return builder.toString();
            } catch (Exception e) {
                Debug.E(getClass(),"Exception.Read file.e="+e+" file="+file+" defaultText="+defaultText,e);
                e.printStackTrace();
            }finally {
                new Closer().close(fis);
            }
        }
        return defaultText;
    }

    public final boolean createFile(File file){
        if (null!=file){
             if (!file.exists()){
                 try {
                     file.createNewFile();
                     return file.exists();
                 } catch (IOException e) {
                     Debug.E(getClass(),"Failed create file.e="+e+" file="+file,e);
                     e.printStackTrace();
                 }
             }
            return false;
        }
        Debug.W(getClass(),"Can't create file. file="+file);
        return false;
    }

    private boolean createParent(File file){
        File parent=null!=file?file.getParentFile():null;
        if (null!=parent){
            if (!parent.exists()){
                parent.mkdirs();
                return parent.exists();
            }
        }
        Debug.W(getClass(),"Can't create file parent.parent="+parent+" file="+file);
        return false;
    }

}
