package net.qiujuer.italker.factory.model.api.user;

/**
 * 用户更新信息所使用的Model
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class UserUpdateModel {
    private String name;
    private String avatar;
    private String signature;
    private String gender;

    public UserUpdateModel(String name, String portrait, String desc, String sex) {
        this.name = name;
        this.avatar = portrait;
        this.signature = desc;
        this.gender = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "UserUpdateModel{" +
                "name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", signature='" + signature + '\'' +
                ", gender=" + gender +
                '}';
    }
}
