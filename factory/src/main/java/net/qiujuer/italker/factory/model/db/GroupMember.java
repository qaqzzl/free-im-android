package net.qiujuer.italker.factory.model.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.Date;
import java.util.Objects;


/**
 * 群成员Model表
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
@Table(database = AppDatabase.class)
public class GroupMember extends BaseDbModel<GroupMember> {
    // 消息通知级别
    public static final int NOTIFY_LEVEL_INVALID = -1; // 关闭消息
    public static final int NOTIFY_LEVEL_NONE = 0; // 正常

    @PrimaryKey
    private String id; // 主键
    @Column
    private String alias;// 别名，备注名
    @Column
    private String member_identity; // 权限

    @Column
    private Long modifyAt;// 更新时间

    @ForeignKey(tableClass = Group.class, stubbedRelationship = true)
    private Group group;// 对应的群外键

    @ForeignKey(tableClass = User.class, stubbedRelationship = true)
    private User user;// 对应的用户外键

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Long getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Long modifyAt) {
        this.modifyAt = modifyAt;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMember_identity() {
        return member_identity;
    }

    public void setMember_identity(String member_identity) {
        this.member_identity = member_identity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupMember that = (GroupMember) o;

        return Objects.equals(id, that.id)
                && Objects.equals(alias, that.alias)
                && Objects.equals(modifyAt, that.modifyAt)
                && Objects.equals(group, that.group)
                && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean isSame(GroupMember old) {
        return Objects.equals(id, old.id);
    }

    @Override
    public boolean isUiContentSame(GroupMember that) {
        return Objects.equals(member_identity, that.member_identity)
                && Objects.equals(alias, that.alias)
                && Objects.equals(modifyAt, that.modifyAt);
    }
}
