package net.qiujuer.italker.factory.model.api.account;

import net.qiujuer.italker.factory.model.db.User;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class AccountRspModel {
    // 用户基本信息
    private User user;
    // 当前登录的uid
    private String uid;
    // 当前登录成功后获取的Token,
    // 可以通过Token获取用户的所有信息
    private String access_token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return access_token;
    }

    public void setToken(String token) {
        this.access_token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "AccountRspModel{" +
                ", token='" + access_token + '\'' +
                ", uid=" + uid +
                '}';
    }
}
