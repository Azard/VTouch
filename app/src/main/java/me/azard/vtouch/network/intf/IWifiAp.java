package me.azard.vtouch.network.intf;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Elega on 2014/7/2.
 */
public interface IWifiAp {
    //关闭Wifi热点
    void closeWifiAp(Context context);

    /**
     * 创建Wifi热点
     * @param ssid  网络名
     * @param passwd 密码
     * @param latency 等待延迟(毫秒)，超过返回就失败
     * 成功返回true，失败返回false
     */
    boolean startWifiAp(String ssid, String passwd, int latency);

    //返回连接到此Wifi的IP地址
    ArrayList<String> getConnectedIP();
}
