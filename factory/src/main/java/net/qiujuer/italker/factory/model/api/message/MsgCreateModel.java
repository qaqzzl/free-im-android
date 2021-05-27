package net.qiujuer.italker.factory.model.api.message;

import net.qiujuer.italker.factory.model.card.MessageCard;
import net.qiujuer.italker.factory.model.db.Message;
import net.qiujuer.italker.factory.persistence.Account;

import java.util.Date;
import java.util.UUID;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class MsgCreateModel {
    // ID从客户端生产，一个UUID
    private String id;

    private String content;
    private int    chatroom_id;
    private int    chatroom_type;

    // 消息类型
    private int code = Message.TYPE_STR;

    private MsgCreateModel() {
        // 随机生产一个UUID
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    // 当我们需要发送一个文件的时候，content刷新的问题

    private transient MessageCard card;

    // 返回一个Card
    public MessageCard buildCard() {
        if (card == null) {
            MessageCard card = new MessageCard();
            card.setId(id);

            card.setContent(content);
            card.setCode(code);
            card.setUser_id(Account.getUserId());
            card.setChatroom_id(chatroom_id);
            card.setChatroom_type(chatroom_type);
            // 通过当前model建立的Card就是一个初步状态的Card
            card.setStatus(Message.STATUS_CREATED);
            card.setCreateAt(new Date(System.currentTimeMillis()));
            this.card = card;
        }
        return this.card;
    }

    // 同步到卡片的最新状态
    public void refreshByCard() {
        if (card == null)
            return;
        // 刷新内容和附件信息
        this.content = card.getContent();
    }


    /**
     * 建造者模式，快速的建立一个发送Model
     */
    public static class Builder {
        private MsgCreateModel model;

        public Builder() {
            this.model = new MsgCreateModel();
        }

        // 设置接收者
        public Builder receiver(int chatroom_id, int chatroom_type) {
            this.model.chatroom_id = chatroom_id;
            this.model.chatroom_type = chatroom_type;
            return this;
        }

        // 设置内容
        public Builder content(String content, int code) {
            this.model.content = content;
            this.model.code = code;
            return this;
        }

        public MsgCreateModel build() {
            return this.model;
        }

    }

    /**
     * 把一个Message消息，转换为一个创建状态的CreateModel
     *
     * @param message Message
     * @return MsgCreateModel
     */
    public static MsgCreateModel buildWithMessage(Message message) {
        MsgCreateModel model = new MsgCreateModel();
        model.id = message.getId();
        model.content = message.getContent();
        model.code = message.getType();
        model.chatroom_type = message.getChatroom_type();
        model.chatroom_id = message.getChatroom_id();

        return model;
    }
}
