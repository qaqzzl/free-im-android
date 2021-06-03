package net.qiujuer.italker.factory.model.api.group;

import java.util.HashSet;
import java.util.Set;

/**
 * 群创建的Model
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class GroupCreateModel {
    private String name;// 群名称
    private String desc;// 群描述
    private String avatar;// 群图片
    private Set<Integer> member_list = new HashSet<>();

    public GroupCreateModel(String name, String desc, String picture, Set<Integer> users) {
        this.name = name;
        this.desc = desc;
        this.avatar = picture;
        this.member_list = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPicture() {
        return avatar;
    }

    public void setPicture(String picture) {
        this.avatar = picture;
    }

    public Set<Integer> getUsers() {
        return member_list;
    }

    public void setUsers(Set<Integer> users) {
        this.member_list = users;
    }
}
