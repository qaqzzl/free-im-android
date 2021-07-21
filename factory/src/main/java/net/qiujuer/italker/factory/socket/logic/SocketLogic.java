package net.qiujuer.italker.factory.socket.logic;

import android.content.Context;
import android.util.Log;

import net.qiujuer.italker.factory.data.message.MessageCenter;
import net.qiujuer.italker.factory.data.message.MessageDispatcher;
import net.qiujuer.italker.factory.model.api.SocketModel;
import net.qiujuer.italker.factory.model.card.MessageCard;
import net.qiujuer.italker.factory.persistence.Account;
import net.qiujuer.italker.factory.persistence.Socket;
import net.qiujuer.italker.factory.socket.SocketManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * @author qaqzz
 * @description TODO
 * @date 2020/3/17 10:09
 */
public class SocketLogic {
    private Context mContext;
    private static volatile SocketLogic instance = null;
    private boolean isSyncTrigger = false;

    private SocketLogic(Context context) {
        mContext = context;
    }

    public static SocketLogic getInstance(Context context) {
        if (instance == null) {
            synchronized (SocketLogic.class) {
                if (instance == null) {
                    instance = new SocketLogic(context);
                }
            }
        }
        return instance;
    }

    // 消息接受处理
    public void MessageHandle(int action, String message)
    {
        String me_uid = Account.getUserId();

        switch (action) {
            case 3:      // 消息
                JSONObject message_json;
                try {
                    message_json = new JSONObject(message);
                    Integer chatroom_id = message_json.optInt("ChatroomId");
                    String message_id = message_json.optString("MessageId");
                    String content = message_json.optString("Content");
                    Integer message_send_time = message_json.optInt("MessageSendTime");
                    Integer code = message_json.optInt("Code");
                    String user_id = message_json.optString("UserId");
                    Integer chatroomType = message_json.optInt("ChatroomType");
                    String deviceID = message_json.optString("DeviceID");
                    String clientType = message_json.optString("ClientType");


                    String max_message_id = Socket.getMessage_id();
                    if (message_id.compareTo(max_message_id) > 0 && this.isSyncTrigger) {
                        Socket.setMessage_id(message_id);
                    }

                    MessageCard card = new MessageCard();
                    card.setChatroom_type(chatroomType);
                    card.setChatroom_id(chatroom_id);
                    card.setUser_id(user_id);
                    card.setCode(code);
                    card.setContent(content);
                    card.setMessage_id(message_id);
                    card.setMessage_send_time(message_send_time);
                    card.setDevice_id(deviceID);
                    card.setClient_type(clientType);
                    card.setId(UUID.randomUUID().toString());
                    card.setCreateAt(new Date((message_send_time * 1000L)));
                    getMessageCenter().dispatch(card);

                    // 消息回执
                    SocketManager.getInstance(mContext).sendTcpMessage(SocketModel.Action_MessageACK, message_id.getBytes());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 4:      //服务端消息回执

                break;
            case 10:      // 连接认证消息
                // 认证成功
                if (true) {
                    Log.d("TEST SOCKET", "连接认认证成功");
                    SyncTrigger();
                }
                break;
        }
    }

    // 消息同步
    public void SyncTrigger()
    {
        Log.d("TEST SOCKET", "消息同步");
        String message_id = Socket.getMessage_id();
        if (message_id != null ){
            SocketManager.getInstance(mContext).sendTcpMessage(SocketModel.Action_SyncTrigger, message_id.getBytes());
        }
        this.isSyncTrigger = true;
    }

    /**
     * 获取一个消息中心的实现类
     *
     * @return 消息中心的规范接口
     */
    public static MessageCenter getMessageCenter() {
        return MessageDispatcher.instance();
    }

}
