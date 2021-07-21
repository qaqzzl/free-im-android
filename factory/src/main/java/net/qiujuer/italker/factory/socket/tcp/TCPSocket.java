package net.qiujuer.italker.factory.socket.tcp;

import android.content.Context;
import android.util.Log;

import net.qiujuer.italker.factory.model.api.SocketModel;
import net.qiujuer.italker.factory.socket.Config;
import net.qiujuer.italker.factory.socket.HeartbeatTimer;
import net.qiujuer.italker.factory.socket.listener.OnConnectionStateListener;
import net.qiujuer.italker.factory.socket.logic.SocketLogic;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by melo on 2017/11/28.
 */

public class TCPSocket {
    private static final String TAG = "TEST TCPSocket";
    private static int sequenceId;

    private Context mContext;
    private ExecutorService mThreadPool;
    private Socket mSocket;
    private BufferedInputStream bis;
    private PrintWriter pw;
    private HeartbeatTimer timer;
    private HeartbeatTimer timerACK;
    private long lastReceiveTime = 0;
    private int HeadbeatTime = getSecondTimestampTwo();
    private OnConnectionStateListener mListener;

    private static final long TIME_OUT = 15 * 1000;
    private static final long HEARTBEAT_MESSAGE_DURATION = 2 * 5000;

    /**
     * 获取精确到秒的时间戳
     * @return
     */
    public static int getSecondTimestampTwo(){
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime()/1000);
        return Integer.valueOf(timestamp);
    }

    public TCPSocket(Context context) {
        this.mContext = context;

        int cpuNumbers = Runtime.getRuntime().availableProcessors();
        // 根据CPU数目初始化线程池
        mThreadPool = Executors.newFixedThreadPool(cpuNumbers * Config.POOL_SIZE);
        // 记录创建对象时的时间
        lastReceiveTime = System.currentTimeMillis();
    }

    public void startTcpSocket(final String ip, final String port) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (startTcpConnection(ip, Integer.valueOf(port))) {// 尝试建立 TCP 连接
                    if (mListener != null) {
                        mListener.onSuccess();
                    }
                    startReceiveTcpThread();
                    // 启动心跳
                    startHeartbeatTimer();

                    // 心跳检测
                    Heartbeat();
                } else {
                    if (mListener != null) {
                        mListener.onFailed(Config.ErrorCode.CREATE_TCP_ERROR);
                    }
                }
            }
        });
    }

    public void setOnConnectionStateListener(OnConnectionStateListener listener) {
        this.mListener = listener;
    }

    /**
     * 创建接收线程
     */
    private void startReceiveTcpThread() {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (bis == null ) {
                            continue;
                        }
                        byte[] buffer = new byte[2048];
                        byte[] tmp_buffer = {};
                        int mActualReadSize = 0;
                        int readSize = 0;
                        //接受服务端消息
                        while((readSize = bis.read(buffer)) != -1) {
                            mActualReadSize = 0;
                            Log.d(TAG, "接收 buffer：" + readSize);
                            if (tmp_buffer == null || tmp_buffer.length == 0) {
                                tmp_buffer = MessageWhile(buffer, readSize, mActualReadSize);
                            } else {
                                Log.d(TAG, "接收 tmp_buffer：" + tmp_buffer.length);
                                byte[] data = new byte[tmp_buffer.length + buffer.length];
                                System.arraycopy(tmp_buffer, 0, data, 0, tmp_buffer.length);
                                System.arraycopy(buffer, 0, data, tmp_buffer.length, buffer.length);
                                tmp_buffer = MessageWhile(data, tmp_buffer.length+readSize, mActualReadSize);
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     *
     * @param buffer
     * @param readSize
     * @param startReadSize
     * @return buffer
     */
    private byte[] MessageWhile(byte[] buffer, int readSize, int startReadSize) {
        int headInt = 13;
        int frameSize = 0;
        int action = 0;
        if( readSize >= headInt ){
            frameSize = ( (buffer[startReadSize + 9] & 0xff) << 24 | (buffer[ startReadSize + 10] & 0xff) << 16 | (buffer[ startReadSize + 11] & 0xff) << 8 | (buffer[ startReadSize + 12] & 0xff) );
            action = (buffer[startReadSize + 4] & 0xff);
        } else {
            byte[] tmpRecvBuffer = new byte[readSize];
            System.arraycopy(buffer, startReadSize, tmpRecvBuffer, 0, readSize);
            return tmpRecvBuffer;
        }
        if (readSize < frameSize + headInt) {
            byte[] tmpRecvBuffer = new byte[readSize];
            System.arraycopy(buffer, startReadSize, tmpRecvBuffer, 0, readSize);
            return tmpRecvBuffer;
        }
        Log.d(TAG, "接收 readSize：" + readSize);
        Log.d(TAG, "接收 frameSize + headInt：" + (frameSize + headInt));
        byte[] mRecvBuffer = new byte[frameSize + headInt];
        System.arraycopy(buffer, startReadSize, mRecvBuffer, 0, frameSize + headInt);

        String str = new String(mRecvBuffer, headInt, frameSize);
        // 消息处理
        Log.d(TAG, "接收 str：" + str);
        handleReceiveTcpMessage(action, str);

        startReadSize += frameSize + headInt;
        int surplusLength = readSize - (frameSize + headInt);
        Log.d(TAG, "接收 startReadSize：" + startReadSize);
        Log.d(TAG, "接收 surplusLength：" + surplusLength);
        if( surplusLength > 0 ){
            return MessageWhile(buffer, surplusLength, startReadSize);
        }
        return null;
    }

    /**
     * 处理 tcp 收到的消息
     *
     * @param line
     */
    private void handleReceiveTcpMessage(int action, String line) {
        Log.d(TAG, "接收 action：" + action);
        HeadbeatTime = getSecondTimestampTwo();
        if (action == SocketModel.Action_Headbeat) {
            return;
        }
        SocketLogic.getInstance(mContext).MessageHandle(action ,line);
        lastReceiveTime = System.currentTimeMillis();
    }

    public void sendTcpMessage(final int action, final byte[] msg) {
        if (action == 3) {
            sequenceId++;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream outputStream = mSocket.getOutputStream();
                    if (outputStream != null) {
                        String s = new String(msg);
                        Log.e(TAG, "发送：action="+Integer.valueOf(action).toString()+"，content: "+s);
                        int mesLen = msg.length;
                        byte[] array = new byte[13+mesLen];

                        // 版本
                        int version = 100000;
                        array[0] = (byte)(version>>24);
                        array[1] = (byte)(version>>16);
                        array[2] = (byte)(version>>8);
                        array[3] = (byte)version;

                        // 方法
                        array[4] = (byte)action;

                        // 请求ID
                        array[5] = (byte)(sequenceId>>24);
                        array[6] = (byte)(sequenceId>>16);
                        array[7] = (byte)(sequenceId>>8);
                        array[8] = (byte)sequenceId;

                        // 内容大小
                        array[9] = (byte)(mesLen>>24);
                        array[10] = (byte)(mesLen>>16);
                        array[11] = (byte)(mesLen>>8);
                        array[12] = (byte)mesLen;
                        System.arraycopy(msg, 0, array, 13, mesLen);
                        outputStream.write(array);
                        outputStream.flush();
                    }
                } catch (IOException e) {
//                    /*发送失败说明socket断开了或者出现了其他错误*/
                    Log.e(TAG, "连接断开，正在重连");
//                    /*重连*/
                    mListener.onFailed(Config.ErrorCode.CREATE_TCP_ERROR);
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * 启动心跳
     */
    private void startHeartbeatTimer() {
        if (timer == null) {
            timer = new HeartbeatTimer();
        }
        timer.setOnScheduleListener(new HeartbeatTimer.OnScheduleListener() {
            @Override
            public void onSchedule() {
                // 发送心跳消息
                byte[] msg = new byte[0];
                sendTcpMessage(Config.PING, msg);
            }

        });
        timer.startTimer(0, 1000 * 5);
    }

    public void stopHeartbeatTimer() {
        if (timer != null) {
            timer.exit();
            timer = null;
        }
        if (timerACK != null) {
            timerACK.exit();
            timerACK = null;
        }
    }

    // 心跳检测
    private void Heartbeat() {
        if (timerACK == null) {
            timerACK = new HeartbeatTimer();
        }
        timerACK.setOnScheduleListener(new HeartbeatTimer.OnScheduleListener() {
            @Override
            public void onSchedule() {
                if ((HeadbeatTime + 7) <  getSecondTimestampTwo() ) {
                    /*发送失败说明socket断开了或者出现了其他错误*/
                    Log.d(TAG, "心跳超时，正在重连");
                    /*重连*/
                    mListener.onFailed(Config.ErrorCode.CREATE_TCP_ERROR);
                }
            }

        });
        timerACK.startTimer(0, 1000 * 5);
    }

    /**
     * 尝试建立tcp连接
     *
     * @param ip
     * @param port
     */
    private boolean startTcpConnection(final String ip, final int port) {
        try {
            if (mSocket == null) {
                mSocket = new Socket(ip, port);
                mSocket.setKeepAlive(true);
                mSocket.setTcpNoDelay(true);
                mSocket.setReuseAddress(true);
            }
            bis = new BufferedInputStream(mSocket.getInputStream());
            OutputStream os = mSocket.getOutputStream();
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)), true);
            Log.d(TAG, "tcp 创建成功...");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void releaseSocket() {
        stopTcpConnection();
        TCPSocket TCPSocket = new TCPSocket(mContext);
        TCPSocket.startTcpSocket(Config.TCP_IP, Config.TCP_PORT);
    }

    public void stopTcpConnection() {
        try {
            stopHeartbeatTimer();
            if (bis != null) {
                bis.close();
            }
            if (pw != null) {
                pw.close();
            }
            if (mThreadPool != null) {
                mThreadPool.shutdown();
                mThreadPool = null;
            }
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
