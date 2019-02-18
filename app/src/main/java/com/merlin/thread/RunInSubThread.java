/**
 * FileName: RunInSubThread
 * Author: LuckMerlin <a href="http://www.luckmerlin.com">LuckMerlin</a>
 * CreateDate: 2018\12\27 0027 10:38
 */
package com.merlin.thread;

import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C), 2018-2018, 偶家科技 <a href="http://www.oplushome.com">O+</a>
 * TODO 
 * Version: 0.0
 * History:
 */
public class RunInSubThread {

    public boolean run(Runnable runnable){
         if (null!=runnable){
             if (Looper.myLooper() != Looper.getMainLooper()){
                 runnable.run();
             }else{
//
//
//
//               int corePoolSize,
//               int maximumPoolSize,
//               long keepAliveTime,
//               TimeUnit unit,
//               BlockingQueue<Runnable> workQueue
//                 ThreadPoolExecutor executor=new ThreadPoolExecutor();
//                 ThreadPoolExecutor executor=ThreadPoolExecutor.CallerRunsPolicy;
//                 runnable.run();
             }
         }
         return false;
    }
}