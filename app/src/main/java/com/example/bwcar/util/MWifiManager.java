package com.example.bwcar.util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by Administrator on 2017/10/19.
 */

public class MWifiManager {

    //定义WifiManager对象
    private WifiManager mWifiManager;
    //定义WifiInfo对象
    private WifiInfo mWifiInfo;
    //扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    //网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    //定义一个WifiLock
    WifiManager.WifiLock mWifiLock;

    public MWifiManager(Context context){
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    //打开wifi
    public void openWifi(){
        if (!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }
    }

    //关闭wifi
    public void closeWifi(){
        if (mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(false);
        }
    }

    //检测当前wifi状态
    public int checkState(){
        return mWifiManager.getWifiState();
    }

    //锁定当前Wifi状态
    public void acquireWifiLock(){
        mWifiLock.acquire();
    }

    //????解锁WifiLock
    public void releaseWifiLock(){
        if (mWifiLock.isHeld()){
            mWifiLock.release();
        }
    }

    //创建一个wifiLock
    public void createWifiLock(){
        mWifiLock = mWifiManager.createWifiLock("Test");
    }


    //得到配置好的网络
    public List<WifiConfiguration> getConfiguration(){
        return mWifiConfiguration;
    }

    //连接配置好的网络
    public void connectConfiguration(int index){
        //index大于配置好的网络返回Null
        if(index > mWifiConfiguration.size()){
            return;
        }

        //连接配置好的指定id的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    //开始扫描
    public void startScan(){
        mWifiManager.startScan();
        //获取扫描结果
        mWifiList = mWifiManager.getScanResults();
        //得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    //得到网络列表
    public List<ScanResult> getmWifiList(){
        return mWifiList;
    }

    //查看扫描结果
    public StringBuilder lookUpScan(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0;i < mWifiList.size(); i++){
            stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
            stringBuilder.append(mWifiList.get(i).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    //得到MAC地址
    public String getMacAddress(){
        return (mWifiInfo == null)? "null" : mWifiInfo.getMacAddress();
    }

    //得到接入点BBSSID
    public String getBBSSID(){
        return (mWifiInfo == null)? "null" : mWifiInfo.getBSSID();
    }

    //得到IP地址
    public int getIPAddress(){
        return (mWifiInfo == null)? 0 : mWifiInfo.getIpAddress();
    }

    //得到连接的ID
    public int getNetWorkId(){
        return (mWifiInfo == null)? 0 : mWifiInfo.getNetworkId();
    }

    //得到WifiInfo的所有信息包
    public String getWifiInfo(){
        return (mWifiInfo == null)? "null" : mWifiInfo.toString();
    }

    //添加一个网络并连接
    public void addNetwork(WifiConfiguration wc){
        int wcId = mWifiManager.addNetwork(wc);
        mWifiManager.enableNetwork(wcId, true);
    }

    //断开指定ID的网络
    public void disconnectWifi(int netId){
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }
}
