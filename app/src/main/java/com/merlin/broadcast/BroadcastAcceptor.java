package com.merlin.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.merlin.debug.Debug;

import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public class BroadcastAcceptor {
   private static BroadcastReceiver mBroadcastReceiver;
   private final static WeakHashMap<HandlerBroadcastReceiver.OnBroadcastReceived,Long> mCallbacks=new WeakHashMap<>();

   protected IntentFilter onResolveIntentFilter(){
       //Do nothing
       return null;
   }

   protected void onBroadcastReceived(Context context, Intent intent, String action){
       //DO nothing
    }

   public final boolean add(HandlerBroadcastReceiver.OnBroadcastReceived callback){
      return null!=callback&&null!=mCallbacks&&!mCallbacks.containsKey(callback)?(null==mCallbacks.put(callback,System.currentTimeMillis())):false;
   }

    public final boolean remove(HandlerBroadcastReceiver.OnBroadcastReceived callback){
        return null!=callback&&null!=mCallbacks?(null!=mCallbacks.remove(callback)):false;
    }

    public final boolean registere(Context context){
         return  registere(context,onResolveIntentFilter());
    }

   public final boolean registere(Context context, IntentFilter filter){
       if (null==mBroadcastReceiver){
            if (null!=context&&null!=filter){
                final BroadcastReceiver receiver=new HandlerBroadcastReceiver(){
                    @Override
                    public void onBroadcastReceived(Context context, Intent intent, String action) {
                        super.onBroadcastReceived(context, intent, action);
                         BroadcastAcceptor.this.onBroadcastReceived(context,intent,action);
                         Set<OnBroadcastReceived> set=null!=mCallbacks?mCallbacks.keySet():null;
                         Iterator<OnBroadcastReceived> iterator=null!=set?set.iterator():null;
                         if (null!=iterator&&iterator.hasNext()) {
                             OnBroadcastReceived callback;
                             do {
                                if (null!=(callback=iterator.next())){
                                    callback.onBroadcastReceived(context,intent,action);
                                }
                             } while (iterator.hasNext());
                         }
                    }
                };
                context.registerReceiver(receiver,filter);
                mBroadcastReceiver=receiver;
                Debug.W(getClass(),"Registered broadcast listener.");
                return true;
            }
           Debug.W(getClass(),"Can't registered broadcast listener.context="+context+" filter="+filter);
           return false;
       }
       return false;
   }

   public final boolean isRegisted(){
       return null!=mBroadcastReceiver;
   }

   public final boolean unregistere(Context context){
       if (null!=mBroadcastReceiver){
           if (null!=context){
               context.unregisterReceiver(mBroadcastReceiver);
               mBroadcastReceiver=null;
               return true;
           }
           Debug.W(getClass(),"Can't registe broadcast listener.context="+context);
           return false;
       }
       return false;
   }
}
