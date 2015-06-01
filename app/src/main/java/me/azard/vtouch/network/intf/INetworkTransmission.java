package me.azard.vtouch.network.intf;

/**
 * Created by Elega on 2014/7/2.
 */
public interface INetworkTransmission {
    //建立服务器
    void startServer(int port);

    //关闭服务器
    void closeServer();

    //为客户端发送信息
    void sendMessage(long cid, String message);

    //得到某客户端的信息
    String getMessage(long cid);

    //得到某客户端的连接状态
    ConnectingState getConnectingState(long cid);

    /**
     * 注册事件
     * @param eventName 事件名，例："Connected"、"Disconnected"、"GetMessage"
     * @param cb 回调函数
     * 成功返回true，失败返回false
     */
    boolean registerEvent(String eventName, IServerCallBack cb);



    public enum ConnectingState {
        unConnected,
        connected,
        failed,
    }
}
