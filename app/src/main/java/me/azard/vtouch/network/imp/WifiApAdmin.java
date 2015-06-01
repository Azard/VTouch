package me.azard.vtouch.network.imp;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import me.azard.vtouch.network.intf.IWifiAp;

/**
 * 创建热点
 *
 */
public class WifiApAdmin implements IWifiAp {
    public static final String TAG = "WifiApAdmin";

    //关闭Wifi热点
    @Override
    public  void closeWifiAp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        closeWifiAp(wifiManager);
    }

    private WifiManager mWifiManager = null;

    public static boolean result;

    private Context mContext = null;
    public WifiApAdmin(Context context) {
        mContext = context;

        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        closeWifiAp(mWifiManager);
    }

    private String mSSID = "";
    private String mPasswd = "";


    /**
     * 创建Wifi热点
     * @param ssid  网络名
     * @param passwd 密码
     * @param latency 等待延迟(毫秒)，超过返回就失败
     * 成功返回true，失败返回false
     */
    @Override
    public boolean startWifiAp(String ssid, String passwd, int latency) {
        mSSID = ssid;
        mPasswd = passwd;
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }

        activationWifiAp();

        MyTimerCheck timerCheck = new MyTimerCheck(result) {

            @Override
            public void doTimerCheckWork() {
                if (isWifiApEnabled(mWifiManager)) {
                    WifiApAdmin.result = true;

                    this.exit();
                } else {
                    WifiApAdmin.result = false;
                    Log.v(TAG, "have connected failed!");
                }
            }

            @Override
            public void doTimeOutWork() {
                WifiApAdmin.result = false;
                this.exit();
            }
        };
        timerCheck.start(latency);
        timerCheck.waitStop();
        return result;

    }

    private void activationWifiAp() {
        Method method1 = null;
        try {
            method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();

            netConfig.SSID = mSSID;
            netConfig.preSharedKey = mPasswd;

            netConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            netConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);

            method1.invoke(mWifiManager, netConfig, true);

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void closeWifiAp(WifiManager wifiManager) {
        if (isWifiApEnabled(wifiManager)) {
            try {
                Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);

                WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);

                Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method2.invoke(wifiManager, config, false);
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private static boolean isWifiApEnabled(WifiManager wifiManager) {
        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);

        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


//返回连接到此Wifi的IP地址
    @Override
    public ArrayList<String> getConnectedIP() {
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
//                Log.v(TAG, line);
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && splitted[0].length()>2) {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectedIP;
    }

}