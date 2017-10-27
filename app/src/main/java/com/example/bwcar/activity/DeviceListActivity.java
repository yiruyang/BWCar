package com.example.bwcar.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bwcar.R;
import com.example.bwcar.service.BluetoothService;
import com.example.bwcar.util.Utility;
import com.example.bwcar.widget.TitleLayout;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/10/26.
 */

public class DeviceListActivity extends AppCompatActivity {

    private static final String TAG = "DeviceListActivity";

    //Adapter
    private ArrayAdapter<String> pairedDeviceAdapter;
    private ArrayAdapter<String> newDeviceAdapter;

    private ListView pairedListView, newListView;
    private BluetoothAdapter bluetoothAdapter;

    private static String EXTRA_DEVICE_ADDRESS = "device_address";
        private ProgressDialog progressDialog;
    private TitleLayout titleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        //Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        initView();
        initButton();
        initBroadcastReceiver();
    }


    private void initView(){

        titleBar = (TitleLayout) findViewById(R.id.title_bar);
        titleBar.setTitle("蓝牙列表");

        pairedDeviceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        newDeviceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        pairedListView = (ListView) findViewById(R.id.paired_devices_list);
        pairedListView.setAdapter(pairedDeviceAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        newListView = (ListView) findViewById(R.id.new_devices_list);
        newListView.setAdapter(newDeviceAdapter);
        newListView.setOnItemClickListener(mDeviceClickListener);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //get bondedDevices bluetoothDevices
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);

        if (bondedDevices.size() > 0){
            for (BluetoothDevice device:bondedDevices){
                pairedDeviceAdapter.add(device.getName()+"\n"+device.getAddress());
            }
        }
    }

    private void initButton(){
        findViewById(R.id.scan_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doDiscover();
            }
        });
    }

    private void initBroadcastReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mBroadcastReceiver, filter);
    }

    public void doDiscover(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.wait_Please));
        progressDialog.setMessage(getResources().getString(R.string.scanning));
        progressDialog.setCancelable(false);
        //让“发现新设备”条目出来
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        //界面变化之后让蓝牙设配器重新扫描
        if (bluetoothAdapter.isDiscovering())bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter.startDiscovery();

    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //if
            if (bluetoothAdapter.isDiscovering())bluetoothAdapter.cancelDiscovery();

            String info = ((TextView)view).getText().toString();
            String address = info.substring(info.length()-17);
            //compile intent
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            //execute intent
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    };

    //broadcastReceiver monitor bluetooth state
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            //开始扫描
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
                progressDialog.show();
            }
            //发现设备
            else if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    newDeviceAdapter.add(device.getName()+"\n"+device.getAddress());
                }
            }
            else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){

                progressDialog.dismiss();
                if (newDeviceAdapter.getCount() == 0){
                    Utility.showToast(getApplicationContext(),"No devices founded");
                    return;
                }
                Utility.showToast(getApplicationContext(),"扫描完成！");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
