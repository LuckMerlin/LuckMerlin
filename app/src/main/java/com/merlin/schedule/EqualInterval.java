package com.merlin.schedule;

public class EqualInterval implements Interval {
    private final long mDuration;
    public EqualInterval(long duration){
        mDuration=duration;
    }

    @Override
    public Long onResolveInterval(long curr, Long max) {
        return mDuration;
    }
}
