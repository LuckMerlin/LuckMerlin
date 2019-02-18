/**
 * FileName: Imei
 * Author: LuckMerlin <a href="http://www.luckmerlin.com">LuckMerlin</a>
 * CreateDate: 2019\1\4 0004 15:26
 */
package com.merlin.device;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * Copyright (C), 2018-2019, 偶家科技 <a href="http://www.oplushome.com">O+</a>
 * TODO 
 * AppVersion: 0.0
 * History:
 */
public class Imei {

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.O)
    public  final String getIMEI(Context context, String def) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (null!=manager){
                try {
                    TelephonyManager.class.getDeclaredMethod("getDeviceId");
                    String imei=manager.getDeviceId();
                    if (null!=imei&&!imei.isEmpty()){
                        return imei;
                    }
                }catch (Exception e){

                }
                return null!=manager?manager.getImei():def;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }
}