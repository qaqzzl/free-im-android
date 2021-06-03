package net.qiujuer.italker.factory.data.helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.qiujuer.italker.common.Common;
import net.qiujuer.italker.factory.Factory;
import net.qiujuer.italker.factory.R;
import net.qiujuer.italker.factory.data.DataSource;
import net.qiujuer.italker.factory.model.api.RspModel;
import net.qiujuer.italker.factory.model.api.account.AccountRspModel;
import net.qiujuer.italker.factory.model.api.account.AppVersionModel;
import net.qiujuer.italker.factory.model.api.account.LoginModel;
import net.qiujuer.italker.factory.model.api.account.RegisterModel;
import net.qiujuer.italker.factory.model.card.GroupMemberCard;
import net.qiujuer.italker.factory.model.card.UserCard;
import net.qiujuer.italker.factory.model.db.User;
import net.qiujuer.italker.factory.net.Network;
import net.qiujuer.italker.factory.net.RemoteService;
import net.qiujuer.italker.factory.persistence.Account;
import net.qiujuer.italker.factory.presenter.account.LoginContract;
import net.qiujuer.italker.factory.presenter.account.LoginPresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class AccountHelper {

    /**
     * 注册的接口，异步的调用
     *
     * @param model    传递一个注册的Model进来
     * @param callback 成功与失败的接口回送
     */
    public static void register(final RegisterModel model, final DataSource.Callback<User> callback) {
        // 调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        // 得到一个Call
        Call<RspModel<AccountRspModel>> call = service.accountRegister(model);
        // 异步的请求
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 发送短信验证码
     *
     * @param callback 成功与失败的接口回送
     */
    public static void sendSMS(final String phone, String type,final DataSource.Callback<User> callback) {
        JsonObject parmas = new JsonObject();
        parmas.addProperty("phone", phone);
        parmas.addProperty("type", type);
        // 调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        // 得到一个Call
        Call<RspModel> call = service.sendSms(parmas);
        // 异步的请求
        call.enqueue(new Callback<RspModel>() {
            @Override
            public void onResponse(Call<RspModel> call, Response<RspModel> response) {
                RspModel rspModel = response.body();
                if (rspModel.success()) {
                    callback.onDataNotAvailable(R.string.data_net_ok);
                } else {
                    callback.onDataNotAvailable(R.string.data_test_sms_code);
                }
            }

            @Override
            public void onFailure(Call<RspModel> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);

            }
        });
    }

    /**
     * 登录的调用
     *
     * @param model    登录的Model
     * @param callback 成功与失败的接口回送
     */
    public static void login(final LoginModel model, final DataSource.Callback<User> callback) {
        // 调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        // 得到一个Call
        Call<RspModel<AccountRspModel>> call = service.accountLogin(model);
        // 异步的请求
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 对设备Id进行绑定的操作
     *
     * @param callback Callback
     */
    public static void bindPush(final DataSource.Callback<User> callback) {
        // 检查是否为空
        String pushId = Account.getPushId();
        if (TextUtils.isEmpty(pushId))
            return;

        // 调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        Call<RspModel<AccountRspModel>> call = service.accountBind(pushId);
    }


    /**
     * 请求的回调部分封装
     */
    private static class AccountRspCallback implements Callback<RspModel<AccountRspModel>> {

        final DataSource.Callback<User> callback;

        AccountRspCallback(DataSource.Callback<User> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(Call<RspModel<AccountRspModel>> call,
                               Response<RspModel<AccountRspModel>> response) {
            // 请求成功返回
            // 从返回中得到我们的全局Model，内部是使用的Gson进行解析
            RspModel<AccountRspModel> rspModel = response.body();
            if (rspModel.success()) {
                // 拿到实体
                AccountRspModel accountRspModel = rspModel.getData();
                // 获取我的信息
                User user = accountRspModel.getUser();

//                DbHelper.save(User.class, user);

                // 第一种，之间保存
                // user.save();
                /*
                // 第二种通过ModelAdapter
                FlowManager.getModelAdapter(User.class)
                        .save(user);

                // 第三种，事务中
                DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
                definition.beginTransactionAsync(new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        FlowManager.getModelAdapter(User.class)
                                .save(user);
                    }
                }).build().execute();
                */
                // 同步到XML持久化中
                 Account.login(accountRspModel);

                callback.onDataLoaded(user);

                // 判断绑定状态，是否绑定设备
                if (false) {
                    // 设置绑定状态为True
                    Account.setBind(true);
                    // 然后返回
                    if (callback != null)
                        callback.onDataLoaded(user);
                } else {
                    // 进行绑定的唤起
                    bindPush(callback);
                    Account.setBind(true);
                }
            } else {
                // 错误解析
                Factory.decodeRspCode(rspModel, callback);
            }
        }

        @Override
        public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
            // 网络请求失败
            if (callback != null)
                callback.onDataNotAvailable(R.string.data_network_error);
        }
    }

    /**
     * 检查应用更新
     */
    public static void checkingAppUpdate(final UpdateHelper mUpdateHelper) {
        // 调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        // 得到一个Call
        Call<RspModel<AppVersionModel>> call = service.getAppVersion();
        // 异步的请求
        call.enqueue(new Callback<RspModel<AppVersionModel>>() {
            @Override
            public void onResponse(Call<RspModel<AppVersionModel>> call, Response<RspModel<AppVersionModel>> response) {
                RspModel<AppVersionModel> rspModel = response.body();
                if (rspModel.success()) {
                    AppVersionModel appVersionModel = rspModel.getData();
                    int version_code = Common.Constance.APP_VERSION_CODE;
                    if (version_code < appVersionModel.getVersion_code()) {
                        mUpdateHelper.updateApp(appVersionModel);
                    }
                }
            }

            @Override
            public void onFailure(Call<RspModel<AppVersionModel>> call, Throwable t) {

            }
        });
    }
}
