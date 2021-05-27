package net.qiujuer.italker.factory.model.card;

import com.raizlabs.android.dbflow.annotation.Column;

import net.qiujuer.italker.factory.model.db.Group;
import net.qiujuer.italker.factory.model.db.Message;
import net.qiujuer.italker.factory.model.db.User;

import java.util.Date;


/**
 * 消息的卡片，用于接收服务器返回信息
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class MessageCard {
    private String id;          // 本地消息ID
    private Date createAt;      // 创建时间

    private String user_id;
    private String content;
    private int code;
    private int chatroom_id;
    private int chatroom_type;
    private String message_id;
    private String device_id;
    private String client_type;
    private Integer message_send_time;      // 发送成功时间


    // 两个额外的本地字段
    // transient 不会被Gson序列化和反序列化
    private transient int status = Message.STATUS_DONE; //当前消息状态
    private transient boolean uploaded = false; // 上传是否完成（对应的是文件）

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getClient_type() {
        return client_type;
    }

    public void setClient_type(String client_type) {
        this.client_type = client_type;
    }

    public Integer getMessage_send_time() {
        return message_send_time;
    }

    public void setMessage_send_time(Integer message_send_time) {
        this.message_send_time = message_send_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    /**
     * 构建一个消息
     *
     * @param sender   发送者
     * @return 一个消息
     */
    public Message build(User sender) {
        Message message = new Message();
        message.setId(id);
        message.setMessage_id(message_id);
        message.setContent(content);
        message.setChatroom_id(chatroom_id);
        message.setChatroom_type(chatroom_type);
        message.setType(code);
        message.setCreateAt(createAt);
        message.setSender(sender);
        message.setStatus(status);
        return message;
    }
}
