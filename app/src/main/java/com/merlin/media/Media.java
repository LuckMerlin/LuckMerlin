package com.merlin.media;
/**
 * 2011-2019, LuckMerlin
 * Author: LuckMerlin
 * Date: 2019/3/2 13:47
 * Description:
 */
public class Media {
    private final String mPath;
    private int mPosition;

    public Media(String path){
        mPath=path;
    }

    public String getPath() {
        return mPath;
    }

    public void setPosition(int position){
        mPosition=position;
    }

    public final int getPosition(){
        return mPosition;
    }

    @Override
    public String toString() {
        return " "+mPosition+" "+(null!=mPath?mPath:"");
    }
}
