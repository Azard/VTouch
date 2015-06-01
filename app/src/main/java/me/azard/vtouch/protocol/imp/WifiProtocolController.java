package me.azard.vtouch.protocol.imp;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import me.azard.vtouch.R;
import me.azard.vtouch.network.imp.AdHoc;
import me.azard.vtouch.network.imp.WifiApAdmin;
import me.azard.vtouch.network.intf.INetworkTransmission;
import me.azard.vtouch.network.intf.IServerCallBack;
import me.azard.vtouch.network.intf.IWifiAp;
import me.azard.vtouch.protocol.intf.IProtocolCallBack;
import me.azard.vtouch.protocol.intf.IProtocolController;
import me.azard.vtouch.protocol.intf.IStartWifiCallBack;


public class WifiProtocolController implements IProtocolController {

    Map<String,IProtocolCallBack> mEventMap=new HashMap<String,IProtocolCallBack>();
    Map<String,String> mInstrMap=new HashMap<String,String>();
    Map<Long,String> mBufMap=new HashMap<Long, String>();
    Context mContext;
    IWifiAp wifiAp;
    INetworkTransmission adhoc;

    int mMessageLength=-1;
    int mSerialNumberDigits=-1;
    int mInstructionDigits=-1;
    int mArgumentDigits=-1;

    public WifiProtocolController(Context context){
        mContext=context;
        wifiAp=new WifiApAdmin(context);
        adhoc=new AdHoc();
        adhoc.registerEvent("GetMessage",new IServerCallBack() {
                    @Override
                    public void execute(long cid) {
                        getMessage(cid);
                    }
                });
        readConfig();
    }

    //启动Wifi热点和服务器
    @Override
    public void startApaAndServer(final String ssid, final String password, final int latency, final int port,final IStartWifiCallBack success,final IStartWifiCallBack fail) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (wifiAp.startWifiAp(ssid,password,latency)) {
                    adhoc.startServer(port);
                    if (success!=null)
                        success.execute();
                }
                else {
                    if (fail != null)
                        fail.execute();
                }
            }

        });
        thread.start();
    }

    @Override
    public void stopServer() {
        adhoc.closeServer();
    }

    @Override
    public void stopWifi() {
        wifiAp.closeWifiAp(mContext);
    }

    //注册事件:如播放声音、调节音量
    @Override
    public boolean registerMusicEvent(String eventName, IProtocolCallBack cb){
        mEventMap.put(eventName, cb);
        return true;
    }

    //注册网络事件:如连接成功、断开连接
     @Override
     public boolean registerNetworkEvent(String eventName, IServerCallBack cb){
         return adhoc.registerEvent(eventName,cb);
     }

    private void readConfig(){
        InputStream is=mContext.getResources().openRawResource(R.raw.ptccfg);
        BufferedReader br=new BufferedReader(new InputStreamReader(is));
        String str=null;
        try{
            while ((str=br.readLine())!=null){
                if (str.length()>0 && str.indexOf("//")==-1 ){
                    try {
                        if (str.indexOf("int:") == 0 || str.indexOf("str:") == 0) {
                            String tmp = str.substring(4);
                            String lhs = tmp.split("=")[0];
                            String rhs = tmp.split("=")[1];
                            Field field = this.getClass().getDeclaredField(lhs);//name为类Instance中的private属性
                            field.setAccessible(true);
                            if (str.indexOf("int:") == 0)
                                field.set(this, Integer.valueOf(rhs));
                            else
                                field.set(this, rhs);
                            field.setAccessible(false);
                            Log.d("AzardDebug", "set " + lhs + "=" + rhs);
                        }
                        else if(str.indexOf("inst:")==0){
                            String tmp = str.substring(5);
                            String lhs = tmp.split("=")[0];
                            String rhs = tmp.split("=")[1];
                            mInstrMap.put(lhs,rhs);
                            Log.d("AzardDebug", "add key:" + lhs + " value:" + rhs);

                        }
                    }
                    catch (IndexOutOfBoundsException e){
                        Log.v("Error", "配置文件格式出错 行:" + str);
                    }
                    catch (NoSuchFieldException e){
                        Log.v("Error", "没有找到字段 行:" + str);
                    }
                    catch (IllegalAccessException e){
                        Log.v("Error", "变量类型出错 行:" + str);
                    }

                }
            }
            is.close();
            br.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    private class Message {
        public Message(int _serialNumber, String _instruction, String _argument) {
            serialNumber = _serialNumber;
            instruction = _instruction;
            argument = _argument;
        }
        public int serialNumber;
        public String instruction;
        public String argument;
    }

    private  void getMessage(Long cid){
        String s=adhoc.getMessage(cid);
        if (mBufMap.containsKey(cid)){
            mBufMap.put(cid,mBufMap.get(cid)+s);
        }
        else{
            mBufMap.put(cid,s);
        }
        try {
            handleMessage(cid);
        }
        catch(Exception e){
            mBufMap.put(cid,"");
        }
    }

    private void handleMessage(Long cid){
        String buf=mBufMap.get(cid);
        while (buf!=null && buf.length()>=mMessageLength){
            String tmp=buf.substring(0,mMessageLength);
            buf=buf.substring(mMessageLength);
            mBufMap.put(cid,buf);
            Message message=parseMessage(cid,tmp);
            postMessage(cid,message);
        }
    }

    private Message parseMessage(Long cid,String message){
        if (message.length()==mMessageLength){
            String tmp=message.substring(0,mSerialNumberDigits);
            int sn= Integer.valueOf(tmp);
            message=message.substring(mSerialNumberDigits);
            String ins=message.substring(0,mInstructionDigits );
            message=message.substring(mInstructionDigits);
            String arg=message.substring(0,mArgumentDigits);

            return new Message(sn,ins,arg);
        }
        else
            return null;
    }

    private void postMessage(final Long cid, final Message message){
        Log.d("AzardDebug", "sn " + message.serialNumber + " inst " + message.instruction + " arg " + message.argument);
        String opr= mInstrMap.get(message.instruction);
        final IProtocolCallBack cb=mEventMap.get(opr);
        if (cb!=null){
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    cb.execute(cid,message.argument);
                }
            });
            thread.start();
        }
    }


}
