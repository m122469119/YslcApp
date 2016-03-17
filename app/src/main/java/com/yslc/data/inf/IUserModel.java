package com.yslc.data.inf;

import android.graphics.Bitmap;

import com.yslc.inf.GetDataCallback;

/**
 * 用户Mode接口层
 * <p>
 * Created by HH on 2016/2/25.
 */
public interface IUserModel {
    /**
     * 用户登录
     *
     * @param userName
     * @param userPass
     * @param callback
     */
    void userLogin(String userName, String userPass, GetDataCallback callback);

    /**
     * 用户注册
     *
     * @param userName
     * @param userPass
     * @param callback
     */
    void userRegister(String userName, String userPass, GetDataCallback callback);

    /**
     * 获取验证码
     *
     * @param type     验证码类型
     * @param phone
     * @param callback
     */
    void getValidationCode(String type, String phone, GetDataCallback callback);

    /**
     * 修改密码
     *
     * @param userId
     * @param oldUserPass
     * @param newUserPass
     * @param callback
     */
    void userUpdatePass(String userId, String oldUserPass, String newUserPass, GetDataCallback callback);

    /**
     * 找回密码后进行修改密码
     *
     * @param userName
     * @param userPass
     * @param callback
     */
    void userForgetPass(String userName, String userPass, GetDataCallback callback);

    /**
     * 上传用户头像
     *
     * @param bitmap
     * @param callback
     */
    void uploadUserImage(String userId, Bitmap bitmap, GetDataCallback callback);
}
