package me.azard.vtouch.protocol.intf;

import me.azard.vtouch.network.intf.IServerCallBack;


public interface IProtocolController {
    //启动Wifi热点和服务器
    void startApaAndServer(final String ssid, final String password, final int latency, final int port, final IStartWifiCallBack success, final IStartWifiCallBack fail);

    //关闭服务器
    void stopServer();

    //关闭Wifi
    void stopWifi();

    //注册事件:如播放声音、调节音量
    boolean registerMusicEvent(String eventName, IProtocolCallBack cb);

    //注册网络事件:如连接成功、断开连接
    boolean registerNetworkEvent(String eventName, IServerCallBack cb);
}
