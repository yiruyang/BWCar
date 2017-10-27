package com.example.bwcar.activity;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import com.example.bwcar.R;
import com.example.bwcar.util.Utility;

public class MainActivity extends AppCompatActivity {

    private static final String TAG =  "MainActivity";

    //Message types sent from the BluetoothService Thread ConnectedThread Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    //adapter
    private BluetoothAdapter mBluetoothAdapter;

    //判定
    private boolean isConnect;
    private boolean isBluetooth;
    private boolean isWifi;

    //bluetooth key
    public static final String Toast= "toast";
    public static final String DEVICE_NAME = "device_name";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBluetooth();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    //初始化bluetooth
    public void initBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null){
            Utility.showToast(getApplicationContext(),"该设备不支持蓝牙！！");
            finish();
            return;
        }
    }

    //发送消息
    public void sendMessage(String msg){

    }


}
