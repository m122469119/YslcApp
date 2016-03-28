package com.yslc.data.impl;

import android.content.Context;
import android.graphics.Bitmap;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yslc.app.Constant;
import com.yslc.bean.UserBean;
import com.yslc.data.inf.IUserModel;
import com.yslc.inf.GetDataCallback;
import com.yslc.util.FileUtil;
import com.yslc.util.HttpUtil;
import com.yslc.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户模块网络请求数据层
 * <p>
 * Created by HH on 2016/2/25.
 */
public class UserModelImpl implements IUserModel {
    private Context context;

    public UserModelImpl(Context context) {
        this.context = context;
    }

    /**
     * 登录
     * @param userName 用户名
     * @param userPass 密码
     * @param callback 回调
     */
    @Override
    public void userLogin(String userName, String userPass, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("account", userName);
        params.put("password", userPass);
        HttpUtil.post(HttpUtil.POST_LOGIN, context, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);
                        callback.failer(Constant.LOGIN_FAILER);
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        try {
                            JSONObject jo = new JSONObject(arg0);
                            if (jo.optString("Status").equals(
                                    HttpUtil.ERROR_CODE)) {
                                callback.failer(jo.optString("msg"));
                            } else {
                                UserBean user = new UserBean();
                                user.setUserId(jo.optString("Ui_Id"));
                                user.setUserImageUrl(jo.optString("Ui_Img"));
                                callback.success(user);
                            }
                        } catch (JSONException e) {
                            callback.failer(null);
                            e.printStackTrace();
                        }

                    }
                });
    }

    /**
     * 用户注册
     * @param userName 用户名（手机号）
     * @param userPass 加密密码
     * @param callback 回调
     */
    @Override
    public void userRegister(String userName, String userPass, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("password", userPass);
        params.put("phone", userName);
        HttpUtil.post(HttpUtil.POST_REGISTER, context, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);
                        callback.failer(Constant.REGISTER_FAILER);
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        try {
                            JSONObject jo = new JSONObject(arg0);
                            if (jo.optString("Status").equals(
                                    HttpUtil.ERROR_CODE)) {
                                callback.failer(jo.optString("msg"));
                            } else {
                                // 注册成功
                                UserBean user = new UserBean();
                                user.setUserId(new JSONObject(arg0)
                                        .optString("Ui_Id"));
                                callback.success(user);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                });
    }

    /**
     * 获取验证码
     * @param type     验证码类型
     * @param phone 手机号
     * @param callback 回调
     */
    @Override
    public void getValidationCode(String type, String phone, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("type", type);
        params.put("phone", phone);
        HttpUtil.get(HttpUtil.GET_CODE, context, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);
                        callback.failer("验证码获取失败");//TODO
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);

                        if (arg0.equals(HttpUtil.ERROR_CODE)) {
                            callback.failer("验证码获取失败");//TODO
                        } else {
                            try {
                                JSONObject jo = new JSONObject(arg0);
                                if (jo.optString("Status").equals("-1")) {
                                    callback.failer(jo.optString("msg"));
                                } else {
                                    callback.success(jo.optString("msg"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 修改密码
     * @param userId 用户id
     * @param oldUserPass 用户原密码
     * @param newUserPass 用户新密码
     * @param callback 回调
     */
    @Override
    public void userUpdatePass(String userId, String oldUserPass, String newUserPass, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("Ui_Id", userId);
        params.put("oldPassword", oldUserPass);
        params.put("newPassword", newUserPass);
        HttpUtil.post(HttpUtil.POST_UPDATE_PASSWORDS, context, params,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);

                        callback.failer("密码修改失败");
                    }

                    @Override
                    public void onSuccess(JSONObject jo) {
                        super.onSuccess(jo);

                        if (jo.optString("Status").equals(HttpUtil.ERROR_CODE)) {
                            callback.failer(jo.optString("msg"));
                        } else {
                            callback.success(jo.optString("msg"));
                        }
                    }
                });
    }

    /**
     * 修改密码
     * @param userName 手机号（用户名）
     * @param userPass 新密码
     * @param callback 回调
     */
    @Override
    public void userForgetPass(String userName, String userPass, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("Ui_Account", userName);
        params.put("Ui_Password", userPass);
        HttpUtil.post(HttpUtil.POST_FIND_PASSWORD, context, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);

                        callback.failer("密码修改失败");
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);

                        try {
                            JSONObject jo = new JSONObject(arg0);
                            if (jo.optString("Status").equals(
                                    HttpUtil.ERROR_CODE)) {
                                callback.failer(jo.optString("msg"));
                            } else {
                                // 密码修改成功
                                callback.success("密码修改成功");//TODO
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void uploadUserImage(String userId, Bitmap bitmap, GetDataCallback callback) {
        RequestParams params = new RequestParams();
        params.put("Ui_Id", userId);
        try {
            params.put("file", FileUtil.inputstreamToFile(FileUtil.crieImage(bitmap), Constant.FILES_USERIMG));
        } catch (Exception e) {
            callback.failer("IO异常");
            e.printStackTrace();
        }
        HttpUtil.post(HttpUtil.UPLOAD_USER_IMAGE, context, params,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);
                        callback.failer("上传失败");
                    }

                    @Override
                    public void onSuccess(int arg0, JSONObject arg1) {
                        super.onSuccess(arg0, arg1);
                        ToastUtil.showMessage(context, arg1.optString("msg"));
                        if (!arg1.optString("Status").equals(HttpUtil.ERROR_CODE)) {
                            callback.success(arg1.optString("imgUrl"));
                        }
                    }
                });
    }

}
