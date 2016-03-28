package com.yslc.data.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.EditText;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.yslc.app.Constant;
import com.yslc.bean.UserBean;
import com.yslc.data.impl.UserModelImpl;
import com.yslc.data.inf.IUserModel;
import com.yslc.inf.GetDataCallback;
import com.yslc.util.CommonUtil;
import com.yslc.util.FileUtil;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;

/**
 * 用户模块实现逻辑
 * <p>
 * Created by HH on 2016/2/25.
 */
public class UserModelService {
    private Context context;
    private IUserModel userData;

    public UserModelService(Context context) {
        this.context = context;
        userData = new UserModelImpl(context);
    }

    /**
     * 用户登录验证
     */
    public boolean userLoginValidation(EditText inputUser, EditText inputPass) {
        if (!CommonUtil.checkMobile(CommonUtil.inputFilter(inputUser))) {
            ToastUtil.showMessage(context, "请输入有效的手机号");
            return false;
        } else if (CommonUtil.inputFilter(inputPass).length() < 8) {
            ToastUtil.showMessage(context, "请输入8-15位有效的密码");
            return false;
        }

        return true;
    }

    /**
     * 用户登录请求
     *
     * @param userName
     * @param userPass
     * @param callback
     */
    public void userLogin(String userName, String userPass, GetDataCallback callback) {
        userData.userLogin(userName, userPass, new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                UserBean user = (UserBean) data;
                user.setUserPhone(userName);
                //保存用户信息
                saveUserInfo((UserBean) data);
                callback.success(null);
            }

            @Override
            public <T> void failer(T data) {
                callback.failer(data.toString());
            }
        });
    }

    /**
     * 登录成功后...
     * 保存用户基本信息
     * @param user 用户信息类
     */
    public void saveUserInfo(UserBean user) {
        ToastUtil.showMessage(context, Constant.LOGIN_SUCCESS);

        // 保存用户Id,用户账号，用户头像,设置登录成功
        SharedPreferencesUtil share = new SharedPreferencesUtil(context, Constant.SPF_USER_INFO_NAME);
        share.setString(Constant.SPF_USER_ID_KEY, user.getUserId());
        share.setString(Constant.SPF_USER_PHONE_KEY, user.getUserPhone());
        share.setString(Constant.SPF_USER_IMGURL_KEY, user.getUserImageUrl());
        share.setBoolean(Constant.SPF_USER_ISLOGIN_KEY, true);

    }

    /**
     * 用户注册请求
     *
     * @param userName 用户名（即手机号）
     * @param userPass 加密密码
     * @param callback 回调
     */
    public void userRegister(String userName, String userPass, GetDataCallback callback) {
        userData.userRegister(userName, userPass, new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                //注册成功，保存用户基本数据
                UserBean user = (UserBean) data;
                user.setUserPhone(userName);
                saveUserInfo(user);//保存用户信息
                callback.success(data);
            }

            @Override
            public <T> void failer(T data) {
                callback.failer(data);
            }
        });
    }

    /**
     * 两次密码输入是否一致
     */
    public boolean passwordIsTrue(EditText inputPass1, EditText inputPass2) {
        if (CommonUtil.inputFilter(inputPass1).length() < 8) {
            ToastUtil.showMessage(context, "请输入8-15位有效的密码");
            return false;
        }

        if (!CommonUtil.inputFilter(inputPass1).equals(CommonUtil.inputFilter(inputPass2))) {
            ToastUtil.showMessage(context, "密码输入不一致");
            inputPass2.setText("");
            return false;

        }

        return true;
    }

    /**
     * 获取验证码
     *
     * @param type 请求类型 1 代表找回密码验证码 0代表注册验证码
     * @param phone 电话号码
     * @param callback 回调
     */
    public void getValidationCode(String type, String phone, GetDataCallback callback) {
        userData.getValidationCode(type, phone, callback);
    }

    /**
     * 验证码是否正确
     */
    public boolean isValidationCode(String validationCode, String inputCode) {
        if (!validationCode.equals("-1")
                && validationCode.equals(inputCode)) {
            return true;
        }

        ToastUtil.showMessage(context, "验证码错误");
        return false;
    }

    /**
     * 修改用户密码
     *
     * @param userId 用户名
     * @param oldUserPass 旧密码
     * @param newUserPass 新密码
     * @param callback 回调
     */
    public void userUpdatePass(String userId, String oldUserPass, String newUserPass, GetDataCallback callback) {
        userData.userUpdatePass(userId, oldUserPass, newUserPass, callback);
    }

    /**
     * 忘记密码后修改密码
     *
     * @param userName 用户名即手机号
     * @param userPass 加密密码
     * @param callback 回调
     */
    public void userForgetPass(String userName, String userPass, GetDataCallback callback) {
        userData.userForgetPass(userName, userPass, callback);
    }

    /**
     * 上传用户头像
     *
     * @param userId 用户id
     * @param bitmap 用户头像
     * @param callback
     */
    public void uploadUserImage(String userId, Bitmap bitmap, GetDataCallback callback) {
        userData.uploadUserImage(userId, bitmap, new GetDataCallback() {
            @Override
            public <T> void success(T data) {//data为服务器返回图片地址
                //保存用户头像
                SharedPreferencesUtil share = new SharedPreferencesUtil(context, Constant.SPF_USER_INFO_NAME);
                share.setString(Constant.SPF_USER_IMGURL_KEY, data.toString());
                //用户头像信息发生改变，保存到sd卡
                FileUtil.creatMoreFiles(Constant.FILES_USERIMG);
                FileUtil.saveFile(bitmap, Constant.FILES_USERIMG);
                callback.success(null);
            }

            @Override
            public <T> void failer(T data) {
                callback.failer(data);
            }
        });
    }

    /**
     * 清除头像缓存
     */
    public void clearUserImgCache(ImageLoader imageLoader) {
        SharedPreferencesUtil spfUtil = new SharedPreferencesUtil(context, Constant.SPF_USER_INFO_NAME);
        DiskCacheUtils.removeFromCache(spfUtil.getString(Constant.SPF_USER_IMGURL_KEY), imageLoader.getDiskCache());
        MemoryCacheUtils.removeFromCache(spfUtil.getString(Constant.SPF_USER_IMGURL_KEY), imageLoader.getMemoryCache());
        DiskCacheUtils.removeFromCache("file://" + FileUtil.getSdCardPath() + Constant.FILES_USERIMG, imageLoader.getDiskCache());
        MemoryCacheUtils.removeFromCache("file://" + FileUtil.getSdCardPath() + Constant.FILES_USERIMG, imageLoader.getMemoryCache());
    }
}
