package net.qiujuer.italker.factory.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.qiujuer.italker.factory.Factory;
import net.qiujuer.italker.factory.model.api.account.AccountRspModel;
import net.qiujuer.italker.factory.model.db.User;
import net.qiujuer.italker.factory.model.db.User_Table;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class Socket {
    private static final String KEY_MESSAGE_ID = "KEY_MESSAGE_ID";

    // 最后收到的消息ID
    private static String message_id = "";

    public static String getMessage_id() {
        return message_id;
    }

    public static void setMessage_id(String message_id) {
        Socket.message_id = message_id;
        Socket.save(Factory.app());
    }

    /**
     * 存储数据到XML文件，持久化
     */
    private static void save(Context context) {
        // 获取数据持久化的SP
        SharedPreferences sp = context.getSharedPreferences(Socket.class.getName(),
                Context.MODE_PRIVATE);
        // 存储数据
        sp.edit()
                .putString(KEY_MESSAGE_ID, message_id)
                .apply();
    }

    /**
     * 进行数据加载
     */
    public static void load(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Socket.class.getName(),
                Context.MODE_PRIVATE);
        message_id = sp.getString(KEY_MESSAGE_ID, "");
    }


}
