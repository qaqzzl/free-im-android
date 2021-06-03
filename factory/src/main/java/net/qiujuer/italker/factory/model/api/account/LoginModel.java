package net.qiujuer.italker.factory.model.api.account;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class LoginModel {
    private String phone;
    private String verify_code;
    private String pushId;

    public LoginModel(String account, String password) {
        this.phone = account;
        this.verify_code = password;
    }

    public LoginModel(String account, String password, String pushId) {
        this.phone = account;
        this.verify_code = password;
        this.pushId = pushId;
    }

    public String getAccount() {
        return phone;
    }

    public void setAccount(String account) {
        this.phone = account;
    }

    public String getPassword() {
        return verify_code;
    }

    public void setPassword(String password) {
        this.verify_code = password;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}
