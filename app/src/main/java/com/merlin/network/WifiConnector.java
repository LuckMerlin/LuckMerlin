package com.merlin.network;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.merlin.debug.Debug;

public class WifiConnector extends WifiFinder {
    private final WifiConnect mConnect=new WifiConnect();
    private CheckAgain mConnectedCheck;

    public final String getConnectingSSID() {
        return null!=mConnectedCheck?mConnectedCheck.getSSID():null;
    }

    public final void cancel(){
        if (isFinding()){
            cancel();
        }
         if (null!=mConnectedCheck){
             mConnectedCheck.cancel();
         }
    }

    public interface OnWifiConnect{
        int WIFI_CONNECT_SUCCEED=10000;
        int WIFI_CONNECT_FAILED=10001;
        int WIFI_CONNECT_FAILED_NOT_ENABLE=10002;
        int WIFI_CONNECT_ALREADY_CONNECTED=10003;
        int WIFI_CONNECT_ENABLING=10004;
        int WIFI_CONNECT_ENABLED=10005;
        int WIFI_CONNECT_ENABLE_FAILED=10006;
        int WIFI_CONNECT_FIND_SUCCEED=10007;
        int WIFI_CONNECT_CANCEL=10008;
        int WIFI_CONNECTING=10009;
        int WIFI_CONNECT_CANCEL_BY_NEW=10010;
        int WIFI_CONNECT_TIMEOUT=10011;
        void onWifiConnect(int what, String ssid, String password);
    }

    public WifiConnector(){
         this(null);
    }

    public WifiConnector(Handler handler){
        super(handler);
    }

    public boolean connect(final Context context,final String ssid,final boolean reconnect, final String password, boolean autoEnable, final OnWifiConnect callback){
        if (null!=ssid&&null!=context){
            boolean enable=isWifiEnable(context);//Check enable
            if (!enable){//Not enable
                if (!autoEnable){
                    Debug.D(getClass(),"Can't connect wifi,Wifi not enable.");
                    return notifyCallback(new int[]{OnWifiConnect.WIFI_CONNECT_FAILED,OnWifiConnect.WIFI_CONNECT_FAILED_NOT_ENABLE},ssid,password,callback);
                }
                setWifiEnable(context,true);
                notifyCallback(new int[]{OnWifiConnect.WIFI_CONNECT_ENABLING},ssid,password,callback);
                notifyCallback(new int[]{setWifiEnable(getWifiManager(context),true)?OnWifiConnect.WIFI_CONNECT_ENABLED:OnWifiConnect.WIFI_CONNECT_ENABLE_FAILED},ssid,password,callback);
            }
           String finding= getFindingSSID();
           if (null!=finding&&!finding.contentEquals(ssid)){
              Debug.D(getClass(),"To cancel find last wifi before new find start.finding="+finding+" ssid="+ssid);
              cancelFind();
           }
           final WifiManager manager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
           findNearby(context, ssid,autoEnable, new WifiFinder.OnWifiFindFinish() {
                @Override
                public void onWifiFindFinish(int what, final String s, ScanResult scanResult) {
                       switch (what){
                           case WIFI_FIND_SUCCEED:
                               notifyCallback(new int[]{OnWifiConnect.WIFI_CONNECT_FIND_SUCCEED},ssid,password,callback);
                               if (isConnectedWifi(getWifiManager(context),ssid)&&!reconnect){//Check if connected
                                   notifyCallback(new int[]{OnWifiConnect.WIFI_CONNECT_SUCCEED,OnWifiConnect.WIFI_CONNECT_ALREADY_CONNECTED},ssid,password,callback);
                               }else{
//                                   String connectedSSID=getConnectedSSID(manager);
                                   mConnect.removeAllExistNetwork(manager,true,"Before connect new wifi.ssid="+ssid);
//                                   if (null!=connectedSSID&&!connectedSSID.equals(ssid)){
//                                       Debug.D(getClass(),"To remove connected wifi before connect new wifi.newWifi="+ssid+" connected="+connectedSSID);
//                                       mConnect.disconnectWifi(manager,ssid,true);
//                                   }
                                   notifyCallback(new int[]{OnWifiConnect.WIFI_CONNECTING},ssid,password,callback);
                                   Debug.D(getClass(),"To connect wifi after found.ssid="+ssid);
                                   mConnect.connect(manager,scanResult,password);
                                   checkConnected(manager,ssid,password,null,callback);
                               }
                               break;
                           case WIFI_FIND_FAILED_DISABLED:
                               Debug.D(getClass(),"Can't connect wifi,Wifi find failed disabled.");
                               notifyCallback(new int[]{OnWifiConnect.WIFI_CONNECT_FAILED,OnWifiConnect.WIFI_CONNECT_FAILED_NOT_ENABLE},ssid,password,callback);
                               break;
                           case WIFI_FIND_FAILED_CANCEL:
                               Debug.D(getClass(),"Can't connect wifi,Wifi find failed cancel.");
                               notifyCallback(new int[]{OnWifiConnect.WIFI_CONNECT_FAILED,OnWifiConnect.WIFI_CONNECT_CANCEL},ssid,password,callback);
                               break;
                       }
                }
            });
           return false;//Interrupt later codes
        }
        Debug.W(getClass(),"Can't connect wifi.");
        return false;
    }

    private String getConnectedSSID(WifiManager manager){
        WifiInfo info=null!=manager?getConnectedWifi(manager):null;
        String infoSSID=null!=info?getSSID(info):null;
        return infoSSID;
    }

    private boolean  checkConnected(final WifiManager manager, final String ssid, final String password,Long firstCheckTime, final OnWifiConnect callback){
        if(null!=ssid&&null!=manager){
            final long starTime=null==firstCheckTime?System.currentTimeMillis():firstCheckTime;//Set start time
            CheckAgain check=mConnectedCheck;
            final String currSSID=null!=check?check.getSSID():null;
            final boolean isChecking=null!=currSSID&&currSSID.contentEquals(ssid);
            if (null!=check){//Remove exist callback at firstly
                mHandler.removeCallbacks(check);
                if (!isChecking){
                    Debug.D(getClass(),"Cancel check last wifi connection before check new wifi.last="+currSSID+" new="+ssid);
                    notifyCallback(new int[]{OnWifiConnect.WIFI_CONNECT_FAILED,OnWifiConnect.WIFI_CONNECT_CANCEL_BY_NEW},currSSID,password,callback);
                }
            }
            WifiInfo info=getConnectedWifi(manager);
            SupplicantState supplicant=null!=info?info.getSupplicantState():null;
            String infoSSID=getSSID(info);
            long duration=System.currentTimeMillis()-starTime;
            if (duration>15*1000 ){
                mConnectedCheck=null;//Set NULL while failed INACTIVE
                Debug.D(getClass(),"Failed connect wifi.ssid="+ssid+" infoSSID="+infoSSID+" supplicant="+supplicant+" duration="+duration);
                notifyCallback(new int[]{OnWifiConnect.WIFI_CONNECT_FAILED,OnWifiConnect.WIFI_CONNECT_TIMEOUT},ssid,password,callback);
                return false;
            }
            if (null!=infoSSID&&infoSSID.contentEquals(ssid)&&supplicant==SupplicantState.COMPLETED){
                mConnectedCheck=null;//Set NULL while failed
                Debug.D(getClass(),"Wifi connect succeed.ssid="+ssid);
                notifyCallback(new int[]{OnWifiConnect.WIFI_CONNECT_SUCCEED},ssid,password,callback);
                return true;//Interrupt later codes
            }
            mConnectedCheck=isChecking?check:new CheckAgain(ssid) {
                @Override
                public void run() {
                    checkConnected(manager,ssid,password,starTime,callback);
                }
            };
            mHandler.postDelayed(mConnectedCheck,1000);
            return false;//Interrupt later codes
        }
        Debug.W(getClass(),"Can't check wifi connected.ssid="+ssid+" manager="+manager);
        return false;
    }

    private boolean notifyCallback(int[] whats,String ssid,String password,OnWifiConnect callback){
        int count=null!=whats&&null!=callback?whats.length:0;
        if (count>0){
            for (int i = 0; i < count; i++) {
                callback.onWifiConnect(whats[i],ssid,password);
            }
            return true;
        }
          return false;
    }

}
