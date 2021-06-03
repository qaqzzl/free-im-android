package net.qiujuer.italker.factory.net;

import com.google.gson.JsonObject;

import net.qiujuer.italker.factory.model.api.RspModel;
import net.qiujuer.italker.factory.model.api.account.AccountRspModel;
import net.qiujuer.italker.factory.model.api.account.AppVersionModel;
import net.qiujuer.italker.factory.model.api.account.LoginModel;
import net.qiujuer.italker.factory.model.api.account.RegisterModel;
import net.qiujuer.italker.factory.model.api.group.GroupCreateModel;
import net.qiujuer.italker.factory.model.api.group.GroupMemberAddModel;
import net.qiujuer.italker.factory.model.api.message.MsgCreateModel;
import net.qiujuer.italker.factory.model.api.user.UserUpdateModel;
import net.qiujuer.italker.factory.model.card.GroupCard;
import net.qiujuer.italker.factory.model.card.GroupMemberCard;
import net.qiujuer.italker.factory.model.card.MessageCard;
import net.qiujuer.italker.factory.model.card.UserCard;
import net.qiujuer.italker.factory.model.db.Session;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * 网络请求的所有的接口
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public interface RemoteService {

    /**
     * 注册接口
     *
     * @param model 传入的是RegisterModel
     * @return 返回的是RspModel<AccountRspModel>
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    /**
     * 登录接口
     *
     * @param model LoginModel
     * @return RspModel<AccountRspModel>
     */
    @POST("login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 发送短信验证码
     * {
     *     "phone": "18016278888",
     *     "type":"login"
     * }
     * @return RspModel<AccountRspModel>
     */
    @POST("common/send.sms")
    Call<RspModel> sendSms(@Body JsonObject parmas);

    /**
     * 绑定设备Id
     *
     * @param pushId 设备Id
     * @return RspModel<AccountRspModel>
     */
    @POST("account/bind.push_id/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind(@Path(encoded = true, value = "pushId") String pushId);

    // 用户更新的接口
    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);

    /**
     * 用户搜索的接口
     * @param parmas
     * {
     *     "search": "测"
     * }
     * @return
     */
    @POST("search/friend")
    Call<RspModel<List<UserCard>>> userSearch(@Body JsonObject parmas);

    // 用户添加好友
    @POST("user/add.friend")
    Call<RspModel> userFollow(@Body JsonObject parmas);

    // 获取联系人列表
    @POST("user/friend.list")
    Call<RspModel<List<UserCard>>> userContacts();

    /**
     * 查询某人的信息
     * @param parmas
     * {
     *     "member_id":1
     * }
     * @return
     */
    @POST("user/others.home.info")
    Call<RspModel<UserCard>> userFind(@Body JsonObject parmas);

    // 发送消息的接口
    @POST("message/push.message")
    Call<RspModel<MessageCard>> msgPush(@Body MsgCreateModel parmas);

    // 创建群
    @POST("chatroom/create.group")
    Call<RspModel<GroupCard>> groupCreate(@Body GroupCreateModel model);

    // 拉取群信息
    @POST("chatroom/group.info")
    Call<RspModel<GroupCard>> groupFind(@Body JsonObject parmas);

    /**
     * 群搜索的接口
     * @param parmas
     * {
     *     "search": "测"
     * }
     * @return
     */
    @POST("search/group")
    Call<RspModel<List<GroupCard>>> groupSearch(@Body JsonObject parmas);

    // 我的群列表
    @GET("chatroom/my.group.list")
    Call<RspModel<List<GroupCard>>> groups();

    // 我的群的成员列表
    @GET("chatroom/group.member/{groupId}")
    Call<RspModel<List<GroupMemberCard>>> groupMembers(@Path("groupId") String groupId);

    // 给群添加成员
    @POST("chatroom/add.group.member")
    Call<RspModel<List<GroupMemberCard>>> groupMemberAdd(@Body GroupMemberAddModel model);

    // 聊天室详情
    @POST("chatroom/get.chatroom.info")
    Call<RspModel<Session>> getChatroomInfo(@Body JsonObject parmas);

    @GET("app/new.version.get")
    Call<RspModel<AppVersionModel>> getAppVersion();
}
