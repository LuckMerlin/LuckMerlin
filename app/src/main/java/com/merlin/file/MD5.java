package com.merlin.file;

import com.merlin.debug.Debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class MD5 {
    private final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public boolean matchMd5(File file,File file2){
        String fileMd5=null!=file&&null!=file2?getFileMD5(file):null;
        if (null!=fileMd5){
            return matchMd5(file2,fileMd5);
        }
        Debug.W(getClass(),"Can't match file MD5.file="+file+" file2="+file2);
        return  false;
    }

    public boolean matchMd5(File file,String md5){
         String fileMd5=null!=file&&null!=md5&&!md5.isEmpty()?getFileMD5(file):null;
         if (null!=fileMd5){
             return fileMd5.equalsIgnoreCase(md5);
         }
        Debug.W(getClass(),"Can't match file MD5.file="+file+" fileMd5="+fileMd5+" md5="+md5);
        return  false;
    }

    public String getFileMD5(File file) {
        final boolean exist=null!=file&&file.exists();
        if (exist){
            InputStream fis=null;
            final byte[] buffer = new byte[1024];
            MessageDigest md5;
            try {
                fis = new FileInputStream(file);
                md5 = MessageDigest.getInstance("MD5");
                int numRead;
                while ((numRead = fis.read(buffer)) > 0) {
                    md5.update(buffer, 0, numRead);
                }
                return toHexString(md5.digest());
            } catch (Exception e) {
                Debug.E(getClass(),"Can't get file MD5 value.e="+e+" file="+file,e);
            }finally {
                new Closer().close(fis);
            }
            return null;//Interrupt later codes
        }
        Debug.W(getClass(),"Can't get file MD5 value. exist="+exist+" file="+file);
        return  null;
    }

    private  String toHexString(byte[] bytes) {
        if (null!=bytes){
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (int i = 0; i < bytes.length; i++) {
                sb.append(HEX_DIGITS[(bytes[i] & 0xf0) >>> 4]);
                sb.append(HEX_DIGITS[bytes[i] & 0x0f]);
            }
            return sb.toString();
        }
        return  null;
    }

}
