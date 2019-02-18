package com.merlin.file;

import com.merlin.debug.Debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zip {

    public interface Callback{
        int UNZIP_FINISH_SUCCEED=0;
        int UNZIP_FINISH_INVALID=1;
        int UNZIP_FINISH_ERROR=2;
        int UNZIP_FINISH_ERROR_OTHER=3;
    }

    public interface OnUnzipFinish{
        void onUnzipFinish(boolean succeed, int what, File zipFile, File targetFolder);
    }

    public final boolean unzip(File zip,File targetFolder,OnUnzipFinish finish){
        final boolean exist=null!=zip?zip.exists():false;
        final boolean canRead=null!=zip?zip.canRead():false;
        if (null==zip||!exist||!canRead){
            Debug.D(getClass(),"Can't unzip file.exist="+exist+" canRead="+canRead+" zip="+zip);
            notifyFinish(false,Callback.UNZIP_FINISH_INVALID,zip,targetFolder,finish);
            return false;
        }
        final MakeFile makeFile=new MakeFile();
        final byte[] buffer = new byte[1024*1024];
        final Closer closer=new Closer();
        ZipInputStream zis = null ;
        try {
            zis = new ZipInputStream(new FileInputStream(zip));
            ZipEntry zipEntry = zis.getNextEntry();
            boolean succeed=true;
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                fileName = fileName.replace("/", File.separator).replace("\\", File.separator);
                File newFile = new File(targetFolder,fileName);
                if (!zipEntry.isDirectory()) {
                    if (null!=makeFile.makeFile(newFile)){
                        FileOutputStream fos=null;
                        try {
                            fos = new FileOutputStream(newFile);
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }catch (Exception e){
                            succeed=false;
                            Debug.E(getClass(),"Exception while unzip file.e="+e+" file="+newFile,e);
                            e.printStackTrace();
                        }finally {
                            closer.close(fos);
                        }
                    }else{
                        succeed=false;
                        Debug.W(getClass(),"Can't unzip one file,make file failed.newFile="+newFile);
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            Debug.D(getClass(),"Finish unzip file.succeed="+succeed+" zip="+zip+" folder="+targetFolder);
            notifyFinish(succeed,succeed?Callback.UNZIP_FINISH_SUCCEED:Callback.UNZIP_FINISH_ERROR_OTHER,zip,targetFolder,finish);
           return true;//Interrupt later codes
        } catch (Exception e) {
            Debug.E(Zip.class, "Can't unzip file.e="+e +" zip="+zip+" folder="+targetFolder, e);
            notifyFinish(false,Callback.UNZIP_FINISH_ERROR,zip,targetFolder,finish);
        } finally {
            closer.close(zis);
        }
        return false;
    }

    private void notifyFinish(boolean succeed,int what,File zipFile,File targetFolder,OnUnzipFinish finish){
        if (null!=finish){
            finish.onUnzipFinish(succeed,what,zipFile,targetFolder);
        }
    }

}
