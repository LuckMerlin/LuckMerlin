package com.merlin.schedule;

public interface Interval {
    Long onResolveInterval(long curr, Long max);
}
