package com.merlin.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationFinder {

    public interface OnAnnotationIterate{
        boolean onAnnotationIterated(Class<?> cls, Object obj, Annotation annotation);
    }

    public interface OnAnnotationFound{
        boolean onAnnotationFound(Class<?> cls, Object obj, Annotation annotation);
    }

    public void find(Class<?> cls,final Class<?> annotationCls,final OnAnnotationFound found,ElementType ...types) {
        find(cls,annotationCls,null,found,types);
    }

    public void find(Class<?> cls,final Class<?> annotationCls,Class<?> topCls,final OnAnnotationFound found,ElementType ...types) {
           find(cls,annotationCls,topCls,false,found,types);
    }

   public void find(Class<?> cls,final Class<?> annotationCls,Class<?> topCls,boolean iterate,final OnAnnotationFound found,ElementType ...types){
       if (null!=cls&&null!=annotationCls&&null!=found){
            final OnAnnotationIterate callback=new OnAnnotationIterate() {
                @Override
                public boolean onAnnotationIterated(Class<?> cls, Object obj, Annotation annotation) {
                    Class type=null!=annotation?annotation.annotationType():null;
                     if (null!=type&&annotationCls.equals(type)){
                         return found.onAnnotationFound(cls,obj,annotation);
                     }
                    return false;
                }
            };
            Class<?> superCls=!iterate(cls,callback,types)&&iterate?cls.getSuperclass():null;
            if(null!=superCls&&(null==topCls||!topCls.equals(superCls))){
                 find(superCls,annotationCls,topCls,iterate,found,types);
            }
       }
    }

    private boolean iterate(Class<?> cls,OnAnnotationIterate callback,ElementType ...types){
        if (null!=callback&&null!=cls){
            //iterate fields's annotations
            Field[] fields=(null==types||exist(ElementType.FIELD,types))?cls.getDeclaredFields():null;
            int count=null!=fields?fields.length:0;
            Field field;
            for (int i = 0; i < count; i++) {
                if (null!=(field=fields[i])){
                    if (iterateArray(cls,field,field.getDeclaredAnnotations(),callback)){
                        return true;//Found it,Now return for interrupt later codes
                    }

                }
            }
            //iterate methods's annotations
            Method[] methods=(null==types||exist(ElementType.METHOD,types))?cls.getDeclaredMethods():null;
            count=null!=methods?methods.length:0;
            Method method;
            for (int i = 0; i < count; i++) {
                if (null!=(method=methods[i])){
                    if (iterateArray(cls,method,method.getDeclaredAnnotations(),callback)){
                        return true;//Found it,Now return for interrupt later codes
                    }

                }
            }
            //iterate constructors's annotations
            Constructor[] constructors=(null==types||exist(ElementType.CONSTRUCTOR,types))?cls.getDeclaredConstructors():null;
            count=null!=constructors?constructors.length:0;
            Constructor constructor;
            for (int i = 0; i < count; i++) {
                if (null!=(constructor=constructors[i])){
                    if (iterateArray(cls,constructor,constructor.getDeclaredAnnotations(),callback)){
                        return true;//Found it,Now return for interrupt later codes
                    }
                }
            }
        }
        return false;
    }

    private boolean iterateArray(Class<?> cls,Object obj,Annotation[] annotations,OnAnnotationIterate callback){
        if (null != callback) {
            int length=null!=annotations?annotations.length:0;
            Annotation annotation;
            for (int j = 0; j < length ; j++) {
                if (null!=(annotation=annotations[j])&&
                        callback.onAnnotationIterated(cls,obj,annotation)){
                    return true;//Found it,Now return for interrupt later codes
                }
            }
        }
        return false;
    }

    private boolean exist(ElementType type,ElementType[] types){
      if (null!=type&&null!=types&&types.length>0){
          for (ElementType f:types) {
              if (null!=f&&f.equals(type)){
                  return true;
              }
          }
      }
      return false;
    }


}
