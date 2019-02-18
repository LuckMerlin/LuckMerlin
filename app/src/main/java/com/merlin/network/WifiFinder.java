package com.merlin.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;

import com.merlin.debug.Debug;

import java.util.List;

public class WifiFinder extends WifiNetwork{
    protected final Handler mHandler;
    private CheckAgain mFindCheckAgain;

    public interface OnWifiFindFinish{
        int WIFI_FIND_SUCCEED=756745;
        int WIFI_FIND_FAILED_DISABLED=756746;
        int WIFI_FIND_FAILED_CANCEL=756747;
        void onWifiFindFinish(int what, String ssid, ScanResult scanResult);
    }

    public WifiFinder(){
       this(null);
    }

    public WifiFinder(Handler handler){
        mHandler=null!=handler?handler:new Handler(Looper.getMainLooper());
    }

    public interface Cancel{
        boolean cancel();
    }

    public final boolean isFinding(){
        return null!=mFindCheckAgain&&!mFindCheckAgain.mCanceled;
    }

    public final boolean cancelFind(){
        Cancel cancel=null!=mFindCheckAgain?mFindCheckAgain.mCancel:null;
        return null!=cancel?cancel.cancel():false;
    }

    public final String getFindingSSID(){
        return null!=mFindCheckAgain?mFindCheckAgain.mSSID:null;
    }

    public final Cancel findNearby(final Context context, final String ssid,final boolean autoEnable, final OnWifiFindFinish callback) {
        if (null!=ssid&&null!=context){
            WifiManager wm =null!=context? (WifiManager) context.getSystemService(Context.WIFI_SERVICE):null;
            if (!wm.isWifiEnabled()&&!autoEnable){
                notifyFind(OnWifiFindFinish.WIFI_FIND_FAILED_DISABLED,ssid,null,callback);
                return null;//Interrupt later codes
            }
            @SuppressLint("MissingPermission") List<ScanResult> results =null!=wm?wm.getScanResults():null;
            if (null!=results&&results.size()>0){
                String scanSSID=null;
                for (ScanResult f:results) {
                    if (null!=f&&null!=(scanSSID=f.SSID)&&scanSSID.contentEquals(ssid)){
                        notifyFind(OnWifiFindFinish.WIFI_FIND_SUCCEED,scanSSID,f,callback);
                        mFindCheckAgain=null;
                        return null;
                    }
                    Debug.D(getClass()," %%%%%%%%%%%%%%%%%%%%% "+scanSSID+" "+ssid);
                }
            }
            if (null!=mFindCheckAgain){
                mHandler.removeCallbacks(mFindCheckAgain);
            }
            mFindCheckAgain=null==mFindCheckAgain||null==mFindCheckAgain.mSSID||!ssid.equals(mFindCheckAgain.mSSID)?new CheckAgain(ssid) {
                @Override
                public void run() {
                      findNearby(context,ssid,autoEnable,callback);
                }
            }:mFindCheckAgain;
            if (mFindCheckAgain.mCanceled){
                notifyFind(OnWifiFindFinish.WIFI_FIND_FAILED_CANCEL,ssid,null,callback);
                return mFindCheckAgain.mCancel;//Interrupt later codes
            }
            Debug.D(getClass(),"%%%%%%%%%%%% 查找网络 %%%%%%%%% "+ssid);
            mHandler.postDelayed(mFindCheckAgain,1000);
            return mFindCheckAgain.mCancel;//Interrupt later codes
        }
        Debug.W(getClass(),"Can't find nearby wifi.ssid="+ssid+" context="+context);
        return null;
    }

    private boolean notifyFind(int what,String ssid,ScanResult result,OnWifiFindFinish callback){
          if (null!=callback){
              callback.onWifiFindFinish(what,ssid,result);
              return true;
          }
          return false;
    }

    @SuppressLint("MissingPermission")
    public final List<ScanResult> findAllNearby(Context context){
        WifiManager wm =null!=context? (WifiManager) context.getSystemService(Context.WIFI_SERVICE):null;
        return null!=wm?wm.getScanResults():null;
    }

    protected abstract class CheckAgain implements Runnable{
       private final String mSSID;
       private boolean mCanceled=false;

       private final Cancel mCancel=new Cancel(){
           @Override
           public boolean cancel() {
               return CheckAgain.this.cancel();
           }
       };

       protected CheckAgain(String ssid){
           mCanceled=false;//DefaultValue must false
           mSSID=ssid;
       }

       public boolean cancel(){
           if (!mCanceled){
               mCanceled=true;
               return true;
           }
           return false;
       }

        public String getSSID() {
            return mSSID;
        }
    }

}
