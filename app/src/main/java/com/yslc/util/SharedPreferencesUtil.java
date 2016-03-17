package com.yslc.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.yslc.app.Constant;

/**
 * SharedPreferences工具类，方便存取得各种类型的数据
 *
 * @author HH
 */
public class SharedPreferencesUtil {

    private SharedPreferences config;

    public SharedPreferencesUtil(Context context, String name) {
        config = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void setBoolean(String key, Boolean flag) {
        config.edit().putBoolean(key, flag).commit();
    }

    public boolean getBoolean(String key) {
        return config.getBoolean(key, false);
    }

    public void setFloat(String key, float flag) {
        config.edit().putFloat(key, flag).commit();
    }

    public float getFloat(String key) {
        return config.getFloat(key, 0f);
    }

    public void setInt(String key, int flag) {
        config.edit().putInt(key, flag).commit();
    }

    public int getInt(String key) {
        return config.getInt(key, 0);
    }

    public void setLong(String key, long flag) {
        config.edit().putLong(key, flag).commit();
    }

    public long getLong(String key) {
        return config.getLong(key, 0);
    }

    public void setString(String key, String flag) {
        config.edit().putString(key, flag).commit();
    }

    public String getString(String key) {
        return config.getString(key, "");
    }

    /**
     * 用户是否登录
     */
    public static boolean isLogin(Context context) {
        return context.getSharedPreferences(Constant.SPF_USER_INFO_NAME, Context.MODE_PRIVATE)
                .getBoolean(Constant.SPF_USER_ISLOGIN_KEY, false);
    }

    /**
     * 用户是否登录,带信息提示
     */
    public static boolean isLogin(Context context, String info) {
        boolean isLogin = isLogin(context);
        if (!isLogin) {
            ToastUtil.showMessage(context, info);
        }
        return isLogin;
    }

    /**
     * 获取用户id
     */
    public static String getUserId(Context context) {
        return context.getSharedPreferences(Constant.SPF_USER_INFO_NAME, Context.MODE_PRIVATE)
                .getString(Constant.SPF_USER_ID_KEY, "");
    }

    /**
     * 清除某个Key
     *
     * @param key
     */
    public void deleteKey(String key) {
        config.edit().remove(key).commit();
    }

    /**
     * 清除全部KEY
     */
    public void clearAll() {
        config.edit().clear().commit();
    }
}
