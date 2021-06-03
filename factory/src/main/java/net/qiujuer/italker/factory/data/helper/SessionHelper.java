package net.qiujuer.italker.factory.data.helper;

import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.qiujuer.italker.factory.Factory;
import net.qiujuer.italker.factory.model.api.RspModel;
import net.qiujuer.italker.factory.model.card.UserCard;
import net.qiujuer.italker.factory.model.db.Session;
import net.qiujuer.italker.factory.model.db.Session_Table;
import net.qiujuer.italker.factory.model.db.User;
import net.qiujuer.italker.factory.net.Network;
import net.qiujuer.italker.factory.net.RemoteService;

import retrofit2.Response;

/**
 * 会话辅助工具类
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class SessionHelper {
    // 从本地查询Session
    public static Session findFromLocal(int chatroom_id) {
        return SQLite.select()
                .from(Session.class)
                .where(Session_Table.chatroom_id.eq(chatroom_id))
                .querySingle();
    }

    // 从网络查询
    public static Session findFromNet(int chatroom_id) {
        RemoteService remoteService = Network.remote();
        try {
            JsonObject parmas = new JsonObject();
            parmas.addProperty("chatroom_id", chatroom_id);
            Response<RspModel<Session>> response = remoteService.getChatroomInfo(parmas).execute();
            Session session = response.body().getData();
            return session;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
