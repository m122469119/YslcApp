package com.yslc.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.yslc.app.Constant;

/**
 * SharedPreferences工具类，方便存取得各种类型的数据
 * <p>注意有关登录和获取用户id的两个方法不是本类的默认sharedPreferences</p>
 * @author HH
 */
public class SharedPreferencesUtil {

    public static final String NAME_PAY_ACTIVITY = "jume_activity";//share名字
    public static final String KEY_ACTIVITY = "current_activity";//key
    public static final String FAST_INFO = "fastInfoActivity";//参数1
    public static final String INVEST_PAPER = "InvestPaperActivity";//参数2
    public static final String NONE = "noneActivity";//参数3

    private SharedPreferences config;

    /**
     * sharedPreference工具类
     * @param context 上下文
     * @param name sharePreferences的名字
     */
    public SharedPreferencesUtil(Context context, String name) {
        config = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * 放boolean数据进sharePreference
     * @param key 数据key
     * @param flag 数据
     */
    public void setBoolean(String key, Boolean flag) {
        config.edit().putBoolean(key, flag).commit();
    }

    /**
     * 获取Boolean数据
     * @param key 数据名字
     * @return 数据
     */
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

    /**
     * 放long数据进sharePreference
     * @param key 数据名字
     * @param flag 数据
     */
    public void setLong(String key, long flag) {
        config.edit().putLong(key, flag).commit();
    }

    /**
     * 获取long数据
     * @param key 数据名字
     * @return 数据
     */
    public long getLong(String key) {
        return config.getLong(key, 0);
    }

    /**
     * 放String数据进sharePreference
     * @param key 数据名字
     * @param flag 数据
     */
    public void setString(String key, String flag) {
        config.edit().putString(key, flag).commit();
    }

    /**
     * 获取String数据
     * @param key 数据名字
     * @return 数据
     */
    public String getString(String key) {
        return config.getString(key, "");
    }

    /**
     * 用户是否登录
     * <p>查询登录信息的sharedPreferences,获取是否登录信息</p>
     * @return 是否登录
     */
    public static boolean isLogin(Context context) {
        return context.getSharedPreferences(Constant.SPF_USER_INFO_NAME, Context.MODE_PRIVATE)
                .getBoolean(Constant.SPF_USER_ISLOGIN_KEY, false);
    }

    /**
     * 用户是否登录,带信息提示
     * @param info 使用Toast显示登录信息
     * @return 是否登录
     * @see #isLogin(Context)
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
     * <p>查询登录信息的sharedPreferences,获取登录的id</p>
     * @return 用户id
     */
    public static String getUserId(Context context) {
        return context.getSharedPreferences(Constant.SPF_USER_INFO_NAME, Context.MODE_PRIVATE)
                .getString(Constant.SPF_USER_ID_KEY, "");
    }

    /**
     * 清除某个Key
     * <p>删掉sharedPreferences的数据</p>
     * @param key 数据名字
     */
    public void deleteKey(String key) {
        config.edit().remove(key).commit();
    }

    /**
     * 清除全部KEY
     * <p>清除所有sharedPreference的数据</p>
     */
    public void clearAll() {
        config.edit().clear().commit();
    }
}
