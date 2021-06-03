package net.qiujuer.italker.factory.model.api.group;

import java.util.HashSet;
import java.util.Set;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class GroupMemberAddModel {
    private int group_id;
    private Set<Integer> member_list = new HashSet<>();

    public GroupMemberAddModel(int group_id, Set<Integer> users) {
        this.member_list = users;
        this.group_id = group_id;
    }

    public Set<Integer> getUsers() {
        return member_list;
    }

    public void setUsers(Set<Integer> users) {
        this.member_list = users;
    }
}
