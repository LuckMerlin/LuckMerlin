package com.merlin.classes;


public class Cast {

    public interface Finder{
        int INTERFACE=100;
        int CLASS=101;
        int ALL=102;
        boolean onClassFound(int type, Class<?> cls);
    }

    public final boolean exist(Class<?> src,Class<?> target){
        return null!=src&&null!=target&&null!=castClass(src,target);
    }

    public final <T> T cast(Object src, final Class<? extends T> target){
           if (null!=src&&null!=target&&!(src instanceof Class)&&null!=castClass(src.getClass(),target)){
                return (T)src;
           }
           return null;
    }

    public final <T> Class<? extends T> castClass(Class<?> src, final Class<? extends T> target){
        return null!=src&&null!=target?(Class<? extends T>) find(src,Finder.ALL, new Finder() {
            @Override
            public boolean onClassFound(int type,Class<?> cls) {
                return null!=cls&&cls.equals(target);
            }
        }):null;
    }

    public final  Class<?> find(final Class<?> src,int type,Finder finder){
        return null!=finder&&null!=src?iterateFound(src,finder):null;
    }

    private Class<?> iterateFound(Class<?> src,Finder finder){
        if (null!=finder&&null!=src) {
            //Iterate self
            if (finder.onClassFound(src.isInterface()?Finder.INTERFACE:Finder.CLASS,src)) {
                return src;
            }
            //Iterate interfaces
            Class<?>[] interfaces=src.getInterfaces();
            int cont=null!=interfaces?interfaces.length:0;
            Class<?> interCls,ok;
            for (int i = 0; i < cont; i++) {
                if (null!=(interCls=interfaces[i])&&null!=(ok=iterateFound(interCls,finder))){
                     return interCls;
                }
            }
            //Iterate supers
            Class<?> superCls=src.getSuperclass();
            if (null!=(ok=iterateFound(superCls,finder))){
                 return superCls;
            }
        }
        return null;
    }
}
