package com.merlin.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.merlin.data.MapIterator;
import com.merlin.debug.Debug;

import java.util.WeakHashMap;

public class ActivityLifeCycle {
    private Application.ActivityLifecycleCallbacks mLifeCycleCallbacks;
    private final Application mApplication;
    private final WeakHashMap<Callback,Long> mCallbacks=new WeakHashMap<>();
    private final MapIterator mIterator=new MapIterator();
    public interface Callback{

    }
    public interface OnActivityCreated extends Callback{
        void onActivityCreated(Activity activity, Bundle savedInstanceState);
    }

    public interface OnActivitySaveInstanceState extends Callback{
       void onActivitySaveInstanceState(Activity activity, Bundle outState);
    }

    public interface OnActivityStarted extends Callback{
        void onActivityStarted(Activity activity);
    }

    public interface OnActivityStopped extends Callback{
        void onActivityStopped(Activity activity);
    }

    public interface OnActivityResumed extends Callback{
        void onActivityResumed(Activity activity);
    }

    public interface OnActivityPaused extends Callback{
        void onActivityPaused(Activity activity);
    }

    public interface OnActivityDestroyed extends Callback{
        void onActivityDestroyed(Activity activity);
    }

    public ActivityLifeCycle(Context context){
        context=null!=context?context.getApplicationContext():null;
        mApplication=null!=context&&context instanceof Application?(Application)context:null;
    }

    private boolean registe(){
        if (null==mLifeCycleCallbacks){
            if (null==mApplication){
                Debug.D(getClass(),"Can't registe activity life cycle callbacks.mApplication="+mApplication);
                return false;
            }
            Debug.D(getClass(),"Registe activity life cycle callbacks.");
            mApplication.registerActivityLifecycleCallbacks(mLifeCycleCallbacks=new Application.ActivityLifecycleCallbacks(){
                @Override
                public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {
                    mIterator.iterate(mCallbacks, new MapIterator.Callback() {
                        @Override
                        public Object onIterate(Object key, Object value,boolean hasNext) {
                            if (null!=key&&key instanceof  OnActivityCreated){
                                ((OnActivityCreated)key).onActivityCreated(activity,savedInstanceState);
                            }
                            return null;
                        }
                    });
                }

                @Override
                public void onActivitySaveInstanceState(final Activity activity, final Bundle outState) {
                    mIterator.iterate(mCallbacks, new MapIterator.Callback() {
                        @Override
                        public Object onIterate(Object key, Object value,boolean hasNext) {
                            if (null!=key&&key instanceof  OnActivitySaveInstanceState){
                                ((OnActivitySaveInstanceState)key).onActivitySaveInstanceState(activity,outState);
                            }
                            return null;
                        }
                    });
                }

                @Override
                public void onActivityStarted(final Activity activity) {
                    mIterator.iterate(mCallbacks, new MapIterator.Callback() {
                        @Override
                        public Object onIterate(Object key, Object value,boolean hasNext) {
                            if (null!=key&&key instanceof  OnActivityStarted){
                                ((OnActivityStarted)key).onActivityStarted(activity);
                            }
                            return null;
                        }
                    });
                }

                @Override
                public void onActivityStopped(final Activity activity) {
                    mIterator.iterate(mCallbacks, new MapIterator.Callback() {
                        @Override
                        public Object onIterate(Object key, Object value,boolean hasNext) {
                            if (null!=key&&key instanceof  OnActivityStopped){
                                ((OnActivityStopped)key).onActivityStopped(activity);
                            }
                            return null;
                        }
                    });
                }

                @Override
                public void onActivityResumed(final Activity activity) {
                    mIterator.iterate(mCallbacks, new MapIterator.Callback() {
                        @Override
                        public Object onIterate(Object key, Object value,boolean hasNext) {
                            if (null!=key&&key instanceof  OnActivityResumed){
                                ((OnActivityResumed)key).onActivityResumed(activity);
                            }
                            return null;
                        }
                    });
                }

                @Override
                public void onActivityPaused(final Activity activity) {
                    mIterator.iterate(mCallbacks, new MapIterator.Callback() {
                        @Override
                        public Object onIterate(Object key, Object value,boolean hasNext) {
                            if (null!=key&&key instanceof  OnActivityPaused){
                                ((OnActivityPaused)key).onActivityPaused(activity);
                            }
                            return null;
                        }
                    });
                }

                @Override
                public void onActivityDestroyed(final Activity activity) {
                    mIterator.iterate(mCallbacks, new MapIterator.Callback() {
                        @Override
                        public Object onIterate(Object key, Object value,boolean hasNext) {
                            if (null!=key&&key instanceof  OnActivityDestroyed){
                                ((OnActivityDestroyed)key).onActivityDestroyed(activity);
                            }
                            return null;
                        }
                    });
                }
            });
            return true;
        }
        return false;
    }

    private boolean unregiste(){
        if (null!=mLifeCycleCallbacks){
            if (null==mApplication){
                Debug.D(getClass(),"Can't unregiste activity life cycle callbacks.mApplication="+mApplication);
                return false;
            }
            Debug.D(getClass(),"Unregiste activity life cycle callbacks.");
            mApplication.unregisterActivityLifecycleCallbacks(mLifeCycleCallbacks);
            mLifeCycleCallbacks=null;
            return true;
        }
        return false;
    }

    public boolean isRegisted(){
        return null!=mLifeCycleCallbacks;
    }

    private void checkForAutoRegiste(){
        int count=null!=mCallbacks?mCallbacks.size():0;
        if (count>0){
            if (!isRegisted()){
                Debug.D(getClass(),"To auto registe activity life cycle after callback more than one.count="+count);
                registe();
            }
        }else{
            if (isRegisted()){
                Debug.D(getClass(),"To auto unregiste activity life cycle after callback less than one.count="+count);
                unregiste();
            }
        }
    }

    public int add(Callback ...callbacks){
        int count=null!=mCallbacks&&null!=callbacks?callbacks.length:0;
        int added=0;
        if (count>0){
            for (Callback f:callbacks){
                if (null!=f&&!mCallbacks.containsKey(f)){
                    mCallbacks.put(f,System.currentTimeMillis());
                    added+=1;
                }
            }
            if (added>0){
                checkForAutoRegiste();
            }
        }
        return added;
    }

    public boolean remove(Callback callback){
        if (null!=callback&&null!=mCallbacks&&mCallbacks.containsKey(callback)){
            mCallbacks.remove(callback);
            checkForAutoRegiste();
            return true;
        }
        return false;
    }

    public final boolean clean(){
        int count=null!=mCallbacks?mCallbacks.size():0;
        if (count>0){
            mCallbacks.clear();
            return true;
        }
        return false;
    }

}
