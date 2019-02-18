package com.merlin.data;

import java.util.ArrayList;
import java.util.List;

public class ListGroup<T>{
    private final List<T> mData=new ArrayList<T>();

    public int size(){
        return  null!=mData?mData.size():0;
    }

    public int index(T data){
        return  null!=mData&&null!=data?mData.indexOf(data):-1;
    }

    public boolean exist(T data){
        return null!=mData&&null!=data?mData.contains(data):false;
    }

    public T set(T data,int index){
        if (null!=data&&index>=0&&null!=mData){
            return mData.set(index,data);
        }
      return null;
    }

    public T get(int index){
        return index>=0&&null!=mData&&index<mData.size()?mData.get(index):null;
    }

    public T getFirst(){
        int size=null!=mData?mData.size():0;
        return size>0?mData.get(0):null;
    }

    public T getLast(){
        int size=null!=mData?mData.size():0;
        return size>0?mData.get(size-1):null;
    }

    public boolean addAll(List<T> data){
        if (null!=data&&data.size()>0&&null!=mData){
            return mData.addAll(data);
        }
        return false;
    }

    public boolean add(T data){
        return  null!=data&&null!=mData&&!mData.contains(data)?mData.add(data):false;
    }

    public boolean remove(T data){
        return  null!=data&&null!=mData&&mData.contains(data)?mData.remove(data):false;
    }

    public int clean(){
        int count=null!=mData?mData.size():-1;
        if (count>0){
            mData.clear();
        }
        return count;
    }

    public List<T> getData() {
        return mData;
    }

    public final void iterateList(Callback callback){
        if (null!=callback) {
            int count = null != mData ? mData.size() : 0;
            for (int i = 0; i < count; i++) {
                if (Callback.CONTINUE!=callback.onListIterate(i,count,mData.get(i))){
                      break;
                }
            }
        }
    }

    public interface Callback{
        int CONTINUE=1;
        int onListIterate(int index, int count, Object data);
    }
}
