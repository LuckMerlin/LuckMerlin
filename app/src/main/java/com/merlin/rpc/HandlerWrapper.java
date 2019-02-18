package com.merlin.rpc;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class HandlerWrapper {

    private final Handler mHandler=new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what=msg.what;
            final Object obj=msg.obj;
            return onHandleMessage(what,obj,msg);
        }
    });

    protected boolean onHandleMessage(int what,Object obj,Message msg){
        //Do nothing
        return true;
    }

    protected final  Message obtainMessage(int what){
        return obtainMessage(what,null);
    }

    protected final  Message obtainMessage(int what,Object obj){
        return null!=mHandler?null!=obj?mHandler.obtainMessage(what,obj):mHandler.obtainMessage(what):null;
    }

    protected final  boolean sendEmptyMessageDelayed(int what,int delay){
        return null!=mHandler?mHandler.sendEmptyMessageDelayed(what,delay):false;
    }

    protected final  boolean hasMessage(int what){
        return hasMessage(what,null);
    }

    protected final  boolean hasMessage(int what,Object obj){
        return null!=mHandler?null!=obj?mHandler.hasMessages(what,obj):mHandler.hasMessages(what):false;
    }

    protected final  boolean removeMessages(int what){
        return removeMessages(what,null);
    }

    protected final  boolean removeMessages(int what,Object obj){
        if (null!=mHandler){
            if (null!=obj){
                mHandler.removeMessages(what,obj);
            }else{
                mHandler.removeMessages(what);
            }
            return true;
        }
        return false;
    }

    protected final  boolean sendMessage(Message msg){
        return sendMessageDelayed(msg,0);
    }

    protected final  boolean sendMessageDelayed(Message msg,int delay){
        if (null!=msg&&null!=mHandler){
            return mHandler.sendMessageDelayed(msg,delay<=0?0:delay);
        }
        return false;
    }

    protected final boolean post(Runnable runnable){
        return post(runnable,0);
    }

    protected final  boolean removeCallbacksAndMessages(Object token){
        if (null!=mHandler&&null!=token){
            mHandler.removeCallbacksAndMessages(token);
            return true;
        }
        return false;
    }

    protected final boolean post(Runnable runnable,int delay){
        if (null!=runnable&&null!=mHandler){
            return mHandler.postDelayed(runnable,delay<=0?0:delay);
        }
        return false;
    }

    protected final boolean post(Runnable runnable,Object token,int delay){
        if (null!=runnable&&null!=mHandler){
            return mHandler.postAtTime(runnable,token,System.currentTimeMillis()+(delay>=0?delay:0));
        }
        return false;
    }

    protected final boolean removeCallbacks(Runnable runnable){
        return removeCallbacks(runnable,null);
    }

    protected final boolean removeCallbacks(Runnable runnable,Object obj){
        if (null!=mHandler&&null!=runnable){
            if (null!=obj){
                mHandler.removeCallbacks(runnable,obj);
            }else{
                mHandler.removeCallbacks(runnable);
            }
            return true;
        }
        return false;
    }

}
