package net.qiujuer.italker.factory.model.card;

import com.google.gson.annotations.Expose;

import net.qiujuer.italker.factory.model.db.Group;
import net.qiujuer.italker.factory.model.db.GroupMember;
import net.qiujuer.italker.factory.model.db.User;

import java.util.Date;

/**
 * 群成员卡片信息
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public class GroupMemberCard {
    private String group_member_id;
    private String alias;               // 成员群昵称
    private String member_identity;     // 身份 成员身份: admin-管理员, root-群主, common-普通成员
    private String member_id;           // 用户ID
    private String group_id;
    private Long created_at;

    public String getId() {
        return group_member_id;
    }

    public void setId(String id) {
        this.group_member_id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getUserId() {
        return member_id;
    }

    public void setUserId(String userId) {
        this.member_id = userId;
    }

    public String getGroupId() {
        return group_id;
    }

    public void setGroupId(String groupId) {
        this.group_id = groupId;
    }

    public Long getModifyAt() {
        return created_at;
    }

    public void setModifyAt(Long modifyAt) {
        this.created_at = modifyAt;
    }

    public String getMember_identity() {
        return member_identity;
    }

    public void setMember_identity(String member_identity) {
        this.member_identity = member_identity;
    }

    public GroupMember build(Group group, User user) {
        GroupMember member = new GroupMember();
        member.setId(this.group_member_id);
        member.setAlias(this.alias);
        member.setModifyAt(this.created_at);
        member.setMember_identity(member_identity);
        member.setGroup(group);
        member.setUser(user);
        return member;
    }
}
