package net.qiujuer.italker.factory.model.card;

import com.google.gson.annotations.Expose;

import net.qiujuer.italker.factory.model.db.Group;
import net.qiujuer.italker.factory.model.db.User;

import java.util.Date;

/**
 * 群卡片信息
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class GroupCard {
    private String id;                  // ID, 对用户展示并且唯一
    private String group_id;               // 群组ID
    private String name;
    private String desc;
    private String avatar;
    private String owner_member_id;
    private String permissions;         // '聊天室权限。 public:开放, protected:受保护(可见,并且管理员同意才能加入), private:私有(不可申请,并且管理员邀请才能加入)
    private int notify_level = 0;       // 通知级别，0：正常，1：接收消息但不提醒，2：屏蔽群消息
    private Long join_at;
    private Long updated_at;
    private int chatroom_id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getOwnerId() {
        return owner_member_id;
    }

    public void setOwnerId(String ownerId) {
        this.owner_member_id = ownerId;
    }

    public int getNotifyLevel() {
        return notify_level;
    }

    public void setNotifyLevel(int notifyLevel) {
        this.notify_level = notifyLevel;
    }

    public Long getJoinAt() {
        return join_at;
    }

    public void setJoinAt(Long joinAt) {
        this.join_at = joinAt;
    }

    public Long getModifyAt() {
        return updated_at;
    }

    public void setModifyAt(Long modifyAt) {
        this.updated_at = modifyAt;
    }

    /**
     * 把一个群的信息，build为一个群Model
     * 由于卡片中有创建者的Id，但是没有创建者这个人的Model；
     * 所以Model需求在外部准备好传递进来
     *
     * @param owner 创建者
     * @return 群信息
     */
    public Group build(User owner) {
        Group group = new Group();
        group.setId(group_id);
        group.setName(name);
        group.setDesc(desc);
        group.setPicture(avatar);
        group.setNotifyLevel(notify_level);
        group.setJoinAt(join_at);
        group.setModifyAt(updated_at);
        group.setOwner(owner);
        group.setChatroom_id(chatroom_id);
        return group;
    }
}
