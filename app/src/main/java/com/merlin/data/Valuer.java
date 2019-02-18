package com.merlin.data;

import com.merlin.debug.Debug;

public class Valuer {

    public final int parseInt(String text,int defaultValue){
        if (null!=text){
            try{
               return Integer.parseInt(text);
            }catch (Exception e){
                Debug.E(getClass(),"Can't parse int value.e="+e+" text="+text,e);
                e.printStackTrace();
            }
        }
       return defaultValue;
    }

    public final long parseLong(String text,long defaultValue){
        if (null!=text){
            try{
                return Long.parseLong(text);
            }catch (Exception e){
                Debug.E(getClass(),"Can't parse long value.e="+e+" text="+text,e);
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

}
