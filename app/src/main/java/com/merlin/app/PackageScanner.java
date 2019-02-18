package com.merlin.app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.merlin.broadcast.BroadcastAutoListener;
import com.merlin.broadcast.HandlerBroadcastReceiver;
import com.merlin.debug.Debug;

import java.util.HashMap;
import java.util.Map;

public class PackageScanner extends BroadcastAutoListener implements BroadcastAutoListener.Listener {
    private final static int DITHER_WHAT=10001;
    private final Map<String,HandlerBroadcastReceiver.BroadcastPack> mDithering=new HashMap<>();

    private final Handler mHandler=new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
             if (null!=msg){
                 if (msg.what==DITHER_WHAT){
                     Object obj=msg.obj;
                     if (null!=obj&&obj instanceof HandlerBroadcastReceiver.BroadcastPack){
                         HandlerBroadcastReceiver.BroadcastPack pack=(HandlerBroadcastReceiver.BroadcastPack)obj;
                         Intent intent=pack.getIntent();
                          Context context=pack.getContext();
                           String packageName=getPackageNameFromIntent(intent);
                           if (null!=packageName){
                               mDithering.remove(packageName);
                           }
                           onPackageChanged(context,packageName,intent);
                           return true;//Interrupt later codes
                     }
                     Debug.W(getClass(),"Can't handle changed package dither.obj="+obj);
                 }
             }
            return true;
        }
    });
    private int mPackageDitherTime;

    public PackageScanner(Context context){
        super(context);
        addListener(this);
    }

    public final boolean setPackageDither(int ditherTime){
         if (mPackageDitherTime!=ditherTime){
             mPackageDitherTime=ditherTime;
             return true;
         }
        return false;
    }

    protected void onPackageChanged(Context context,String packageName,Intent intent){
        //Do nothing
    }

    private String getPackageNameFromIntent(Intent intent){
        Uri uri=null!=intent?intent.getData():null;
        String packageName=null!=uri?uri.getSchemeSpecificPart():null;
        return packageName;
    }

    @Override
    protected void onIterateListener(Listener listener, Context context, Intent intent, String action) {
        String packageName=null!=intent?getPackageNameFromIntent(intent):null;
        if (null!=packageName&&!packageName.isEmpty()){
            int dither=mPackageDitherTime;
            HandlerBroadcastReceiver.BroadcastPack pack= new HandlerBroadcastReceiver.BroadcastPack(context,intent);
            Message msg= mHandler.obtainMessage(DITHER_WHAT,pack);
            if (dither>0){
                HandlerBroadcastReceiver.BroadcastPack exist=mDithering.get(packageName);
                if (null!=exist){
                    mHandler.removeMessages(DITHER_WHAT,exist);
                }
                mDithering.remove(packageName);
                mDithering.put(packageName,pack);
                mHandler.sendMessageDelayed(msg,dither);
            }else{
                mHandler.sendMessage(msg);
            }
            return;//Interrupt later codes
        }
        Debug.W(getClass(),"Failed get changed package name.packageName="+packageName+" action="+action);
    }

    @Override
    protected final IntentFilter onResolveIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        return filter;
    }

}
