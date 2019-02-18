package com.merlin.network;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.merlin.debug.Debug;

import java.util.List;

public class WifiConnect {
    private static final String WPA2 = "WPA2";
    private static final String WPA = "WPA";
    private static final String WEP = "WEP";
    private static final String NONE = "None";
    private static final String[] SECURITY_MODES = {WEP, WPA, WPA2, NONE};

    public WifiInfo connect(WifiManager manager, ScanResult result, String password){
        final String ssid=null!=result?result.SSID:null;
        if (null!=ssid&&null!=manager){
            String securityMode = null!=result?getSecurityMode(result):null;//Get wifi security mode
            WifiConfiguration config = createConfiguration(ssid,password, securityMode);
            if (null==config){
                Debug.W(getClass(), "Can't connect Wifi.create config failed.config="+config+" ssid="+ssid+" pwd="+password+" securityMode="+securityMode);
                return null;//Interrupt later codes
            }
            disconnectWifi(manager,ssid,true);//Try remove existed ssid at firstly
            int networkID = manager.addNetwork(config);
            boolean succeed=manager.enableNetwork(networkID, true);
            Debug.D(getClass(),"Connecting wifi.ssid="+ssid);
            return manager.getConnectionInfo();
        }
        Debug.W(getClass(),"Can't connect wifi. ssid="+ssid+" manager="+manager+" result="+result);
        return null;
    }

    public  boolean disconnectWifi(WifiManager manager,String ssid){
         return disconnectWifi(manager,ssid,false);
    }

    public final   int removeAllExistNetwork(WifiManager manager,boolean remove,String debug){
       List<WifiConfiguration> configurations= manager.getConfiguredNetworks();
       int count=null!=configurations?configurations.size():0;
        WifiConfiguration configuration;
        int result=0;
        for (int i = 0; i < count; i++) {
            if (null!=(configuration=configurations.get(i))){
                result=result+1;
                String ssid=configuration.SSID;
                int networkId=configuration.networkId;
                Debug.W(getClass(),"Disable "+(remove?"and remove ":"")+" wifi "+(null!=debug?debug:".")+" old ssid="+ssid);
                manager.disableNetwork(networkId);//Interrupt later codes
                if (remove){
                    manager.removeNetwork(networkId);
                }
            }
        }
       return result;
    }

    public  boolean disconnectWifi(WifiManager manager,String ssid,boolean remove){
        if (null!=ssid&&!ssid.isEmpty()&&null!=manager){
            WifiConfiguration config = getRememberedConfig(manager,ssid);//Get remember wifi config
            if (config != null) {
                int networkId=config.networkId;
                Debug.W(getClass(),"Disconnect wifi.ssid="+ssid);
                manager.disableNetwork(networkId);//Interrupt later codes
                if (remove){
                    Debug.W(getClass(),"Remove wifi.ssid="+ssid);
                    manager.removeNetwork(networkId);
                }
                return true;
            }
//            Debug.W(getClass(),"Can't disconnect wifi.Config not exist.ssid="+ssid+" config="+config);
            return false;//Interrupt later codes
        }
        Debug.W(getClass(),"Can't disconnect wifi.ssid="+ssid+" manager="+manager);
        return false;
    }

    public WifiConfiguration getRememberedConfig(WifiManager manager,String ssid) {
        if (null!=ssid&&null!=manager){
            List<WifiConfiguration> configs = manager.getConfiguredNetworks();
            if (null!=configs){
                String temp;
                for (WifiConfiguration f:configs) {
                    if (null!=f&&null!=(temp=f.SSID)&&temp.equals("\"" + ssid + "\"")){
                        return f;
                    }
                }
            }
            return null;//interrupt later codes
        }
        Debug.W(getClass(),"Can't get remember wifi config.ssid="+ssid+" manager="+manager);
        return null;
    }

    public String getSecurityMode(ScanResult result) {
        final int modeLength=null!=SECURITY_MODES&&null!=result?SECURITY_MODES.length:0;
        if (modeLength>0){
            final String cap = null!=result?result.capabilities:null;
            String mode;
            for (int i = modeLength - 1; i >= 0; i--) {
                if (null!=(mode=SECURITY_MODES[i])&&cap.contains(mode)) {
                    return mode;
                }
            }
            return NONE;//Interrupt later codes
        }
        Debug.W(getClass(),"Can't get wifi security.modeLength="+modeLength+" result="+result+" SECURITY_MODES"+SECURITY_MODES);
        return NONE;
    }

    public WifiConfiguration createConfiguration(String ssid, String password, String pwdType) {
        WifiConfiguration config = new WifiConfiguration();
        pwdType=null==pwdType?NONE:pwdType;
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
//        WifiConfiguration tempConfig = getWifiRememberedConfig(SSID);
//        if(tempConfig != null) {
//            mWifiManager.removeNetwork(tempConfig.networkId);
//        }
        if (pwdType.equals(NONE)){//WIFICIPHER_NOPASS
//          config.wepKeys[0] = "";
//          config.wepTxKeyIndex = 0;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        if (pwdType.equals(WEP)){ //WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (pwdType.equals(WPA) || pwdType.equals(WPA2)){ //WIFICIPHER_WPA
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

}
