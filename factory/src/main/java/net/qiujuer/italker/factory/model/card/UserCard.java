package net.qiujuer.italker.factory.model.card;

import com.raizlabs.android.dbflow.annotation.Column;

import net.qiujuer.italker.factory.model.Author;
import net.qiujuer.italker.factory.model.db.User;

import java.util.Date;

/**
 * 用户卡片，用于接收服务器返回
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class UserCard implements Author {


    private String member_id;
    private String nickname;
    private String avatar;
    private String signature;
    private String gender = "wz";


    // 我与当前User的关系状态，是否是好友 ，yes｜no
    private String is_friend;

    // 用户信息最后的更新时间
    private Date modifyAt;

    public String getId() {
        return member_id;
    }

    public void setId(String id) {
        this.member_id = id;
    }

    public String getName() {
        return nickname;
    }

    public void setName(String name) {
        this.nickname = name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
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

    public String getIs_friend() {
        return is_friend;
    }

    public void setIs_friend(String is_friend) {
        this.is_friend = is_friend;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    // 缓存一个对应的User, 不能被GSON框架解析使用ø
    private transient User user;

    public User build() {
        if (user == null) {
            User user = new User();
            user.setId(member_id);
            user.setName(nickname);
            user.setAvatar(avatar);
            user.setSignature(signature);
            user.setGender(gender);
            user.setIs_friend(is_friend);
            user.setModifyAt(modifyAt);
            this.user = user;
        }
        return user;
    }
}
