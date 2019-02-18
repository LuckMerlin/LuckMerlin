package com.merlin.hook;

import android.content.Context;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ActivityHook {

    public interface AmsInvocationHandler{
        public Object invoke(Object ams,Object proxy, Method method, Object[] args) throws Throwable;
    }

    public void hookAms(Context context, final AmsInvocationHandler handler) {
        try {
        // 第一步， API 26 以后，hook android.app.ActivityManager.IActivityManagerSingleton，
        // API 25 以前，hook android.app.ActivityManagerNative.gDefault
        Field gDefaultField = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Class<?> activityManager = null;
                activityManager = Class.forName("android.app.ActivityManager");
            gDefaultField = activityManager.getDeclaredField("IActivityManagerSingleton");
        } else {
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
        }
        gDefaultField.setAccessible(true);
        Object gDefaultObj = gDefaultField.get(null);
        //所有静态对象的反射可以通过传null获取。如果是实列必须传实例
        Class<?> singletonClazz = Class.forName("android.util.Singleton");
        final Field amsField = singletonClazz.getDeclaredField("mInstance");
        amsField.setAccessible(true);
        final Object amsObj = amsField.get(gDefaultObj);
        // 第二步，获取我们的代理对象，这里因为是接口，所以我们使用动态代理的方式
        final Object ams = Proxy.newProxyInstance(context.getClass().getClassLoader(),amsObj.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return null!=handler?handler.invoke(amsObj,proxy,method,args):null;
                    }
                });
        // 第三步：设置为我们的代理对象
        amsField.set(gDefaultObj, ams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
