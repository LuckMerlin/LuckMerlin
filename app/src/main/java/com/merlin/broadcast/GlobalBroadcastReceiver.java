package com.merlin.broadcast;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.merlin.debug.Debug;

import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public class GlobalBroadcastReceiver extends HandlerBroadcastReceiver{
    private static GlobalBroadcastReceiver mInstance;
    private static WeakHashMap<OnBroadcastReceived,Long> mCallbacks;

    public GlobalBroadcastReceiver(){
         this(null);
    }

    public GlobalBroadcastReceiver(OnBroadcastReceived callback){
        if (null!=callback){
            add(callback);
        }
    }

    @Override
    public void onBroadcastReceived(Context context, Intent intent, String action) {
        Set<OnBroadcastReceived> set=null!=mCallbacks?mCallbacks.keySet():null;
        Iterator<OnBroadcastReceived> iterator=null!=set?set.iterator():null;
          if (null!=iterator&&iterator.hasNext()){
              OnBroadcastReceived callback;
                do {
                    if (null!=(callback=iterator.next())){
                        callback.onBroadcastReceived(context,intent,action);
                    }
                }while (iterator.hasNext());
          }
    }

    public int size(){
        return null!=mCallbacks?mCallbacks.size():0;
    }

    public boolean add(OnBroadcastReceived callback){
        if (null!=callback&&(null==mCallbacks||!mCallbacks.containsKey(callback))){
             mCallbacks=null!=mCallbacks?mCallbacks:new WeakHashMap<OnBroadcastReceived, Long>();
             mCallbacks.put(callback,System.currentTimeMillis());
            return true;
        }
        return false;
    }

    public boolean remove(OnBroadcastReceived callback){
        if (null!=callback&&null!=mCallbacks&&mCallbacks.containsKey(callback)){
            mCallbacks.remove(callback);
            if (mCallbacks.size()<=0){
                mCallbacks=null;
            }
            return true;
        }
        return false;
    }

    public boolean registe(Context context, IntentFilter filter){
        if (null==mInstance){
             if (null!=context&&null!=filter){
                 context.registerReceiver(mInstance=this,filter);
                 Debug.D(getClass(),"Registed global broadcast receiver.");
                 return true;
             }
             Debug.W(getClass(),"Can't registe global broadcast receiver.context="+context+" filter="+filter);
        }
       return false;
    }

    public boolean unregiste(Context context){
        if (null!=mInstance){
            if (null!=context){
                context.unregisterReceiver(mInstance);
                Debug.D(getClass(),"Unregisted global broadcast receiver.");
                return true;
            }
            Debug.W(getClass(),"Can't unregiste global broadcast receiver.context="+context);
        }
        return false;
    }
}
