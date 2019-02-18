package com.merlin.data;


import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MapIterator {

    public interface Callback{
        int CONTINUE=-999999;
        int BREAK=-9999991;
        Object onIterate(Object key, Object value, boolean hasNext);
    }

    public final <K,V> Object iterate(Map<K,V> data,Callback callback){
        Set<K> modules=null!=data?data.keySet():null;
        Iterator<K> iterator=null!=modules?modules.iterator():null;
        K key;
        if (null!=iterator&&iterator.hasNext()){
            Object returnObj;
            boolean continueNext;
            do {
                if (null != (key=iterator.next())&&null!=callback) {
                    continueNext=true;
                    if (null!=(returnObj=callback.onIterate(key,data.get(key),iterator.hasNext()))){
                        continueNext=false;
                         if (returnObj instanceof Integer){
                              switch ((Integer)returnObj){
                                  case Callback.BREAK:
                                      returnObj=null;//Clean
                                      break;
                              }
                         }
                    }
                    if (!continueNext){
                        return returnObj;
                    }
                }
            }while (iterator.hasNext());
        }
        return null;
    }
}
