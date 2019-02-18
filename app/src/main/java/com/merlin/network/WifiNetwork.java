package com.merlin.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.merlin.debug.Debug;

public class WifiNetwork {

    public final WifiInfo getConnectedWifi(WifiManager manager){
        @SuppressLint("MissingPermission") WifiInfo info=null!=manager?manager.getConnectionInfo():null;
        return isConnected(info)?info:null;
    }

    public static final WifiManager getWifiManager(Context context){
        return null!=context?(WifiManager)context.getSystemService(Context.WIFI_SERVICE):null;
    }

    public boolean setWifiEnable(Context context, boolean enable){
        WifiManager wm =null!=context? (WifiManager) context.getSystemService(Context.WIFI_SERVICE):null;
        return null!=wm?setWifiEnable(wm,enable):false;
    }

    @SuppressLint("MissingPermission")
    public boolean setWifiEnable(WifiManager manager, boolean enable){
        return null!=manager?manager.setWifiEnabled(enable):false;
    }

    public boolean isWifiEnable(Context context){
        WifiManager wm =null!=context? (WifiManager) context.getSystemService(Context.WIFI_SERVICE):null;
        return null!=wm?wm.isWifiEnabled():false;
    }

    public String  getConnectedWifiSSID(WifiManager manager){
        WifiInfo wifiInfo=getConnectedWifi(manager);
        return null!=wifiInfo?getSSID(wifiInfo):null;
    }

    public final boolean isConnectedWifi(WifiManager manager){
        return isConnectedWifi(manager,null);
    }

    public final boolean isConnectedWifi(WifiManager manager,String ssid){
        WifiInfo wifiInfo=null!=ssid?getConnectedWifi(manager):null;
        String currSSID=getSSID(wifiInfo);
        if (null!=currSSID&&(null==ssid||currSSID.contentEquals(ssid))){
            return isConnected(wifiInfo);
        }
        return false;
    }

    public final boolean isConnected(WifiInfo wifiInfo){
        SupplicantState supplicant=null!=wifiInfo?wifiInfo.getSupplicantState():null;
        return null!=supplicant&&supplicant==SupplicantState.COMPLETED;
    }


    public final String getSSID(WifiInfo info){
        String  infoSSID=null!=info?info.getSSID():null;
        if (null!=infoSSID&&infoSSID.startsWith("\"")&&infoSSID.endsWith("\"")&&infoSSID.length()>2){
            infoSSID=infoSSID.substring(1,infoSSID.length()-1);
        }
        return infoSSID;
    }

}
