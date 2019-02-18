package com.merlin.classes;

import android.content.Context;

import com.merlin.debug.Debug;

public class ContextCreator {

    public final Context create(Context context,String pkgName){
           return null!=pkgName?create(context,pkgName,Context.CONTEXT_INCLUDE_CODE| Context.CONTEXT_IGNORE_SECURITY):null;
    }

    public final Context create(Context con,String pkgName,int flag){
        if (null!=con&&null!=pkgName){
            try {
                return con.createPackageContext(pkgName,flag);
            } catch (Exception e) {
                Debug.E(getClass(),"Can't create package context.e="+e+" pkgName="+pkgName);
                e.printStackTrace();
            }
            return null;
        }
        Debug.W(getClass(),"Can't create package context.context="+con+" pkgName="+pkgName);
        return null;
    }

}
