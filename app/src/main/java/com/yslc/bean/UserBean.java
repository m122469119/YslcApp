package com.yslc.bean;

/**
 * 用户Bean
 * ●用户Id
 * ●用户名称（用户昵称)
 * ●用户账号（电话号码）
 * ●用户头像地址
 *
 * @author HH
 */
public class UserBean {
    private String userId;
    private String userName;
    private String userPhone;
    private String userImageUrl;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }
}
