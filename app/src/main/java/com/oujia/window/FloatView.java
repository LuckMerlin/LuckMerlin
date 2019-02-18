package com.oujia.window;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.merlin.debug.Debug;

import java.util.ArrayList;
import java.util.List;

public class FloatView {
    private final static List<View> mViews=new ArrayList<>();

    public final boolean show(Context context, View view,WindowManager.LayoutParams params){
        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        return show(context,view,flags,params);
    }

    public final boolean show(Context context, View view,int flags, WindowManager.LayoutParams params){
        if (null==context||null==view){
            Debug.W(getClass(),"Can't show float view.context="+context+" view="+view);
           return false;
        }
        if (null!=view.getParent()){
            Debug.W(getClass(),"Can't show float view for view exist parent.view="+view);
            return false;
        }
        WindowManager manager = null!=context?(WindowManager) context.getSystemService(Context.WINDOW_SERVICE):null;
        if (null==params){
            params = new WindowManager.LayoutParams();
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG;//TYPE_SYSTEM_ALERT;
            // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            // 设置flag
//            int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
            params.flags = flags;
            // 不设置这个弹出框的透明遮罩显示为黑色
            params.format = PixelFormat.TRANSLUCENT;
            // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
            // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
            // 不设置这个flag的话，home页的划屏会有问题
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.gravity = Gravity.CENTER;
        }
        mViews.add(view);
        manager.addView(view,params);
      return true;
    }

    public final boolean remove(Context context, View view){
        WindowManager manager =null!=view&& null!=context?(WindowManager) context.getSystemService(Context.WINDOW_SERVICE):null;
        if (null!=manager&&null!=mViews&&mViews.contains(view)){
            manager.removeView(view);
            mViews.remove(view);
            return true;
        }
        return false;
    }
}
