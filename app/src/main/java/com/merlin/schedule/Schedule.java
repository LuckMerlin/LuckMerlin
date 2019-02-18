package com.merlin.schedule;

public abstract class Schedule implements Runnable{
    private final Long  mTimeoutCount;
    private final Interval mInterval;
    private int mScheduleCount;

    public Schedule(Long timeoutCount,Interval interval){
        mTimeoutCount=timeoutCount;
        mInterval=interval;
    }

    public Long getTimeoutCount() {
        return mTimeoutCount;
    }

    boolean reset(){
        if (mScheduleCount!=0){
            mScheduleCount=0;
            return true;
        }
        return false;
    }

    final Long onResolveInterval(){
        if (null!=mTimeoutCount&&mScheduleCount>=mTimeoutCount){
            return null;
        }
        Long interval=null!=mInterval?mInterval.onResolveInterval(mScheduleCount,mTimeoutCount):5000;
        mScheduleCount++;
        return interval;
    }

}
