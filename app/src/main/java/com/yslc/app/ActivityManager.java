package com.yslc.app;

import java.util.Stack;

import android.app.Activity;

/**
 * Activity管理类（使用单例模式）
 * <p> 类型与Android系统内部Activity管理 </p>
 * <p> 实现一次性关闭所有Activity </p>
 * <p> 关闭指定的Activity </p>
 *
 * @author HH
 */
public class ActivityManager {
    private static ActivityManager am = null;
    private static Stack<Activity> mStack = null;

    private ActivityManager() {
    }

    /**
     * 获取单例对象
     */
    public static ActivityManager getInstence() {
        if (null == am) {
            am = new ActivityManager();
            mStack = new Stack<>();
        }

        return am;
    }

    /**
     * 添加Activity
     */
    public void pushActivity(Activity activity) {
        mStack.add(activity);
    }

    /**
     * 移除某个Activity
     */
    public void finishActivity(Activity activity) {
        if (null != activity) {
            mStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void killActivity(Class<?> cls) {
        for (Activity activity : mStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

}
