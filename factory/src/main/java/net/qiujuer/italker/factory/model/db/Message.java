package net.qiujuer.italker.factory.model.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import net.qiujuer.italker.factory.persistence.Account;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


/**
 * 本地的消息表
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
@Table(database = AppDatabase.class)
public class Message extends BaseDbModel<Message> implements Serializable {
    // 接收者类型
    public static final int RECEIVER_TYPE_NONE = 1;
    public static final int RECEIVER_TYPE_GROUP = 2;

    // 消息类型
    public static final int TYPE_STR = 1;
    public static final int TYPE_PIC = 2;
    public static final int TYPE_FILE = 6;
    public static final int TYPE_AUDIO = 5;

    // 消息状态
    public static final int STATUS_DONE = 0; // 正常状态
    public static final int STATUS_CREATED = 1; // 创建状态
    public static final int STATUS_FAILED = 2; // 发送失败状态

    @PrimaryKey
    private String id;  // 本地消息ID 主键
    @Column
    private String message_id;  // 服务器消息ID
    @Column
    private String content;// 内容
    @Column
    private int chatroom_id; // 聊天室ID
    @Column
    private int chatroom_type;   // 聊天室类型
    @Column
    private String attach;// 附属信息 ***** 废弃
    @Column
    private int type;// 消息类型
    @Column
    private Date createAt;// 创建时间
    @Column
    private int status;// 当前消息的状态

    // 在加载Message信息的时候，User并没有，懒加载
    @ForeignKey(tableClass = User.class, stubbedRelationship = true)
    private User sender;// 发送者 外键

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public int getChatroom_id() {
        return chatroom_id;
    }

    public void setChatroom_id(int chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    public int getChatroom_type() {
        return chatroom_type;
    }

    public void setChatroom_type(int chatroom_type) {
        this.chatroom_type = chatroom_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * 当消息类型为普通消息（发送给人的消息）
     * 该方法用于返回，和我聊天的人是谁
     * <p>
     * 和我聊天，要么对方是发送者，要么对方是接收者
     *
     * @return 和我聊天的人
     */
    User getOther() {
        return sender;
    }

    /**
     * 构建一个简单的消息描述
     * 用于简化消息显示
     *
     * @return 一个消息描述
     */
    String getSampleContent() {
        if (type == TYPE_PIC)
            return "[图片]";
        else if (type == TYPE_AUDIO)
            return "🎵";
        else if (type == TYPE_FILE)
            return "📃";
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return type == message.type
                && status == message.status
                && Objects.equals(id, message.id)
                && Objects.equals(content, message.content)
                && Objects.equals(attach, message.attach)
                && Objects.equals(createAt, message.createAt)
                && Objects.equals(sender, message.sender);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean isSame(Message oldT) {
        // 两个类，是否指向的是同一个消息
        if (message_id == null || oldT.message_id == null) {
            return Objects.equals(id, oldT.id);
        } else {
            return Objects.equals(message_id, oldT.message_id);
        }

    }

    @Override
    public boolean isUiContentSame(Message oldT) {
        // 对于同一个消息当中的字段是否有不同
        // 这里，对于消息，本身消息不可进行修改；只能添加删除
        // 唯一会变化的就是本地（手机端）消息的状态会改变
        return oldT == this || status == oldT.status;
    }
}
