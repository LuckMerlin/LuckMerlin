/**
 * FileName: Dumper
 * Author: LuckMerlin <a href="http://www.luckmerlin.com">LuckMerlin</a>
 * CreateDate: 2019\1\14 0014 11:09
 */
package com.merlin.data;

import android.os.Bundle;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C), 2018-2019, 偶家科技 <a href="http://www.oplushome.com">O+</a>
 * TODO 
 * Version: 0.0
 * History:
 */
public class Dumper {

    public String dump(Object obj){
      final StringBuffer buffer=new StringBuffer();
      if (null==obj){
          final StackTraceElement[] elements=new Throwable().getStackTrace();
          final int count=null!=elements?elements.length:0;
          if (count>0) {
              StackTraceElement element;
              String clsName;
              for (int i = count-1; i >= 0; i--) {
                  if (null != (element = elements[i]) && null != (clsName = element.getClassName())) {
                      buffer.append(""+i+"="+i);
                      buffer.append(clsName);
                  }
              }
          }
      }else  if (null!=obj){
          if (obj instanceof List){
              List list=(List)obj;
              final int size=null!=list?list.size():0;
              Object child;
              for (int i = 0; i <size ; i++) {
                  if (null!=(child=list.get(i))){
                      buffer.append(i);
                      buffer.append("=");
                      buffer.append(null!=child?child:"null");
                      buffer.append("\n");
                  }
              }
          }else if (obj instanceof Bundle){
              Bundle bundle=(Bundle)obj;
              Set<String> set=null!=bundle?bundle.keySet():null;
              Iterator<String> iterator=null!=set?set.iterator():null;
              if (null!=iterator&&iterator.hasNext()){
                  String child;
                  do{
                      if (null!=(child=iterator.next())){
                          Object value=bundle.get(child);
                          buffer.append(child+"="+null!=value?value:"null");
                      }
                  }while (iterator.hasNext());
              }
          }else if (obj instanceof Map){
              Map map=(Map)obj;
              Set set=null!=map?map.keySet():null;
              Iterator iterator=null!=set?set.iterator():null;
              if (null!=iterator&&iterator.hasNext()){
                  Object child;
                  do{
                      if (null!=(child=iterator.next())){
                          Object value=map.get(child);
                          buffer.append(child+"="+null!=value?value:"null");
                      }
                  }while (iterator.hasNext());
              }
          }else if (obj.getClass().isArray()){
              int count=Array.getLength(obj);
              for (int i = 0; i < count; i++) {
                  buffer.append(" i="+i+" value="+Array.get(obj,i));
              }
          }
      }
        return buffer.toString();
    }

}