package com.merlin.thread;

import android.os.Handler;
import android.os.Looper;

import com.merlin.debug.Debug;

public class UIThreadPost {

    public final boolean runInUiThread(Handler handler, Runnable runnable){
        return runInUiThread(handler,runnable,0);
    }


    public final boolean runInUiThread(Handler handler, Runnable runnable,long delayMillis){
        handler=null!=runnable&&null!=handler&&handler.getLooper()==Looper.getMainLooper()?handler:new Handler(Looper.getMainLooper());
        if (null!=handler){
            return handler.postDelayed(runnable,delayMillis);
        }
        Debug.W(getClass(),"Can't run in UI thread.runnable="+runnable+" handler="+handler);
        return false;
    }

}
