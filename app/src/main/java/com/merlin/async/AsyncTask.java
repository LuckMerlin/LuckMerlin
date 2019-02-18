package com.merlin.async;

import android.os.Handler;

import com.merlin.data.AppendableArray;
import com.merlin.debug.Debug;
import com.merlin.thread.UIThreadPost;

import java.lang.ref.WeakReference;


public abstract class AsyncTask extends AppendableArray<AsyncTaskRunner.Callback>  {
    private UiThreadNotifyResolver mUiResolver;
    private Retry mRetry;
    private final String mName;
    private String mCanceled;
    private WeakReference<Thread> mRunningThread;

    public interface OnTaskPrepareInterrupt{
        boolean onTaskPrepareInterrupt();
    }

    public interface UiThreadNotifyResolver{
        boolean onResolveUiThreadNotify(Finish finish);
    }

    public AsyncTask(AsyncTaskRunner.Callback...callbacks){
        this(null,callbacks);
    }


    public AsyncTask(String name,AsyncTaskRunner.Callback...callbacks){
        this(name,null,callbacks);
    }


    public AsyncTask(String name,Retry retry, AsyncTaskRunner.Callback...callbacks){
        this(name,retry,null,callbacks);
    }

    public AsyncTask(String name,Retry retry, UiThreadNotifyResolver uiNotifyResolver, AsyncTaskRunner.Callback...callbacks){
        super(callbacks);
        mName=null==name||name.isEmpty()?"Merlin's async task ":name;
        mRetry=retry;
        setUiNotifyResolver(uiNotifyResolver);
    }

    @Override
    protected final AsyncTaskRunner.Callback[] onCreateNewInstance(int length) {
        return length>0?new AsyncTaskRunner.Callback[length]:null;
    }

    public final void setUiNotifyResolver(UiThreadNotifyResolver resolver){
        mUiResolver=resolver;
    }


    public final UiThreadNotifyResolver getUiResolver() {
        return mUiResolver;
    }

    public boolean cancel(String debug){
        return cancel(debug,false);
    }

    public boolean cancel(String debug,boolean interrupt){
        if (!isCanceled()){
            mCanceled=debug;
            Thread thread=null;
            if (interrupt&&null!=(thread=null!=mRunningThread?mRunningThread.get():null)){
                Debug.D(getClass(),"Interrupt to cancel task "+(null!=debug?debug:"."));
                thread.interrupt();
            }
            return true;
        }
        return false;
    }

    final Finish run(AsyncTaskRunner runner){
        Finish finish=null;
        try {
            mCanceled=null;//Reset cancel flag
            mRunningThread=new WeakReference<>(Thread.currentThread());//Set running thread for cancel at firstly
            while (true){//Circle for retry
                if (isCanceled()){
                    Debug.D(getClass(),"One task has been canceled "+(null!=mCanceled&&!mCanceled.isEmpty()?mCanceled:".")+this);
                    finish=new Finish(false,AsyncTaskRunner.Callback.TASK_FINISH_FAILED_CANCELED);
                    break;//Not run canceled task,Break to finish task
                }
                finish=onTaskRun(runner);
                Integer retryDelay=onResolveRetryDelay(finish);
                if (null!=retryDelay&&retryDelay>0){
                    Debug.D(getClass(),"Sleep delay to retry task.retryDelay="+retryDelay+" "+getTried()+" "+this);
                    Thread.sleep(retryDelay);
                }else{
                    break;//Not need retry again,Break to finish task
                }
            }
        } catch (InterruptedException e) {
            finish=new Finish(false,AsyncTaskRunner.Callback.TASK_FINISH_FAILED_INTERRUPTED);
        }
        if (null!=mRunningThread){
            mRunningThread.clear();
        }
        mRunningThread=null;//Clean running thread while task finish
        onFinish(finish,runner,this);
        return finish;
    }

    public abstract Finish onTaskRun(AsyncTaskRunner runner) throws InterruptedException;

    protected Integer onResolveRetryDelay(Finish finish){
        return null!=mRetry?mRetry.resolveRetryDelay(finish,AsyncTask.this):null;//Resolve retry delay,default null to mean not need retry
    }

      public static final class Finish{
        private final boolean mSucceed;
        private final int mWhat;

          Finish(boolean succeed,int what){
            mSucceed=succeed;
            mWhat=what;
          }

          public final int getWhat() {
              return mWhat;
          }

          public final boolean isSucceed() {
              return mSucceed;
          }
      }

     protected final Finish generateFinish(boolean succeed){
              return generateFinish(succeed,succeed?AsyncTaskRunner.Callback.TASK_FINISH_SUCCEED:AsyncTaskRunner.Callback.TASK_FINISH_FAILED_UNKNOW);
      }

      protected final Finish generateFinish(boolean succeed,int what){
        return new Finish(succeed,what);
      }

    public final boolean isCanceled() {
        return null!=mCanceled&&!mCanceled.isEmpty();
    }

    final void onFinish(final Finish finish, AsyncTaskRunner runner, final AsyncTask task){
            final int size=null!=task?task.size():0;
            if (size>0){
                UiThreadNotifyResolver uiResolver= null!=task?task.getUiResolver():null;
                boolean notifyInUi=null!=uiResolver?uiResolver.onResolveUiThreadNotify(finish):true;//If notify in UI thread,default true
                for (int i = 0; i < size ; i++) {
                    final AsyncTaskRunner.Callback callback=null!=task?get(i):null;
                    if (null!=callback){
                        final boolean succeed=null!=finish?finish.mSucceed:false;
                        if (notifyInUi){
                            UIThreadPost  uiPost=null!=runner?runner.getUiPost():null;
                            Handler  handler=null!=runner?runner.getHandler():null;
                            (null!=uiPost?uiPost:new UIThreadPost()).runInUiThread(handler, new Runnable() {
                                @Override
                                public void run() {
                                    if (callback instanceof AsyncTaskRunner.OnTaskFinish){
                                        ((AsyncTaskRunner.OnTaskFinish)callback).onTaskFinish(succeed,finish,task);
                                    }
                                }
                            });
                        }else{
                            if (callback instanceof AsyncTaskRunner.OnTaskFinish){
                                ((AsyncTaskRunner.OnTaskFinish)callback).onTaskFinish(succeed,finish,task);
                            }
                        }
                    }

                }
            }
    }


    public final Retry getRetry() {
        return mRetry;
    }

    public final int getTried(){
        return null!=mRetry?mRetry.mTried:-1;
    }

    public static abstract  class Retry
    {
            private int mTried;
            abstract  Integer onResolveRetryDelay(Finish finish,int tried,AsyncTask task);

            final Integer resolveRetryDelay(Finish finish,AsyncTask task){
                Integer delay=onResolveRetryDelay(finish,mTried=((mTried<=0?0:mTried)),task);
                if (null!=delay){
                    mTried++;//Increase if need retry
                }
                return delay;
            }

            public final int getTried() {
                return mTried;
            }
        }

    public static class DefaultRetry extends Retry {
        public final int mMaxTry, mDelay;
        private final float mDelayFactor;

        public DefaultRetry(){
            this(5,3*1000,0);
        }

        public DefaultRetry(int maxTry,int delay,float delayFactor ){
            mMaxTry=maxTry;
            mDelay=delay;
            mDelayFactor=delayFactor;
        }

        public final int resolveTriedDelay(int tried){
            int unintDelay=mDelay<=0?0:mDelay;
            int increase=(int)(unintDelay*(tried*(mDelayFactor<=0?0:mDelayFactor)));
            return unintDelay+increase;
        }

        @Override
        public Integer onResolveRetryDelay(Finish finish, int tried,AsyncTask task) {
            if (!(null!=finish&&finish.mSucceed)){//Just retry after not succeed
                if (tried>=(mMaxTry<=0?0:mMaxTry)){
                    return null;//Not need retry again
                }
                return resolveTriedDelay(tried);
            }
            return null;
        }
    }

    protected final Thread getRunningThread() {
        WeakReference<Thread> thread=mRunningThread;
        if (null!=thread){
            synchronized (thread){
                return thread.get();
            }
        }
        return null;
    }

    //    private static interface Canceller{
//       boolean cancel(String debug) throws TaskCancelException;
//    }
//
//    static final class TaskCancelException extends Exception{
//       private final String mDebug;
//
//       private TaskCancelException(String debug){
//           mDebug=debug;
//       }
//    }

    @Override
    public String toString() {
        return (mName==null?"":mName)+" "+super.toString();
    }

}
