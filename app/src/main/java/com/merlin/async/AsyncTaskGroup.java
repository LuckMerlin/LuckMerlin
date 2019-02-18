package com.merlin.async;

import android.os.Handler;

import com.merlin.debug.Debug;
import com.merlin.thread.UIThreadPost;

import java.util.ArrayList;
import java.util.List;

public class AsyncTaskGroup extends AsyncTask {
    private final List<AsyncTask> mTaskList=new ArrayList<>();
    private boolean mAutoInterruptIfFailed=false;
    private AsyncTask mRunning;

    public AsyncTaskGroup(AsyncTaskRunner.Callback...callbacks){
        this(null,null,callbacks);
    }

    public AsyncTaskGroup(String name,AsyncTaskRunner.Callback...callbacks){
        this(name,null,callbacks);
    }

    public AsyncTaskGroup(String name,Retry retry, AsyncTaskRunner.Callback...callbacks){
        this(name,retry,null,callbacks);
    }

    public AsyncTaskGroup(String name,Retry retry, UiThreadNotifyResolver uiNotifyResolver, AsyncTaskRunner.Callback...callbacks){
        super(name,retry,uiNotifyResolver,callbacks);
    }

    public final boolean cancel(String debug){
        return cancel(debug,false);
    }

    public final boolean cancel(String debug,boolean interrupt){
        if (super.cancel(debug,false)){//Cancel group without interrupt
            AsyncTask task=mRunning;
            if (interrupt&&null!=task){//If need interrupt running
                synchronized (task) {
                    task.cancel(debug, true);
                }
            }
        }
       return false;
    }

    public final int cancel(String debug,boolean interrupt,AsyncTask ...tasks){
        final int count=null!=tasks?tasks.length:0;
        AsyncTask task;
        int canceld=0;
        for (int i = 0; i < count; i++) {
            if (null!=(task=tasks[i])){
                canceld=canceld+(task.cancel(debug,interrupt)?1:0);
            }
        }
        return 0;
    }

    public final boolean add(AsyncTask task){
        List<AsyncTask> list=mTaskList;
      if (null!=task&&null!=list){
        synchronized (list){
           if (!mTaskList.contains(task)&&mTaskList.add(task)){
               return true;
           }
        }
      }
       return false;
    }

    public final boolean remove(AsyncTask task){
        List<AsyncTask> list=mTaskList;
        if (null!=task&&null!=list){
            synchronized (list){
                if (mTaskList.contains(task)&&mTaskList.remove(task)){
                    return true;
                }
            }
        }
        return false;
    }

    public final int taskSize(){
        List<AsyncTask> tasks=mTaskList;
        if (null!=tasks){
            synchronized (tasks){
                return null!=tasks?tasks.size():0;
            }
        }
        return -1;
    }

    public final boolean enableAutoInterruptWhenFailed(boolean enable){
        if (mAutoInterruptIfFailed!=enable){
            mAutoInterruptIfFailed=enable;
            return true;
        }
        return false;
    }

    public final boolean existTask(AsyncTask task){
        List<AsyncTask> list=null!=task?mTaskList:null;
        if (null!=list){
            synchronized (list){
                return null!=list&&list.contains(task);
            }
        }
        return false;
    }

    public final boolean isAutoInterruptIfFailed() {
        return mAutoInterruptIfFailed;
    }

    public final int cleanTasks(){
      int count=null!=mTaskList?mTaskList.size():0;
      if (count>0){
          mTaskList.clear();
      }
      return count;
    }

    protected void onGroupStart(){
      //Do nothing
    }

    protected void onGroupStop(){
        //Do nothing
    }

    public final AsyncTask getRunning() {
        return mRunning;
    }

    @Override
    public final Finish onTaskRun(AsyncTaskRunner runner)  {
        mRunning=null;//Clean
        onGroupStart();
        List<AsyncTask> list=mTaskList;
        int what=AsyncTaskRunner.Callback.TASK_FINISH_FAILED_INVALID;//Set failed as default finish
        if (null!=list){
            final boolean autoInterruptIfFailed=mAutoInterruptIfFailed;
            synchronized (list){
                int count=null!=list?list.size():0;
                if (count>0){
                    what=AsyncTaskRunner.Callback.TASK_FINISH_SUCCEED;//Set succeed as default finish
                    AsyncTask task;
                    for (int i = 0; i < count; i++) {
                        mRunning=null;//Clean running task
                        if (isCanceled()){
                            what=AsyncTaskRunner.Callback.TASK_FINISH_FAILED_CANCELED;
                            Debug.D(getClass(),"Task group canceled.i="+i+" group="+AsyncTaskGroup.this);
                            break;
                        }
                        if (null!=(task=list.get(i))){
                            mRunning=task;
                            Finish finish=task.run(runner);
                            super.onFinish(finish,runner,task);
                            boolean succeed=null!=finish&&finish.isSucceed();
                            if (!succeed&&autoInterruptIfFailed){//Auto interrupt later task while failed
                                what=AsyncTaskRunner.Callback.TASK_FINISH_FAILED_INTERRUPTED;
                                Debug.D(getClass(),"To interrupt task group while child failed.i="+i+" child="+task+" group="+AsyncTaskGroup.this);
                                break;
                            }
                        }
                    }
                    mRunning=null;//Clean running task
                }
            }
        }
        onGroupStop();
        return new Finish(what==AsyncTaskRunner.Callback.TASK_FINISH_SUCCEED,what);
    }
}
