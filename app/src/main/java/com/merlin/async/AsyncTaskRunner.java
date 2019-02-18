package com.merlin.async;

import android.os.Handler;
import android.os.Looper;

import com.merlin.debug.Debug;
import com.merlin.thread.UIThreadPost;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncTaskRunner<T extends AsyncTask> {
    private final UIThreadPost mUiPost=new UIThreadPost();
    private final Handler mHandler=new Handler(Looper.getMainLooper());
    private final ThreadPoolExecutor mService;
    private final Map<T,Future> mTaskQueue=new LinkedHashMap<>();
    public AsyncTaskRunner(){
        this(3);
    }

    public interface Callback{
        public final static int TASK_FINISH_FAILED_INVALID=-11231423;

        public final static int TASK_FINISH_SUCCEED=-11231424;

        public final static int TASK_FINISH_FAILED_ALREADY_EXIST=-11231425;

        public final static int TASK_FINISH_FAILED_INTERRUPTED=-11231426;

        public final static int TASK_FINISH_FAILED_UNKNOW=-11231427;

        public final static int TASK_FINISH_FAILED_CANCELED=-11231428;

    }

    public interface OnTaskFinish <T>extends Callback{
        void onTaskFinish(boolean succeed, AsyncTask.Finish finish, T task);
    }

    public AsyncTaskRunner(int corePoolSize){
        this(corePoolSize<=0||corePoolSize>=100?1:corePoolSize,20,60,TimeUnit.SECONDS,null);
    }

    public AsyncTaskRunner(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue){
        mService=new ThreadPoolExecutor(corePoolSize<=0?1:corePoolSize,maximumPoolSize<=0||maximumPoolSize>1000?1000:maximumPoolSize,
                keepAliveTime<=0?0:keepAliveTime,null!=unit?unit:TimeUnit.SECONDS,null==workQueue?new ArrayBlockingQueue(10):workQueue,mThreadFactory,mRejectedExecutionHandler);
    }


    private final ThreadFactory mThreadFactory=new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("Merlin's async task thread."+t);
            return t;
        }
    };

    public final boolean isIdle(){
       return getRunningSize()<=0;
    }

    public final  int getRunningSize(){
        Map<T,Future> taskQueue=mTaskQueue;
        if (null!=taskQueue) {
            synchronized (taskQueue) {
                return taskQueue.size();
            }
        }
        return 0;
    }

    public final  boolean isRunning(T task){
        if (null!=task) {
            final Map<T, Future> taskQueue = mTaskQueue;
            if (null!=taskQueue){
                synchronized (taskQueue){
                    Future future=taskQueue.get(task);
                    return null!=future;
                }
            }
        }
        return false;
    }

    public final boolean existEqualed(Object object){
        return null!=object?null!=findEqualed(object,null):false;
    }

    public final T indexEqualed(Object object){
          return null!=object?findEqualed(object,null):null;
    }

    public final T findEqualed(Object object){
        return findEqualed(object,null);
    }

    public final T findEqualed(Object object, List<T> result){
        if (null!=object) {
            final Map<T, Future> taskQueue = mTaskQueue;
            if (null!=taskQueue){
                synchronized (taskQueue){
                    Set<T> set=taskQueue.keySet();
                    Iterator<T> iterator=null!=set?set.iterator():null;
                    T key;
                    T found=null;
                    if (null!=iterator&&iterator.hasNext()){
                        do {
                            if (null!=(key=iterator.next())&&key.equals(object)){
                                  if (null!=result){
                                      found=key;
                                      result.add(key);
                                      continue;//Continue equal next
                                  }else{//Just need equal first one
                                      found=key;
                                     break;//Break to return first one result
                                  }
                            }
                        }while (iterator.hasNext());
                    }
                    return found;
                }
            }
        }
        return null;
    }

    public final boolean cancel(T task, boolean interruptIfRunning){
        if (null!=task) {
            final Map<T, Future> taskQueue = mTaskQueue;
            if (null!=taskQueue){
                synchronized (taskQueue){
                    Future future=taskQueue.get(task);
                    if (null!=future&&!future.isCancelled()&&!future.isDone()){
                        return future.cancel(interruptIfRunning);
                    }
                    return false;
                }
            }
        }
       return false;
    }

    public final boolean submit(final T task, final Callback ...callbacks){
        if (null!=task){
            if (task instanceof AsyncTask.OnTaskPrepareInterrupt&&((AsyncTask.OnTaskPrepareInterrupt)task).onTaskPrepareInterrupt()){
               return false;//Just return if start interrupted
            }
            task.append(true,callbacks);//Append callbacks into task at firstly
            final Map<T,Future> taskQueue=mTaskQueue;
            if (null!=mService&&null!=taskQueue){
                synchronized (taskQueue) {
                    if (!taskQueue.containsKey(task)) {
                        Future future = mService.submit( new Runnable() {
                                @Override
                                public void run() {
                                    AsyncTask.Finish finish=task.run(AsyncTaskRunner.this);
//                                    Debug.D(getClass(),"Remove finish task."+task);
                                    synchronized (taskQueue){
                                        taskQueue.remove(task);//Remove while end
                                    }
                                }
                            }
                        );
                        if (null != future) {
                            taskQueue.put(task, future);
                            return true;
                        }
                        Debug.W(getClass(), "Can't put task future after submit succeed.future=" + future);
                        return false;
                    }
                    Debug.W(getClass(),"Can't submit async task.task already exist.task="+task);
                    notifyTaskFinish(new AsyncTask.Finish(false,Callback.TASK_FINISH_FAILED_ALREADY_EXIST),task);
                }
                return false;//Interrupt later codes
            }
            Debug.W(getClass(),"Can't submit async task.mService="+mService+" taskQueue="+taskQueue+" task="+task);
            notifyTaskFinish(new AsyncTask.Finish(false,Callback.TASK_FINISH_FAILED_INVALID),task);
            return false;
        }
        Debug.W(getClass(),"Can't submit async task.task="+task);
        return false;
    }

    private final RejectedExecutionHandler mRejectedExecutionHandler=new RejectedExecutionHandler(){

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        }
    };


    public final Handler getHandler() {
        return mHandler;
    }

    public final UIThreadPost getUiPost() {
        return mUiPost;
    }

    private void notifyTaskFinish(AsyncTask.Finish finish, final AsyncTask task){
        if (null!=task){
            task.onFinish(finish,AsyncTaskRunner.this,task);
        }
    }
}
