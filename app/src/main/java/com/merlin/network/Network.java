package com.merlin.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class Network {
    public final static  String UNKNOW="unknow";
    public final static  String WIFI_CONNECTED="wifi";
    public final static  String MOBILE_CONNECTED="mobile";
    public final static  String DISCONNECTED="disconnected";

    public static boolean isConnected(String type){
        return  null!=type&&(type.equalsIgnoreCase(WIFI_CONNECTED)||type.equals(MOBILE_CONNECTED));
    }

    /**
     * @deprecated  replace with getConnectedType
     */
    public String  getConnectType(Context context){
         return getConnectedType(context);
    }

    public String  getConnectedType(Context context){
        if (null!=context){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            String state=DISCONNECTED;
            if (null!=mobNetInfo&&mobNetInfo.isConnected()){
                state=MOBILE_CONNECTED;
            }else if (null!=wifiNetInfo&&wifiNetInfo.isConnected()){
                state=WIFI_CONNECTED;
            }
            return state;
        }
        return UNKNOW;
    }

    public final NetworkInfo getMobileNetwork(Context context){
        return null!=context? getNetwork(context,ConnectivityManager.TYPE_MOBILE):null;
    }

    public final NetworkInfo getWifiNetwork(Context context){
        return null!=context? getNetwork(context,ConnectivityManager.TYPE_WIFI):null;
    }

    public final NetworkInfo getNetwork(Context context,int type){
        ConnectivityManager cm = null!=context?(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE):null;
        return null!=cm? cm.getNetworkInfo(type):null;
    }


}
