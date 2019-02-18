package com.merlin.data;


import java.util.ArrayList;
import java.util.List;

public class ListRemover {

   public final int remove(List<?> list,RemoveResolver callback){
        if (null!=list&&list.size()>0&&null!=callback){
            final List<Object> needRemove=new ArrayList<>();
            Object obj;
            for (Object f:list){
               if (null!=f&&null!=(obj=callback.onResolveRemove(f))){
                   if (obj instanceof Integer){
                       if (((Integer)obj)== RemoveResolver.REMOVE){
                           needRemove.add(f);
                           continue;
                       }
                       if (((Integer)obj)== RemoveResolver.BREAK){
                           break;
                       }
                   }
               }
            }
            boolean succeed=list.removeAll(needRemove);
            return succeed&&null!=needRemove?needRemove.size():0;
        }
        return -1;
   }

   public interface RemoveResolver{
        int BREAK=1000;
        int REMOVE=1001;
        Object onResolveRemove(Object obj);
   }
}
