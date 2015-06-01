package me.azard.vtouch.network.intf;

/**
 * Created by Elega on 2014/7/1.
 */

//类似C++的函数指针、C#的委托，服务器发生事件时会执行回调函数
public interface IServerCallBack {
    void execute(long cid);
}
