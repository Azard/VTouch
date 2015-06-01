package me.azard.vtouch.network.imp;

import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import me.azard.vtouch.network.intf.INetworkTransmission;

/**
 * Created by Elega on 2014/7/9.
 */

public class Client{
    public String ipAddr;
    public int port;
    public long cid;
    public Socket socket;
    public String readBuf;
    public String writeBuf;
    public ReentrantLock readBufLock;
    public ReentrantLock writeBufLock;
    public INetworkTransmission.ConnectingState connectingState;
    public Client(String _ipAddr,int _port,long _cid,Socket _socket){
        ipAddr=_ipAddr;
        port=_port;
        cid=_cid;
        socket=_socket;
        connectingState= INetworkTransmission.ConnectingState.unConnected;
        readBufLock=new ReentrantLock(true);
        writeBufLock=new ReentrantLock(true);
        readBuf="";
        writeBuf="";
    }

}
