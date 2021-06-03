package net.qiujuer.italker.factory.model.api;

public class SocketModel {
    public static int Action_GetMessageID  = 0;   // 获取消息ID
    public static int Action_SignIn        = 1;   // 设备登录
    public static int Action_SyncTrigger   = 2;   // 消息同步触发
    public static int Action_Message       = 3;   // 消息
    public static int Action_MessageACK    = 4;   // 消息回执
    public static int Action_Auth          = 10;  // 连接认证
    public static int Action_Quit          = 11;  // 客户端退出
    public static int Action_Headbeat      = 100; // 心跳
}
