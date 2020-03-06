package com.easing.commons.android.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BluetoothListener extends BroadcastReceiver {

    private Context ctx;
    private Callback callback;
    private BluetoothAdapter adapter;
    private List<BluetoothDevice> deviceList;
    private Map<String, BluetoothDevice> deviceMap;
    private boolean singleDiscovery;
    private String targetAddress;

    public static BluetoothListener startDiscovery(Context ctx, Callback callback) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.device.action.FOUND");
        filter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        BluetoothListener listener = new BluetoothListener();
        ctx.registerReceiver(listener, filter);
        listener.ctx = ctx;
        listener.callback = callback;
        listener.adapter = BluetoothAdapter.getDefaultAdapter();
        listener.deviceList = new ArrayList();
        listener.deviceMap = new HashMap();
        listener.singleDiscovery = false;
        listener.targetAddress = null;
        if (!listener.adapter.isEnabled())
            listener.adapter.enable();
        listener.adapter.startDiscovery();
        return listener;
    }

    public static BluetoothListener startDiscovery(String targetAddress, Context ctx, Callback callback) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.device.action.FOUND");
        filter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        BluetoothListener listener = new BluetoothListener();
        ctx.registerReceiver(listener, filter);
        listener.ctx = ctx;
        listener.callback = callback;
        listener.adapter = BluetoothAdapter.getDefaultAdapter();
        listener.deviceList = new ArrayList();
        listener.deviceMap = new HashMap();
        listener.singleDiscovery = true;
        listener.targetAddress = targetAddress;
        if (!listener.adapter.isEnabled())
            listener.adapter.enable();
        listener.adapter.startDiscovery();
        return listener;
    }

    public static Map<String, BluetoothDevice> getBoundBluetooths() {
        Map<String, BluetoothDevice> deviceMap = new HashMap();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        for (BluetoothDevice device : devices)
            deviceMap.put(device.getAddress(), device);
        return deviceMap;
    }

    public static boolean isDeviceBound(String deviceAddress) {
        return BluetoothListener.getBoundBluetooths().containsKey(deviceAddress);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.bluetooth.device.action.FOUND")) {
            //添加设备
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (!singleDiscovery || device.getAddress().equalsIgnoreCase(targetAddress)) {
                deviceList.add(device);
                deviceMap.put(device.getAddress(), device);
            }
            //停止搜索
            if (singleDiscovery && device.getAddress().equalsIgnoreCase(targetAddress))
                adapter.cancelDiscovery();
        }

        if (intent.getAction().equals("android.bluetooth.adapter.action.DISCOVERY_FINISHED")) {
            //解注册
            context.unregisterReceiver(this);
            adapter.cancelDiscovery();
            //执行回调
            if (callback != null)
                callback.onDiscoveryFinish(deviceList, deviceMap, adapter);
        }
    }

    public interface Callback {
        void onDiscoveryFinish(List<BluetoothDevice> deviceList, Map<String, BluetoothDevice> deviceMap, BluetoothAdapter adapter);
    }
}
