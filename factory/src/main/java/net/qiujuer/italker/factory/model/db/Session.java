package net.qiujuer.italker.factory.model.db;

import android.text.TextUtils;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import net.qiujuer.italker.factory.data.helper.GroupHelper;
import net.qiujuer.italker.factory.data.helper.MessageHelper;
import net.qiujuer.italker.factory.data.helper.SessionHelper;
import net.qiujuer.italker.factory.data.helper.UserHelper;

import java.util.Date;
import java.util.Objects;


/**
 * 本地的会话表
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
@Table(database = AppDatabase.class)
public class Session extends BaseDbModel<Session> {
    @PrimaryKey
    private int chatroom_id; //聊天室ID
    @Column
    private String id; // Id, 是Message中的接收者User的Id或者群的Id
    @Column
    private String picture; // 图片，接收者用户的头像，或者群的图片
    @Column
    private String title; // 标题，用户的名称，或者群的名称
    @Column
    private String content; // 显示在界面上的简单内容，是Message的一个描述
    @Column
    private int receiverType = Message.RECEIVER_TYPE_NONE; // 类型，对应人，或者群消息
    @Column
    private int unReadCount; // 未读数量，当没有在当前界面时，应当增加未读数量
    @Column
    private Date modifyAt; // 最后更改时间

    @ForeignKey(tableClass = Message.class)
    private Message message; // 对应的消息，外键为Message的Id

    public Session() {

    }

    public Session(Identify identify) {
        this.chatroom_id = identify.chatroom_id;
        this.id = identify.id;
        this.receiverType = identify.type;
    }

    public Session(Message message) {
        if (message.getChatroom_type() == Message.RECEIVER_TYPE_NONE) {
            receiverType = Message.RECEIVER_TYPE_NONE;
            User other = UserHelper.findFromLocalByChatroomID(chatroom_id);
            if (other == null) {
                Session session = SessionHelper.findFromNet(chatroom_id);
                id = session.getId();
                picture = session.getPicture();
                title = session.getTitle();
            } else {
                id = other.getId();
                picture = other.getAvatar();
                title = other.getName();
            }
        } else {
            receiverType = Message.RECEIVER_TYPE_GROUP;
            Group group = GroupHelper.findFromLocalByChatroomID(chatroom_id);
            if (group == null) {
                Session session = SessionHelper.findFromNet(chatroom_id);
                id = session.getId();
                picture = session.getPicture();
                title = session.getTitle();
            } else {
                id = group.getId();
                picture = group.getPicture();
                title = group.getName();
            }
        }
        this.chatroom_id = message.getChatroom_id();
        this.message = message;
        this.content = message.getSampleContent();
        this.modifyAt = message.getCreateAt();
    }

    public int getChatroom_id() {
        return chatroom_id;
    }

    public void setChatroom_id(int chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(int receiverType) {
        this.receiverType = receiverType;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        return receiverType == session.receiverType
                && unReadCount == session.unReadCount
                && Objects.equals(chatroom_id, session.chatroom_id)
                && Objects.equals(id, session.id)
                && Objects.equals(picture, session.picture)
                && Objects.equals(title, session.title)
                && Objects.equals(content, session.content)
                && Objects.equals(modifyAt, session.modifyAt)
                && Objects.equals(message, session.message);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + receiverType;
        return result;
    }

    @Override
    public boolean isSame(Session oldT) {
        return Objects.equals(chatroom_id, oldT.chatroom_id);
    }

    @Override
    public boolean isUiContentSame(Session oldT) {
        return this.content.equals(oldT.content)
                && Objects.equals(this.modifyAt, oldT.modifyAt);
    }


    /**
     * 对于一条消息，我们提取主要部分，用于和Session进行对应
     *
     * @param message 消息Model
     * @return 返回一个Session.Identify
     */
    public static Identify createSessionIdentify(Message message) {
        Identify identify = new Identify();
        identify.type = message.getChatroom_type();
        identify.chatroom_id = message.getChatroom_id();
        if (identify.type == Message.RECEIVER_TYPE_GROUP) {
            Group group = GroupHelper.findFromLocalByChatroomID(identify.chatroom_id);
            if (group == null) {
                Session session = SessionHelper.findFromNet(identify.chatroom_id);
                identify.id = session.getId();
            } else {
                identify.id = group.getId();
            }
        } else {
            User user = UserHelper.findFromLocalByChatroomID(identify.chatroom_id);
            if (user == null) {
                Session session = SessionHelper.findFromNet(identify.chatroom_id);
                identify.id = session.getId();
            } else {
                identify.id = user.getId();
            }
        }
        return identify;
    }

    /**
     * 刷新会话对应的信息为当前Message的最新状态
     */
    public void refreshToNow() {
        Message message;
        if (receiverType == Message.RECEIVER_TYPE_GROUP) {
            // 刷新当前对应的群的相关信息
            message = MessageHelper.findLastWith(chatroom_id);
            if (message == null) {
                // 如果没有基本信息
                if (TextUtils.isEmpty(picture)
                        || TextUtils.isEmpty(this.title)) {
                    // 查询群
                    Group group = GroupHelper.findFromLocal(id);
                    if (group != null) {
                        this.picture = group.getPicture();
                        this.title = group.getName();
                    }
                }

                this.message = null;
                this.content = "";
                this.modifyAt = new Date(System.currentTimeMillis());
            } else {
                // 本地有最后一条聊天记录
                if (TextUtils.isEmpty(picture)
                        || TextUtils.isEmpty(this.title)) {
                    // 如果没有基本信息, 直接从Message中去load群信息
                    Group group = GroupHelper.findFromLocal(id);
                    if (group == null) {
                        group = GroupHelper.findFormNet(id);
                    }
                    group.load();
                    this.picture = group.getPicture();
                    this.title = group.getName();
                }

                this.message = message;
                this.content = message.getSampleContent();
                this.modifyAt = message.getCreateAt();
            }
        } else {
            // 和人聊天的
            message = MessageHelper.findLastWith(chatroom_id);
            if (message == null) {
                // 我和他的消息已经删除完成了
                // 如果没有基本信息
                if (TextUtils.isEmpty(picture)
                        || TextUtils.isEmpty(this.title)) {
                    // 查询人
                    User user = UserHelper.findFromLocal(id);
                    if (user != null) {
                        this.picture = user.getAvatar();
                        this.title = user.getName();
                    }
                }

                this.message = null;
                this.content = "";
                this.modifyAt = new Date(System.currentTimeMillis());
            } else {
                // 我和他有消息来往

                // 如果没有基本信息
                if (TextUtils.isEmpty(picture)
                        || TextUtils.isEmpty(this.title)) {
                    // 查询人
                    User other = UserHelper.findFromLocal(id);
                    if (other == null) {
                        other = UserHelper.findFromNet(id);
                    }
                    other.load(); // 懒加载问题
                    this.picture = other.getAvatar();
                    this.title = other.getName();
                }

                this.message = message;
                this.content = message.getSampleContent();
                this.modifyAt = message.getCreateAt();
            }
        }
    }


    /**
     * 对于会话信息，最重要的部分进行提取
     * 其中我们主要关注两个点：
     * 一个会话最重要的是标示是和人聊天还是在群聊天；
     * 所以对于这点：Id存储的是人或者群的Id
     * 紧跟着Type：存储的是具体的类型（人、群）
     * equals 和 hashCode 也是对两个字段进行判断
     */
    public static class Identify {
        public int chatroom_id;
        public String id;
        public int type;


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Identify identify = (Identify) o;
            return chatroom_id == identify.chatroom_id;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + type;
            return result;
        }
    }
}