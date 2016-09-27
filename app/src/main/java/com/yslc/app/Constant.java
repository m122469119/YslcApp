package com.yslc.app;

/**
 * 应用程序常量配置
 *
 * @author 黄辉
 */
public class Constant {

    /**
     * .....................文件和文件夹配置.........................*
     */
    private static final String FILES_FRIST = "/yslc";  //一级文件夹
    public static final String FILES_USER = FILES_FRIST + "/userImg";  //用户
    public static final String FILES_USERIMG = FILES_FRIST + "/userImg/userImg.jpg";  //用户头像存储地址
    public static final String FILES_TEMPIMG = FILES_FRIST + "/userImg/my.jpg";  //用户拍照图片暂存地址
    public static final String FILES_EXCEPTION_LOG = FILES_FRIST + "/exception/exception.txt"; //异常Log文件存储地址
    public static final String FILES_LOGO = FILES_FRIST + "/logo/logo.png"; //Logo图标文件存储地址


    /**
     * .................系统设置SharePerferences配置..................*
     */
    public static final String SYSTEM_NAME = "systemSet"; //文件名称
    public static final String SYSTEM_ISFRIST_KEY = "isFrist"; //是否第一次开启KEY
    public static final String LAST_UPDATE_TIME_KEY = "lastUpdateTime"; // 股票代码数据导入时间SharedPreferences Key

    /**
     * .................股票行情横竖屏切换SharePerferences配置..................*
     */
    public static final String CACHE_STOCK_DATA_NAME = "cacheStock";  //横竖屏切换缓存股票数据
    public static final String CACHE_STOCK_MIN_KEY = "cacheStockMinKey";  //分时键值
    public static final String CACHE_STOCK_K_KEY = "cacheStockKKey";  //K线键值

    /**
     * .................用户信息SharePerferences配置..................*
     */
    public static final String SPF_USER_INFO_NAME = "user";  //文件名称
    public static final String SPF_USER_ID_KEY = "userId";   //用户ID
    public static final String SPF_USER_ISLOGIN_KEY = "isLogin";  //是否登录
    public static final String SPF_USER_PHONE_KEY = "userPhone";  //用户电话(有户名）
    public static final String SPF_USER_IMGURL_KEY = "userImg";   //用户电话
    //极光推送
    public static final String SPF_USER_JPUSH_TAG = "jpush_tag"; //极光推送标签
    public static final String SPF_USER_JPUSH_ALIAS = "jpush_alias"; //极光推送别名

    /**
     * .................设置SharePerferences配置..................*
     */
    public static final String SPF_SET_INFO_NAME = "seting";  //文件名称
    public static final String SPF_IS_PUSH_KEY = "isPush";   //用户ID
    public static final String SPF_IS_WIFI_KEY = "isWifi";  //是否登录

    /**
     * ............................其他常量..........................*
     */
    public static final String PACKGER_NAME = "com.tzkb";
    public static final String PAEASE_LOGIN = "请先登录";
    public static final String LOGIN_SUCCESS = "登录成功";
    public static final String LOGIN_FAILER = "登录失败";
    public static final String REGISTER_SUCCESS = "注册成功";
    public static final String REGISTER_FAILER = "注册失败";
    public static final String UPLOAD_WAIT = "正在上传，请稍等...";

}
