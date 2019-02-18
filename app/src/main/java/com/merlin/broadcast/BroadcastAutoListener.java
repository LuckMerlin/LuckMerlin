package com.merlin.broadcast;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.merlin.debug.Debug;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class BroadcastAutoListener{
    private static HandlerBroadcastReceiver mReceiver;
    private final WeakReference<Context> mContext;
    private final static List<Listener> mListeners=new ArrayList<>();
    protected abstract IntentFilter onResolveIntentFilter();

    protected abstract void onIterateListener(Listener listener,Context context, Intent intent, String action);

    public interface  Listener{

    }

    public BroadcastAutoListener(Context context){
        mContext=null!=context?new WeakReference(context):null;
    }

    private final boolean unregiste(){
        if (null!=mReceiver){
            Context context=null!=mContext?mContext.get():null;
            if (null!=context){
                context.unregisterReceiver(mReceiver);
                mReceiver=null;
                Debug.D(getClass(),"Unregiste broadcast auto listener."+this);
                return true;
            }
            Debug.W(getClass(),"Can't unregiste broadcast auto listener."+this);
            return false;//Interrupt later codes
        }
        return false;
    }

    private final boolean registe(IntentFilter filter){
        if (null==mReceiver){
            Context context=null!=mContext?mContext.get():null;
            if (null!=context&&null!=filter){
                mReceiver=new HandlerBroadcastReceiver(){
                    @Override
                    public void onBroadcastReceived(Context context, Intent intent, String action) {
                        int count=null!=mListeners?mListeners.size():0;
                        if (count>0){
                            for (Listener listener:mListeners) {
                                onIterateListener(listener,context,intent,action);
                            }
                        }
                    }
                };
                context.registerReceiver(mReceiver,filter);
                Debug.D(getClass(),"Registe broadcast auto listener."+this);
                return true;
            }
            Debug.W(getClass(),"Can't registe broadcast.context="+context+" filter="+filter);
            return false;
        }
        return false;
    }

    public final boolean isRegisted(){
        return null!=mReceiver;
    }

    public final boolean addListener(Listener listener){
        if (null!=listener&&null!=mListeners){
            if (!mListeners.contains(listener)){
                mListeners.add(listener);
                autoCheck();
                return true;
            }
            return false;//Interrupt later codes
        }
        Debug.W(getClass(),"Can't add listener.listener="+listener+" mListeners="+mListeners);
        return false;
    }

    public final boolean removeListener(Listener listener){
        if (null!=listener&&null!=mListeners){
            if (mListeners.contains(listener)){
                mListeners.remove(listener);
                autoCheck();
                return true;
            }
            return false;//Interrupt later codes
        }
        Debug.W(getClass(),"Can't remove listener.listener="+listener+" mListeners="+mListeners);
        return false;
    }

    private boolean autoCheck(){
        int size=null!=mListeners?mListeners.size():0;
        final  boolean registed=isRegisted();
        if (size>=0){
              if (!registed){
                   Debug.D(getClass(),"Auto to registe broadcast listener while exist listener.size="+size+" "+this);
                  return registe(onResolveIntentFilter());
              }
        }else{
            if (registed){
                Debug.D(getClass(),"Auto to unregiste broadcast listener while not exist listener.size="+size+" "+this);
               return unregiste();
            }
        }
        return false;
    }

    protected final Context getContext(){
        return null!=mContext?mContext.get():null;
    }

}
