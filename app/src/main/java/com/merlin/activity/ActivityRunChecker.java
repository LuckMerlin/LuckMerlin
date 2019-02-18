package com.merlin.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityRunChecker {
    private final Handler mHandler=new Handler();
    private final Map<Class<? extends Activity>,OnActivityRunChange> mMaps=new HashMap<>();
    private final Context mContext;

    public interface OnActivityRunChange{
        void onActivityRunChange(boolean running,Class<?extends Activity> cls);
    }

    public ActivityRunChecker(Context context){
        mContext=context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public final boolean reset(){
        if (null!=mHandler){
            ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.AppTask> list= mActivityManager.getAppTasks();
        }
        return false;
    }

}
