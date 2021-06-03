package net.qiujuer.italker.push;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.igexin.sdk.IUserLoggerInterface;
import com.igexin.sdk.PushManager;

import net.qiujuer.italker.common.app.Application;
import net.qiujuer.italker.factory.Factory;
import net.qiujuer.italker.factory.persistence.Account;
import net.qiujuer.italker.factory.utils.DeviceID;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 调用Factory进行初始化
        Factory.setup();

        // 推送进行初始化
        com.igexin.sdk.PushManager.getInstance().initialize(this);

        com.igexin.sdk.PushManager.getInstance().setDebugLogger(this, new IUserLoggerInterface() {
            @Override
            public void log(String s) {
                Log.i("PUSH_LOG",s);
            }
        });

        // dubug
        Context mContext = this;
        DeviceID.getDeviceId(mContext, new DeviceID.OnDeviceIdListener() {
            @Override
            public void onSuccess(final String deviceId) {
                Log.e("TEST DeviceID", deviceId );
                Account.setDeviceId(deviceId);
            }
        });
    }

    @Override
    protected void showAccountView(Context context) {

        // 登录界面的显示

    }
}
