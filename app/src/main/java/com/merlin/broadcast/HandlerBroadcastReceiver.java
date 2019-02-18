package com.merlin.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

public  class HandlerBroadcastReceiver extends BroadcastReceiver   {
    protected final Handler mHandler=new Handler(Looper.getMainLooper());

    public final static class BroadcastPack{
       private final Context mContext;
       private final Intent mIntent;

       public BroadcastPack(Context context,Intent intent){
          mContext=context;
          mIntent=intent;
       }

        public Intent getIntent() {
            return mIntent;
        }

        public Context getContext() {
            return mContext;
        }
    }

    public interface OnBroadcastReceived{
         void onBroadcastReceived(Context context, Intent intent, String action);
    }

    public void onBroadcastReceived(Context context, Intent intent, String action) {
        //Do nothing
    }


    @Override
    public final void onReceive(final Context context,final Intent intent) {
            final String action=null!=intent?intent.getAction():null;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onBroadcastReceived(context,intent,action);
                }
            });
    }


}
