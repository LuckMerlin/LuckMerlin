/**
 * FileName: SystemProperties
 * Author: LuckMerlin <a href="http://www.luckmerlin.com">LuckMerlin</a>
 * CreateDate: 2019\1\4 0004 14:19
 */
package com.merlin.device;

import com.merlin.debug.Debug;

import java.lang.reflect.Method;

/**
 * Copyright (C), 2018-2019, 偶家科技 <a href="http://www.oplushome.com">O+</a>
 * TODO 
 * Version: 0.0
 * History:
 */
public class SystemProperties {

    public  String getString(final String key, String def) {
        Class cls=systemProperties();
        Method method= null;
        try {
            method = null!=cls?cls.getMethod("get", String.class,String.class):null;
            return null!=method?(String) method.invoke(null, key, def):def;
        } catch (Exception e) {
            Debug.E(getClass(),"Can't get system properties long.e="+e+" key="+key,e);
            e.printStackTrace();
        }
        return def;
    }

    public  long getLong(final String key, long def) {
        Class cls=systemProperties();
        Method method= null;
        try {
            method = null!=cls?cls.getMethod("getLong", String.class,long.class):null;
            return null!=method?(Long) method.invoke(null, key, def):def;
        } catch (Exception e) {
            Debug.E(getClass(),"Can't get system properties long.e="+e+" key="+key,e);
            e.printStackTrace();
        }
        return def;
    }

    public  boolean getBoolean(final String key, boolean def) {
        Class cls=systemProperties();
        Method method= null;
        try {
            method = null!=cls?cls.getMethod("getBoolean", String.class,boolean.class):null;
            return null!=method?(Boolean)method.invoke(null, key, def):def;
        } catch (Exception e) {
            Debug.E(getClass(),"Can't get system properties boolean.e="+e+" key="+key,e);
            e.printStackTrace();
        }
        return def;
    }

    private Class<?> systemProperties(){
        try {
            return Class.forName("android.os.SystemProperties");
        } catch (ClassNotFoundException e) {
            Debug.E(getClass(),"Can't get system properties.e="+e,e);
            e.printStackTrace();
        }
        return null;
    }
}