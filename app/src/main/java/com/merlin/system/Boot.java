/**
 * FileName: Boot
 * Author: LuckMerlin <a href="http://www.luckmerlin.com">LuckMerlin</a>
 * CreateDate: 2019\1\8 0008 9:25
 */
package com.merlin.system;

import android.os.SystemClock;

public class Boot {

    public final boolean isInBootInterval(){
        long elapsedTime=SystemClock.elapsedRealtime();
       return elapsedTime>=0&&elapsedTime<60*1000;
    }
}