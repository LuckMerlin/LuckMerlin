package com.merlin.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.merlin.debug.Debug;

import java.util.List;

public class PackageQuery {

    public List<ResolveInfo> queryIntentServicesWithMeta(Context context, Intent intent){
        if (null!=intent&&null!=context){
            PackageManager pm=context.getPackageManager();
            List<ResolveInfo> infos= null!=pm?pm.queryIntentServices(intent,PackageManager.MATCH_ALL|PackageManager.GET_META_DATA):null;
            return infos;
        }
        Debug.W(getClass(),"Can't query package with intent.context="+context+" intent="+intent);
        return null;
    }

}
