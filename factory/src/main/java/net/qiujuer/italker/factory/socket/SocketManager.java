package net.qiujuer.italker.factory.socket;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.qiujuer.italker.factory.model.api.SocketModel;
import net.qiujuer.italker.factory.persistence.Account;
import net.qiujuer.italker.factory.socket.listener.OnConnectionStateListener;
import net.qiujuer.italker.factory.socket.tcp.TCPSocket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by melo on 2017/11/27.
 */

public class SocketManager {
    private static volatile SocketManager instance = null;
    private TCPSocket tcpSocket;
    private Context mContext;

    private SocketManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static SocketManager getInstance(Context context) {
        // if already inited, no need to get lock everytime
        if (instance == null) {
            synchronized (SocketManager.class) {
                if (instance == null) {
                    instance = new SocketManager(context);
                }
            }
        }

        return instance;
    }

    /**
     * 处理 udp 收到的消息
     *
     * @param message
     */
    private void handleUdpMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始 TCP 连接
     */
    public void startTcpConnection() {
        String ip = Config.TCP_IP;
        String port = Config.TCP_PORT;
        if (tcpSocket == null) {// 保证只创建一次
            tcpSocket = new TCPSocket(mContext);
            tcpSocket.startTcpSocket(ip, port);

            tcpSocket.setOnConnectionStateListener(new OnConnectionStateListener() {
                @Override
                public void onSuccess() {// tcp 创建成功
                    JsonObject parmas = new JsonObject();
                    parmas.addProperty("DeviceID", Account.getDeviceId());
                    parmas.addProperty("UserID", Integer.valueOf(Account.getUserId()));
                    parmas.addProperty("AccessToken", Account.getToken());
                    parmas.addProperty("DeviceType", Account.getDevice_type());
                    parmas.addProperty("ClientType", Account.getClient_type());

                    String jsonStr = parmas.toString();
                    tcpSocket.sendTcpMessage(SocketModel.Action_Auth, jsonStr.getBytes());
                    Log.d("TEST SOCKET", "socket已连接");
                }

                @Override

                public void onFailed(int errorCode) { // tcp 异常处理
                    switch (errorCode) {
                        case Config.ErrorCode.CREATE_TCP_ERROR:
                            stopSocket();
                            startTcpConnection();
                            break;
                        case Config.ErrorCode.PING_TCP_TIMEOUT:
                            tcpSocket = null;
                            break;
                    }
                }
            });
        }
    }
    // 发送tcp消息
    public void sendTcpMessage(final int action, final byte[] msg) {
        if (tcpSocket != null ) {
            tcpSocket.sendTcpMessage(action, msg);
        } else {
            startTcpConnection();
        }

    }

    public void stopSocket() {
        tcpSocket.stopTcpConnection();
        if (tcpSocket != null) {
            tcpSocket = null;
        }
        Log.d("TEST SOCKET", "socket已断开连接");
    }

}
