package com.merlin.network;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.merlin.broadcast.HandlerBroadcastReceiver;
import com.merlin.debug.Debug;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;


public class NetworkListener {
    private static final WeakHashMap<OnNetworkChange,Long> mCallbacks=new WeakHashMap<>();
    private static WeakReference<Context> mContext;
    private static NetBroadcast mNetBroadcast;
    private final Network mNetWork=new Network();

    public interface OnNetworkChange{
        void onNetworkChanged(boolean connected, String currType, String lastType);
    }

    public  NetworkListener(Context context){
        mContext=null!=context?new WeakReference<Context>(context):null;
    }

    public boolean add(OnNetworkChange listener){
        if (null!=listener&&null!=mCallbacks){
             if (!mCallbacks.containsKey(listener)){
                 mCallbacks.put(listener,System.currentTimeMillis());
                 checkRun("After add listener.");
                 String connect=mNetWork.getConnectedType(getContext());
                 notifyChanged(Network.isConnected(connect),connect,connect,listener);
                return true;
             }
             return false;
        }
        Debug.W(getClass(),"Can't add network change listener.listener="+listener+" mCallbacks="+mCallbacks);
        return false;
    }

    public boolean remove(OnNetworkChange listener){
        if (null!=listener&&null!=mCallbacks){
            if (null!=mCallbacks.remove(listener)){
                checkRun("After remove listener");
                return true;
            }
            return false;
        }
        Debug.W(getClass(),"Can't remove network change listener.listener="+listener+" mCallbacks="+mCallbacks);
        return false;
    }

    private boolean checkRun(String debug){
        boolean needRun=(null!=mCallbacks?mCallbacks.size():0)>0;//Run just when size more than one
        return enable(needRun,debug);
    }

    public final boolean enable(boolean enable,String debug){
       final Context context=null!=mContext?mContext.get():null;
        if (enable){
            if (null==mNetBroadcast){
                if (null!=context){
                    NetBroadcast broadcast=new NetBroadcast(context);
                    context.registerReceiver(broadcast,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                    mNetBroadcast=broadcast;
                    Debug.D(getClass(),"Started network listener "+(null!=debug?debug:".")+" size="+(null!=mCallbacks?mCallbacks.size():-1));
                    return true;
                }
                Debug.E(getClass(),"Can't start network listener "+(null!=debug?debug:".")+" context="+context);
                return false;
            }
        }else{
            if (null!=mNetBroadcast){
                if (null!=context){
                    context.unregisterReceiver(mNetBroadcast);
                    mNetBroadcast=null;
                    Debug.D(getClass(),"Stopped network listener "+(null!=debug?debug:"."));
                    return true;
                }
                Debug.E(getClass(),"Can't stop network listener "+(null!=debug?debug:".")+" context="+context);
                return false;
            }
        }
        return false;
    }

    private void notifyChanged(boolean connected,String currType,String lastType,OnNetworkChange change){
        if (null!=change){//Just notify one
            change.onNetworkChanged(connected,currType,lastType);
        }else{//Notify all
            Set<OnNetworkChange> set=null!=mCallbacks?mCallbacks.keySet():null;
            Iterator<OnNetworkChange> iterator=null!=set?set.iterator():null;
            if(null!=iterator&&iterator.hasNext()){
                do {
                    if (null!=(change=iterator.next())){
                        change.onNetworkChanged(connected,currType,lastType);
                    }
                }while (iterator.hasNext());
            }
        }
    }

    public final Context getContext(){
        return null!=mContext?mContext.get():null;
    }

    private final class NetBroadcast extends HandlerBroadcastReceiver {
        private Runnable mCheckRunnable;
        private String mLastConnected;

        public NetBroadcast(Context context){
            mLastConnected=mNetWork.getConnectedType(context);//Init
        }

        @Override
        public void onBroadcastReceived(final Context context, Intent intent, String action) {
            super.onBroadcastReceived(context, intent, action);
            if (null!=mCheckRunnable){
                return;//Not need check within shortly time
            }
            mCheckRunnable=new Runnable() {
                @Override
                public void run() {
                    mCheckRunnable=null;//Clean
                    String type=mNetWork.getConnectedType(context);
                    if (null!=type?(null==mLastConnected||!type.contentEquals(mLastConnected)):null!=mLastConnected){//If changed
                        notifyChanged(Network.isConnected(type),type,mLastConnected,null);
                        mLastConnected=type;
                    }
                }
            };
            mHandler.postDelayed(mCheckRunnable,800);
        }
    }


}
