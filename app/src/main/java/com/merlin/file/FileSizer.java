/**
 * FileName: FileSizer
 * Author: LuckMerlin <a href="http://www.luckmerlin.com">LuckMerlin</a>
 * CreateDate: 2018\12\27 0027 10:20
 */
package com.merlin.file;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Copyright (C), 2018-2018, 偶家科技 <a href="http://www.oplushome.com">O+</a>
 * TODO 
 * Version: 0.0
 * History:
 */
public class FileSizer {

    public final int size(URL url) {
        final int DEFAULT_SIZE=-1;
        if (null!=url){
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("HEAD");
                conn.getInputStream();
                return conn.getResponseCode() == HttpURLConnection.HTTP_OK?conn.getContentLength():DEFAULT_SIZE;
            } catch (IOException e) {
                return DEFAULT_SIZE;
            } finally {
                if (null!=conn){
                    conn.disconnect();
                }
            }
        }
        return DEFAULT_SIZE;
    }
}