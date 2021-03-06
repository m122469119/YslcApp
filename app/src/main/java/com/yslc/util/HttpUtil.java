package com.yslc.util;

import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 网络请求工具类
 *
 * @author HH
 */
public class HttpUtil {
    public static final String ERROR_CODE = "-1";
    public static final String ERROR_INFO = "获取失败";
    public static final String NO_INTERNET_INFO = "请检查你的网络设置";
    private static final int TIME_OUT = 6000;
    private static AsyncHttpClient client;
    public static final String P = "FC4EED8F-32AD-4179-A3B9-2F5C0DA5536A";

    static {
        client = new AsyncHttpClient();
        // 设置网络请求超时时间
        client.setTimeout(TIME_OUT);
    }

    /**
     * ----------------IP配置------------------ *
     */
    private static final String IP = "http://app.etz927.com";
    /** -------------IP配置结束---------------- **/

    /**
     * ---------------所有接口配置-------------- *
     */
    public static final String GET_COLNUM = "/AppJson/Get_SmallType.ashx"; // 获取栏目
    public static final String GET_MAIN = "/AppJson/index/GetHomePage.ashx"; // 获取主页信息
    public static final String GET_MAIN_FAST = "/AppJson/news/GetLive.ashx"; // 获取主页快讯信息
    public static final String GET_STOCK = "/AppJson/stock/GetMarketPrice.ashx"; //获取大盘信息
    public static final String GET_VEDIO = "/AppJson/index/getvideoindex.ashx"; // 获取分类视频
    public static final String GET_COMMENT = "/AppJson/news/GetNews_Comment.ashx"; //获取评论列表
    public static final String POST_COMMENT = "/AppJson/news/AddNews_Comment.ashx";// 提交评论
    public static final String GET_NEW = IP + "/AppJson/news/news.aspx"; // 获取新闻详细页面（Web页面）

    public static final String POST_LOGIN = "/AppJson/Users_AppJson/Post_Users_Info_Login.ashx"; // 用户登录
    public static final String POST_REGISTER = "/AppJson/Users_AppJson/Post_Users_Info_Registration.ashx"; // 用户注册
    public static final String POST_FIND_PASSWORD = "/AppJson/Users_AppJson/GetUsers_Info_Password.ashx"; // 用户找回密码
    public static final String POST_UPDATE_PASSWORDS = "/AppJson/Users_AppJson/Post_Users_Info_Edit.ashx"; // 用户修改密码
    public static final String GET_CODE = "/AppJson/Users_AppJson/SendCheckNumber.ashx"; // 获取手机验证码
    public static final String UPLOAD_USER_IMAGE = "/AppJson/Users_AppJson/PostUsers_Info_Img.ashx"; // 上传用户头像地址

    public static final String GET_STAR_TYPE = "/AppJson/Star/Get_Star_Type.ashx"; //获取明星类型
    public static final String GET_STAR_LIST = "/AppJson/Star/Get_Star.ashx"; // 获取明星资料列表
    public static final String GET_STAR_CONTENT_LIST = "/AppJson/Star/Get_Star_News_List.ashx"; // 获取明星文章列表
    public static final String GET_STAR_COMTENT_COMMENT = "/AppJson/Star/GetStar_News_Comment_List.ashx"; // 获取明星文章评论
    public static final String GET_STAR_COMMENT_COMMINT = "/AppJson/Star/PostStar_News_Comment.ashx"; // 提交文章评论
    public static final String DO_PRAISE = "/AppJson/Star/AddSn_Praise.ashx"; // 文章点赞接口
    public static final String PLAY_VEDIO = "/AppJson/program/Get_Program.ashx"; //广播接口

    public static final String PLAY_VEDIO_RELIVE = "/AppJson/program/Get_ProgramList.ashx"; // 广播重温界面列表接口
    public static final String PLAY_VEDIO_RELIVE_DETAILS = "/AppJson/program/Get_ProgramDetails.ashx"; // 广播重温界面节目详细信息
    public static final String PLAY_VEDIO_RELIVE_DETAILS_LIST = "/AppJson/program/GetByDbName.ashx"; // 广播重温界面节目列表信息

    public static final String GET_STOCK_CODElIST = "/AppJson/stock/getBlock.ashx"; // 获取股市代码列表
    public static final String GET_STOCY_H_DATA = "/AppJson/stock/getMinuteData.ashx";  //获取分时数据
    public static final String GET_STOCY_K_DATA = "/AppJson/stock/getKLine.ashx";  //获取K线数据
    /** ---------------所有接口配置结束------------- **/

    /**
     * 发送post请求
     *
     * @param url             地址
     * @param context         内容
     * @param params          参数
     * @param responseHandler 回调
     */
    public static void post(String url, Context context, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        // 判断是否连接网络
        if (!CommonUtil.isNetworkAvalible(context)) {
            ToastUtil.showMessage(context, HttpUtil.NO_INTERNET_INFO);
            return;
        }

        client.post(getHostUrl(url), params, responseHandler);
    }

    /**
     * 发送get请求
     *
     * @param url             地址
     * @param context         内容
     * @param params          参数
     * @param responseHandler 回调
     */
    public static void get(String url, Context context, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        // 判断是否连接网络
        if (!CommonUtil.isNetworkAvalible(context)) {
            ToastUtil.showMessage(context, HttpUtil.NO_INTERNET_INFO);
            return;
        }

        client.get(getHostUrl(url), params, responseHandler);
    }

    /**
     * 关闭该Context所有网络请求
     */
    public static void closeHttp(Context context) {
        if (null != context) {
            client.cancelRequests(context, true);
        }
    }

    /**
     * 获取接口地址
     */
    private static String getHostUrl(String url) {
        return IP + url;
    }

}
