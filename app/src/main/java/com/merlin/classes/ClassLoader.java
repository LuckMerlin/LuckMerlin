/**
 * FileName: ClassLoader
 * Author: LuckMerlin <a href="http://www.luckmerlin.com">LuckMerlin</a>
 * CreateDate: 2019\1\14 0014 10:45
 */
package com.merlin.classes;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.merlin.classes.DefaultInstance;
import com.merlin.debug.Debug;

import dalvik.system.DexClassLoader;

/**
 * Copyright (C), 2018-2019, 偶家科技 <a href="http://www.oplushome.com">O+</a>
 * TODO 
 * Version: 0.0
 * History:
 */
public class ClassLoader {

    public final String getAppDexPath(Context context,String pkgName){
        final PackageManager manager=null!=context?context.getPackageManager():null;
        if (null!=manager){
            String pkg=null!=pkgName&&!pkgName.isEmpty()?pkgName:context.getPackageName();
            if (null!=pkg){
                ApplicationInfo info= null;
                try {
                    info = manager.getApplicationInfo(pkg,0);
                } catch (PackageManager.NameNotFoundException e) {
                    Debug.E(getClass(),"Can't get application dex path.e="+e+" pkg="+pkg);
                    e.printStackTrace();
                }
                return null!=info?info.sourceDir:null;
            }
            Debug.W(getClass(),"Can't get application dex path.pkg="+pkg+" pkgName="+pkgName);
            return null;
        }
        Debug.E(getClass(),"Can't  get application dex path.context="+context+" manager="+manager);
        return null;
    }

    public final String getAppSoLibPath(Context context,String pkgName){
        final PackageManager manager=null!=context?context.getPackageManager():null;
        if (null!=manager){
            String pkg=null!=pkgName&&!pkgName.isEmpty()?pkgName:context.getPackageName();
            if (null!=pkg){
                ApplicationInfo info= null;
                try {
                    info = manager.getApplicationInfo(pkg,0);
                } catch (PackageManager.NameNotFoundException e) {
                    Debug.E(getClass(),"Can't get application so lib path.e="+e+" pkg="+pkg);
                    e.printStackTrace();
                }
                return null!=info?info.nativeLibraryDir:null;
            }
            Debug.W(getClass(),"Can't get application so lib path.pkg="+pkg+" pkgName="+pkgName);
            return null;
        }
        Debug.E(getClass(),"Can't  get application so lib path.context="+context+" manager="+manager);
        return null;
    }

    public DexClassLoader getClassLoader(Context context,String pkgName){
        final String dexPath=null!=context?getAppDexPath(context,pkgName):null;
        final ApplicationInfo appInfo= null!=context?context.getApplicationInfo():null;
        final String soPath=null!=context?getAppSoLibPath(context,pkgName):null;
        final String dataDir=null!=appInfo?appInfo.dataDir:null;
        return null!=dexPath&&!dexPath.isEmpty()&&null!=dataDir&&!dataDir.isEmpty()?
                new DexClassLoader(dexPath,dataDir,soPath,getClass().getClassLoader()):null;
    }

    public final   <T> T  createDefaultInstance(Context context,String pkgName,String clsName,Class<T> cls){
        if (null!=context&&null!=clsName&&null!=cls){
              Class<?> targetCls= loadClass(context,pkgName,clsName);
              return (T)(null!=targetCls?new DefaultInstance().createDefault(targetCls):null);
        }
        Debug.W(getClass(),"Can't  create instance.context="+context+" clsName="+clsName);
         return null;
    }

    public  Class<?>  loadClass(Context context,String pkgName,String clsName){
         return loadClass(context,pkgName,clsName,true);
    }

    public  Class<?>  loadClass(Context context,String pkgName,String clsName,boolean logException){
        DexClassLoader loader= null!=context&&null!=clsName&&!clsName.isEmpty()?getClassLoader(context,pkgName):null;
        try {
            return null!=loader?loader.loadClass(clsName):null;
        } catch (ClassNotFoundException e) {
            if (logException) {
                Debug.E(getClass(), "Can't load class.e=" + e + " clsName=" + clsName + " pkgName=" + pkgName, e);
                e.printStackTrace();
            }
        }
        return null;
    }

}