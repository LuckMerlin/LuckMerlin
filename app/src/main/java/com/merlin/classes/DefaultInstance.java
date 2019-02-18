package com.merlin.classes;

import com.merlin.debug.Debug;

import java.lang.reflect.Constructor;

public class DefaultInstance {

    public final <T> T createDefault(Class<? extends T> cls){
        Constructor<?>[] constructors=null!=cls?cls.getDeclaredConstructors():null;
        int count=null!=constructors?constructors.length:0;
        Constructor constructor;
        Object object;
        for (int i = 0; i < count; i++) {
            if (null!=(constructor=constructors[i])&&null!=(object=createDefault(constructor))){
                return (T)object;
            }
        }
        return null;
    }

    private Object createDefault(Constructor constructor){
        if (null!=constructor){
            boolean access=constructor.isAccessible();
            constructor.setAccessible(true);
            Class<?>[] types= null!=constructor?constructor.getParameterTypes():null;
            try {
                Object[] args=null;
                int count=null!=types?types.length:0;
                if (count>0){
                    final DefaultValue classes=new DefaultValue();
                    Class<?> cls;
                    args=new Object[count];
                    for (int i = 0; i < count; i++) {
                        if (null!=(cls=types[i])){
                            args[i]=classes.javaDefaultValue(cls);
                        }
                    }
                }
                return null!=constructor?constructor.newInstance(args):null;
            } catch (Exception e) {
                Debug.E(getClass(),"Can't create default .e="+e+" constructor="+constructor,e);
                e.printStackTrace();
            }
            constructor.setAccessible(access);
        }
        return null;
    }
}
