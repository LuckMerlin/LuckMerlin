/**
 * FileName: Ams
 * Author: LuckMerlin <a href="http://www.luckmerlin.com">LuckMerlin</a>
 * CreateDate: 2019\1\4 0004 17:31
 */
package com.merlin.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

/**
 * Copyright (C), 2018-2019, 偶家科技 <a href="http://www.oplushome.com">O+</a>
 * TODO 
 * Version: 0.0
 * History:
 */
public class Ams {

    public final ComponentName getTopActivityComponentName(Context context){
        ActivityManager am =null!=context?(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE):null;
        List<ActivityManager.RunningTaskInfo> runningTaskInfos=null!=am?am.getRunningTasks(1):null;
        ActivityManager.RunningTaskInfo runningTaskInfo=null!=runningTaskInfos&&runningTaskInfos.size()>0?runningTaskInfos.get(0):null;
        ComponentName componentName= null!=runningTaskInfo?runningTaskInfo.topActivity:null;
        return componentName;
    }
}