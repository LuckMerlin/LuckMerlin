package com.merlin.schedule;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private final Handler mHandler=new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (null!=msg){
                Object obj=msg.obj;
                switch (msg.what){
                    case SCHEDULE_WHAT:
                        if (null!=obj&&obj instanceof Schedule){
                            schedule((Schedule)obj);
                        }
                        break;
                }
            }
            return true;
        }
    });

    private final List<Schedule> mScheduling=new ArrayList<>();
    private final static int SCHEDULE_WHAT=1000;

    public boolean reset(Schedule schedule,boolean removeSameType){
        if (null!=schedule) {
            if (removeSameType) {
                remove(schedule.getClass());
            }
            schedule.reset();//Reset
            return schedule(schedule);
        }
        return false;
    }

    public List<Schedule> remove(Class<? extends Schedule> cls){
        List<Schedule> schedules= null!=cls&&null!=mScheduling?mScheduling:null;
        int count=null!=schedules?schedules.size():0;
        Schedule schedule;
        List<Schedule> removed=new ArrayList<>();
        for (int i = 0; i < count; i++) {
             if (null!=(schedule=schedules.get(i))&&schedule.getClass().equals(cls)){
                 if (remove(schedule)){
                     removed.add(schedule);
                 }
             }
        }
        return removed;
    }

    public boolean remove(Schedule schedule){
        if (null!=schedule&&null!=mHandler) {
            mHandler.removeCallbacks(schedule);
            return null!=mScheduling&&mScheduling.remove(schedule);
        }
        return false;
    }

    public boolean exist(Class<? extends Schedule> cls){
        int size=null!=cls&&null!=mScheduling?mScheduling.size():0;
        Schedule schedule;
        for (int i = 0; i < size; i++) {
            if (null!=(schedule=mScheduling.get(i))&&schedule.getClass().equals(cls)){
               return true;
            }
        }
        return false;
    }

    private boolean schedule(Schedule schedule){
        if (null!=schedule) {
            Long interval = schedule.onResolveInterval();
            mHandler.removeMessages(SCHEDULE_WHAT,schedule);
            mScheduling.remove(schedule);
            if (null != interval) {
                mScheduling.add(schedule);
                long delay=interval<=0?0:interval;
                mScheduling.add(schedule);
                mHandler.sendMessageDelayed(mHandler.obtainMessage(SCHEDULE_WHAT,schedule),delay);
                return true;
            }
        }
        return false;
    }
}
