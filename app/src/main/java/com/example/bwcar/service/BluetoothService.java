package com.example.bwcar.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.bwcar.activity.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Administrator on 2017/10/24.
 */

public class BluetoothService  {

    private static final String TAG = "BluetoothService";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //We're doing nothing
    public static final int STATE_NONE = 0;
    //now listening from incoming
    public static final int STATE_LISTEN = 1;
    //now initating an outgoing connection
    public static final int STATE_CONNECTING = 2;
    //now connected to a remote device
    public static final int STATE_CONNECTED = 3;

    private Handler mHandler;
    private Context mContext;
    private int mState;

    //thread
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    public BluetoothService(Context context, Handler handler){
        mContext = context;
        mHandler = handler;
        mState = STATE_NONE;
    }

    class ConnectThread extends Thread{

        private BluetoothDevice mDevice;
        private BluetoothSocket mSocket;

        public ConnectThread(BluetoothDevice device){
            mDevice = device;
            BluetoothSocket tmp = null;

            try {
                tmp = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = tmp;
        }
        @Override
        public void run() {
            Log.i(TAG,"run:begin connect");
            try {
                mSocket.connect();
                mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE,STATE_CONNECTING,-1).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG,"connect faliled",e);
                try {
                    mSocket.close();
                } catch (IOException e1) {
                    Log.e(TAG,"socket close exception",e1);
                }
                connectionFailed();
                return;
            }
            if (mSocket.isConnected()){
                Log.i(TAG, "run:is connected");
                mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE,STATE_CONNECTED,-1).sendToTarget();
            }
            //重置线程
            synchronized (BluetoothService.class){
                mConnectThread = null;
            }

            connected(mDevice,mSocket);

        }

        public void cancel(){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ConnectedThread extends Thread{

        private BluetoothSocket mmSocket;
        private InputStream mInputStream;
        private OutputStream mOutputStream;

        public ConnectedThread(BluetoothSocket socket){

            Log.i(TAG,"ConnectedThread: ");
            mmSocket = socket;
            InputStream tempIs = null;
            OutputStream tempOs = null;
            try {
                tempIs = mmSocket.getInputStream();
                tempOs = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.i(TAG, "获取输入输出流失败！", e);
            }
            mInputStream = tempIs;
            mOutputStream = tempOs;
        }

        @Override
        public void run() {

            Log.i(TAG, "run: connected");
            byte[] buf = new byte[1024];
            int len;

            while (true){

                try {
                    len = mInputStream.read(buf);
                    mHandler.obtainMessage(MainActivity.MESSAGE_READ, len, -1);
                } catch (IOException e) {
                    Log.i(TAG, "读取数据异常！", e);
                    connectionFailed();

                    BluetoothService.this.stop();
                }
            }
        }

        /**
         * 发送数据
         */
        public void write(byte[] buffer){
            try {
                mOutputStream.write(buffer);
                mHandler.obtainMessage(MainActivity.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {

                Log.i(TAG, "Exception during writing", e);
            }
        }

        public void cancel(){

            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.i(TAG, "close of connected socket failed", e);
            }
        }
    }

    private void setState(int state){
        mState = state;
        mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE,state,-1).sendToTarget();
    }

    public synchronized void connected(BluetoothDevice device,BluetoothSocket socket){

        //停止当前在连接设备的线程
        if (mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        //开启线程
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        //将正在通信的设备名称发送到主线程中
        Message message = mHandler.obtainMessage(MainActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.DEVICE_NAME,device.getName());
        message.setData(bundle);
        mHandler.sendMessage(message);

    }

    public void connectionFailed(){
        Message message = mHandler.obtainMessage(MainActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.Toast, "连接失败！");
        message.setData(bundle);
        message.sendToTarget();
    }

    public synchronized void stop(){

        if (mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    /**
     * 连接断开了并通知主线程
     */
    public void connectionLost(){

        Message message = mHandler.obtainMessage(MainActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.Toast, "连接断开，请重新连接!");
        message.setData(bundle);
        message.sendToTarget();
    }
}
