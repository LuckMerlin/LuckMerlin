/**
 * FileName: DeviceSleep
 * Author: LuckMerlin <a href="http://www.luckmerlin.com">LuckMerlin</a>
 * CreateDate: 2018\12\29 0029 14:34
 */
package com.merlin.device;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;

import com.merlin.debug.Debug;

import java.lang.reflect.Method;

/**
 * Copyright (C), 2018-2018, 偶家科技 <a href="http://www.oplushome.com">O+</a>
 * TODO 
 * Version: 0.0
 * History:
 */
public class DeviceSleep implements Sleeper{
    private final PowerManager mManager;

    public DeviceSleep(Context context){
        this(null!=context? (PowerManager)context.getSystemService(Context.POWER_SERVICE):null);
    }

    public DeviceSleep(PowerManager manager){
        mManager=manager;
    }

    @Override
    public boolean sleep() {
        return goToSleepByReflect();
    }

    private boolean goToSleepByReflect(){
        if (null!=mManager){
            try {
                Method method=mManager.getClass().getDeclaredMethod("goToSleep",long.class);
                if (null!=method){
                    method.setAccessible(true);
                    method.invoke(mManager, SystemClock.uptimeMillis());
                    return true;
                }
            } catch (Exception e) {
                Debug.E(getClass(),"Can't go to sleep.e="+e);
                e.printStackTrace();
            }
            return false;
        }
        Debug.W(getClass(),"Can't go to sleep.mManager="+mManager);
        return false;
    }
}