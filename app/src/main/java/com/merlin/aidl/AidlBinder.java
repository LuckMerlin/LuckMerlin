package com.merlin.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.merlin.Aidl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AidlBinder {
    private final static String DOUBLE_LABEL="AIDL_FROM_LABEL";
    private final Map<ComponentName,Aidl> mConnected=new HashMap<>();
    private final Map<OnAidlConnect,ComponentName> mCallbacks=new ConcurrentHashMap<>();
    private final Handler mHandler=new Handler(Looper.getMainLooper());
    private ComponentName mBinding;

    public interface OnAidlConnect{
        void onAidlConnect(boolean connected,ComponentName cn,Aidl aidl );
    }


    private void notifyConnectChange(boolean connected,ComponentName cn,Aidl aidl){
        if (null!=cn){
            if (connected){
                mConnected.put(cn,aidl);
            }else{
                mConnected.remove(cn);
            }
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    public final boolean bind(Context context, ComponentName from, final ComponentName target, ServiceConnection serviceConnection) {
        if (null != context && null != target) {
            Intent intent=new Intent().setComponent(target);
            if (null!=from) {
                intent.putExtra(DOUBLE_LABEL, from);
            }
            return context.bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Aidl aidl=null!=service?Aidl.Stub.asInterface(service):null;
                    notifyConnectChange(true,target,aidl);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    notifyConnectChange(true,target,getConnected(target));
                }
            },Context.BIND_AUTO_CREATE);
        }
        return false;
    }

    public Aidl getConnected(ComponentName cn){
        Map<ComponentName,Aidl> connected=null!=cn?mConnected:null;
        Set<ComponentName> set=null!=connected?connected.keySet():null;
        Iterator<ComponentName> iterator=null!=set?set.iterator():null;
        if (null!=iterator&&iterator.hasNext()){
            ComponentName componentName;
            do {
                 if (null!=(componentName=iterator.next())&&componentName.equals(cn)){
                     return connected.get(componentName);
                 }
            }while (iterator.hasNext());
        }
        return null;
    }

}
