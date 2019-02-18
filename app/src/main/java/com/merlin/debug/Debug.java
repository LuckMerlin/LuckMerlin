package com.merlin.debug;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

public class Debug {
    private static WeakReference<Filter> mFilter;
    private static String mTag;
    private static boolean mPrintClass = false;
    private static final String DEFAULT_TAG = "XB";

    public Debug() {
    }

    public static void D(Class<?> cls, String msg) {
        D(cls, mTag, msg);
    }

    public static void D(Class<?> cls, String tag, String msg) {
        tag = null == tag ? mTag : tag;
        if (isNeedPrintDebug(3, cls, tag, msg)) {
            Log.d(null == tag ? "Xiao" : tag, formatMessage(cls, msg));
        }

    }

    public static void I(Class<?> cls, String msg) {
        I(cls, mTag, msg);
    }

    public static void I(Class<?> cls, String tag, String msg) {
        tag = null == tag ? mTag : tag;
        if (isNeedPrintDebug(4, cls, tag, msg)) {
            Log.i(null == tag ? "Xiao" : tag, formatMessage(cls, msg));
        }

    }

    public static void W(Class<?> cls, String msg) {
        W(cls, mTag, msg);
    }

    public static void W(Class<?> cls, String tag, String msg) {
        tag = null == tag ? mTag : tag;
        if (isNeedPrintDebug(5, cls, tag, msg)) {
            Log.w(null == tag ? "Xiao" : tag, formatMessage(cls, msg));
        }

    }

    public static void E(Class<?> cls, String msg) {
        E(cls, mTag, msg, (Exception)null);
    }

    public static void E(Class<?> cls, String msg, Exception e) {
        E(cls, mTag, msg, e);
    }

    public static void E(Class<?> cls, String tag, String msg, Exception e) {
        tag = null == tag ? mTag : tag;
        if (isNeedPrintDebug(6, cls, tag, msg)) {
            Log.e(null == tag ? "Xiao" : tag, formatMessage(cls, msg), e);
        }

    }

    private static String formatMessage(Class<?> cls, String msg) {
        return (null == msg ? "" : msg) + (mPrintClass ? (null == cls ? "" : cls) : "");
    }

    public static boolean printClassEnable(boolean enable) {
        if (mPrintClass != enable) {
            mPrintClass = enable;
            return true;
        } else {
            return false;
        }
    }

    public static void setFilter(Filter filter) {
        if (null != mFilter) {
            mFilter.clear();
            mFilter = null;
        }

        if (null != filter) {
            mFilter = new WeakReference(filter);
        }

    }

    private static boolean isNeedPrintDebug(int type, Class<?> cls, String tag, String msg) {
        Filter filter = null != mFilter ? (Filter)mFilter.get() : null;
        return null == filter || !filter.onResolveDebugFilte(type, cls, tag, msg);
    }

    public interface Filter {
        boolean onResolveDebugFilte(int var1, Class<?> var2, String var3, String var4);
    }

    public static class DebugClassFilter implements Filter {
        private boolean mExcept;
        private List<Class<?>> mClasses;

        @Override
        public boolean onResolveDebugFilte(int var1, Class<?> var2, String var3, String var4) {
            return false;
        }
    }
}
