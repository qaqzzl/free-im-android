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
public class Account {
    private static final String KEY_PUSH_ID = "KEY_PUSH_ID";
    private static final String KEY_IS_BIND = "KEY_IS_BIND";
    private static final String KEY_TOKEN = "KEY_TOKEN";
    private static final String KEY_USER_ID = "KEY_USER_ID";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    private static final String KEY_DEVICE_ID = "KEY_DEVICE_ID";
    private static final String KEY_IS_RefreshContacts = "isRefreshContacts";
    private static final String KEY_IS_RefreshGroups = "isRefreshGroups";

    // 设备的推送Id
    private static String pushId;
    // 设备Id是否已经绑定到了服务器
    private static boolean isBind;
    // 登录状态的Token，用来接口请求
    private static String token;
    // 登录的用户ID
    private static String userId;
    // 登录的账户
    private static String account;
    // 设备ID
    private static String deviceId;
    // 设备类型
    private static String device_type = "mobile";
    // 客户端类型
    private static String client_type = "android";

    private static boolean isRefreshContacts = false;   //是否初始化联系人数据
    private static boolean isRefreshGroups = false;     //是否初始化群组数据

    public static boolean isRefreshContacts() {
        return isRefreshContacts;
    }

    public static void setRefreshContacts(boolean isRefreshContacts) {
        Account.isRefreshContacts = isRefreshContacts;
        Account.save(Factory.app());
    }

    public static boolean isRefreshGroups() {
        return isRefreshGroups;
    }

    public static void setRefreshGroups(boolean isRefreshGroups) {
        Account.isRefreshGroups = isRefreshGroups;
        Account.save(Factory.app());
    }

    public static String getDevice_type() {
        return device_type;
    }

    public static void setDevice_type(String device_type) {
        Account.device_type = device_type;
    }

    public static String getClient_type() {
        return client_type;
    }

    public static void setClient_type(String client_type) {
        Account.client_type = client_type;
    }

    /**
     * 存储数据到XML文件，持久化
     */
    private static void save(Context context) {
        // 获取数据持久化的SP
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);
        // 存储数据
        sp.edit()
                .putString(KEY_PUSH_ID, pushId)
                .putBoolean(KEY_IS_BIND, isBind)
                .putString(KEY_TOKEN, token)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_ACCOUNT, account)
                .putString(KEY_ACCOUNT, account)
                .putString(KEY_DEVICE_ID, deviceId)
                .putBoolean(KEY_IS_RefreshContacts, isRefreshContacts)
                .putBoolean(KEY_IS_RefreshGroups, isRefreshGroups)
                .apply();
    }

    /**
     * 进行数据加载
     */
    public static void load(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);
        pushId = sp.getString(KEY_PUSH_ID, "");
        isBind = sp.getBoolean(KEY_IS_BIND, false);
        token = sp.getString(KEY_TOKEN, "");
        userId = sp.getString(KEY_USER_ID, "");
        account = sp.getString(KEY_ACCOUNT, "");
        deviceId = sp.getString(KEY_DEVICE_ID, "");
        isRefreshContacts = sp.getBoolean(KEY_IS_RefreshContacts, false);
        isRefreshGroups = sp.getBoolean(KEY_IS_RefreshGroups, false);
    }


    /**
     * 设置并存储设备的Id
     *
     * @param deviceId 设备ID
     */
    public static void setDeviceId(String deviceId) {
        Account.deviceId = deviceId;
        Account.save(Factory.app());
    }

    public static String getDeviceId() {
        return deviceId;
    }

    /**
     * 设置并存储设备的推送Id
     *
     * @param pushId 设备的推送ID
     */
    public static void setPushId(String pushId) {
        Account.pushId = pushId;
        Account.save(Factory.app());
    }

    /**
     * 获取推送Id
     *
     * @return 推送Id
     */
    public static String getPushId() {
        return pushId;
    }

    /**
     * 返回当前账户是否登录
     *
     * @return True已登录
     */
    public static boolean isLogin() {
        // 用户Id 和 Token 不为空
        return !TextUtils.isEmpty(userId)
                && !TextUtils.isEmpty(token);
    }

    /**
     * 返回是否已经初始化账号数据
     * @return boolean
     */
    public static boolean isInitAccountData()
    {
        return isRefreshContacts && isRefreshGroups;
    }

    /**
     * 是否已经完善了用户信息
     *
     * @return True 是完成了
     */
    public static boolean isComplete() {
        if(true) {
            return true;
        }
        // 首先保证登录成功
        if (isLogin()) {
            User self = getUser();
            return !TextUtils.isEmpty(self.getSignature())
                    && !TextUtils.isEmpty(self.getAvatar())
                    && TextUtils.isEmpty(self.getGender());
        }
        // 未登录返回信息不完全
        return false;
    }

    /**
     * 是否已经绑定到了服务器
     *
     * @return True已绑定
     */
    public static boolean isBind() {
        return isBind;
    }

    /**
     * 设置绑定状态
     */
    public static void setBind(boolean isBind) {
        Account.isBind = isBind;
        Account.save(Factory.app());
    }

    /**
     * 保存我自己的信息到持久化XML中
     *
     * @param model AccountRspModel
     */
    public static void login(AccountRspModel model) {
        // 存储当前登录的账户, token, 用户Id，方便从数据库中查询我的信息
        Account.token = model.getToken();
        Account.userId = model.getUid();
        save(Factory.app());
    }

    /**
     * 获取当前登录的用户信息
     *
     * @return User
     */
    public static User getUser() {
        // 如果为null返回一个new的User，其次从数据库查询
        return TextUtils.isEmpty(userId) ? new User() : SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(userId))
                .querySingle();
    }

    /**
     * 返回用户Id
     *
     * @return 用户Id
     */
    public static String getUserId() {
        return userId;
    }

    /**
     * 获取当前登录的Token
     *
     * @return Token
     */
    public static String getToken() {
        return token;
    }
}
