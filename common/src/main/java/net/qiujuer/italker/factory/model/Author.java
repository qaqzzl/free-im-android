package net.qiujuer.italker.factory.model;

/**
 * 基础用户接口
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public interface Author {
    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    String getAvatar();

    void setAvatar(String avatar);

    int getChatroom_id();

    void setChatroom_id(int chatroom_id);
}
