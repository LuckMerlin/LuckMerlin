package com.merlin.string;

import android.content.Context;
import android.content.res.Resources;

import com.merlin.debug.Debug;

public class StringEquals {

    public final boolean equal(String value1,String value2){
           return equal(value1,value2,false);
    }

    public final boolean equal(String value1,String value2,boolean notNull){
        if(notNull&&(null==value1||null==value2)){
            return false;
        }else if(null!=value1&&null!=value2){
            return value1.equals(value2);
        }else if (null==value1&&null==value2){
            return true;
        }
        return false;
    }

    public boolean equals(Context context,String text,int ...resIds){
        int length=null!=resIds?resIds.length:0;
        if (length>0){
            String value=null;
            for (int i = 0; i <length ; i++) {
                if (null!=(value=getString(context,resIds[i]))&&value.equals(text)){
                     return true;
                }
            }
        }
       return false;
    }

    public boolean equalsIgnoreCase(Context context,String text,int ...resIds){
        int length=null!=resIds?resIds.length:0;
        if (length>0){
            String value=null;
            for (int i = 0; i <length ; i++) {
                if (null!=(value=getString(context,resIds[i]))&&value.equalsIgnoreCase(text)){
                    return true;
                }
            }
        }
        return false;
    }

    public String getString(Context context,int resId){
        try {
            return null!=context?context.getString(resId):null;
        }catch (Exception e){
            Debug.E(getClass(),"Can't get string.e="+e+" resId="+resId,e);
            e.printStackTrace();
        }
        return null;
    }

    public boolean equalsAnyResString(String src,Resources resources, int ...resIds){
        final int length=null!=src&&null!=resIds?resIds.length:0;
        if (length>0){
            if (null!=resources) {
                for (int f : resIds) {
                    try {
                        String text=resources.getString(f);
                        if (null!=text&&text.equals(src)){
                            return true;
                        }
                    }catch (Exception e){
                        Debug.E(getClass(),"Can't equal strings any.e="+e+" id="+f+" src="+src,e);
                        e.printStackTrace();
                    }
                }
                return false;//Not equals anyone
            }
            Debug.W(getClass(),"Can't equal strings any.resources="+resources+" src="+src);
            return false;
        }
        return false;
    }
}
