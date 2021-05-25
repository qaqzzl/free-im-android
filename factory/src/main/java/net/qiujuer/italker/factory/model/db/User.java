package net.qiujuer.italker.factory.model.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import net.qiujuer.italker.factory.model.Author;

import java.util.Date;
import java.util.Objects;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
@Table(database = AppDatabase.class)
public class User extends BaseDbModel<User> implements Author {
    @Override
    public boolean isUiContentSame(User old) {
        return false;
    }

    @Override
    public boolean isSame(User old) {
        return false;
    }

    public static final String SEX_MAN = "m";
    public static final String SEX_WOMAN = "w";

    // 主键
    @PrimaryKey
    private String id;
    @Column
    private String name;
    @Column
    private String avatar;
    @Column
    private String signature;
    @Column
    private String gender = "wz";

    // 我对某人的备注信息，也应该写入到数据库中
    @Column
    private String alias;


    // 我与当前User的关系状态，是否是好友 ，yes｜no
    @Column
    private String is_friend;

    // 时间字段
    @Column
    private Date modifyAt;

    public static String getSexMan() {
        return SEX_MAN;
    }

    public static String getSexWoman() {
        return SEX_WOMAN;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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

    public String getGenderText() {
        if (gender.equals("wz")) {
            return "未知";
        } else if (gender.equals("m")) {
            return "男";
        } else if (gender.equals("w")) {
            return "女";
        }
        return "保密";
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getIs_friend() {
        return is_friend;
    }

    public void setIs_friend(String is_friend) {
        this.is_friend = is_friend;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }
}

