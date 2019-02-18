package com.merlin.data;

import com.merlin.debug.Debug;

import java.util.ArrayList;
import java.util.List;

public abstract class AppendableArray<T> {
    private T[] mObjects;

    public AppendableArray(T ...objects){
        mObjects=objects;
    }

   protected abstract T[] onCreateNewInstance(int length);


    public final boolean append(T...callbacks){
        return append(false,callbacks);
    }

    public final boolean append(boolean keepSingle,T...callbacks){
        if (null!=callbacks&&callbacks.length>0){
            final T[] objects=mObjects;
            final int currLength=null!=objects?objects.length:0;
            if (currLength<=0){//If current is NULL,just assign to it
                mObjects=callbacks;
                return true;
            }else{
                int count=callbacks.length;
                final List<T> appendArray=new ArrayList<>();
                T callback;
                for (int i = 0; i < count; i++) {
                    if (null!=(callback=callbacks[i])&&(!keepSingle||!exist(callback))){
                        appendArray.add(callback);
                    }
                }
                int appendSize=appendArray.size();
                int totalLength=currLength+appendSize;
                if (appendSize>0&&totalLength>0){//If need append more
                   final T[] newArray= onCreateNewInstance(totalLength);
                   if (null==newArray||newArray.length!=totalLength){
                       Debug.W(getClass(),"Failed append value to array.new instance array not valid.length="+(null!=newArray?newArray.length:-1)+" validLength="+totalLength+" newArray="+newArray);
                       return false;
                   }
                   System.arraycopy(objects,0,newArray,0,currLength);//Copy old at firstly
                    final int apeendStartIndex=currLength+1;
                    for (int i = 0; i < totalLength; i++) {//Copy append list
                        newArray[apeendStartIndex+i]=appendArray.get(i);
                    }
                    mObjects=newArray;
                    return true;
                }
                return false;//Interrupt later codes
            }
        }
        return false;
    }

    public final  boolean exist(T obj){
        int count=null!=obj&&null!=mObjects?mObjects.length:0;
        T temp;
        for (int i = 0; i < count; i++) {
             if (null!=(temp=mObjects[i])&&(temp==obj||(obj instanceof Equalable&&temp.equals(obj)))){
                 return true;
            }
        }
        return false;
    }

    public final int clean(){
        int count=null!=mObjects?mObjects.length:0;
        mObjects=null;
        return count;
    }
    public final T get(int index){
        return null!=mObjects&&index>=0&&index<mObjects.length?mObjects[index]:null;
    }
    public final int size(){
        return null!=mObjects?mObjects.length:0;
    }


}
