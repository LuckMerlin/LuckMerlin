package com.merlin.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.merlin.debug.Debug;

public class AppVersion {

    public  String getVersion(Context context){
        PackageManager pm=null!=context?context.getPackageManager():null;
        if (null!=pm){
            try{
                PackageInfo pi=null!=pm?pm.getPackageInfo(context.getPackageName(),0):null;
                return null!=pi?""+pi.versionName+"."+pi.versionCode:null;
            }catch (Exception e){
                Debug.E(getClass(),"Can't get version.e="+e+" context="+context);
                e.printStackTrace();
            }
        }
        Debug.E(getClass(),"Can't get version. pm="+pm+" context="+context);
        return null;
    }
}
