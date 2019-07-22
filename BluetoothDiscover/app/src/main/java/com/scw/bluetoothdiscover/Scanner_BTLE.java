package com.scw.bluetoothdiscover;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by SCW on 2018/10/8.
 */
public class Scanner_BTLE {
    private Activity mActivity;
    private BluetoothAdapter mBluetoothAdapter;
    private HashMap<String, List<Integer>> mBTDevicesHashMap;
    private boolean mScanning = false;


    private int signalStrength;


    public Scanner_BTLE(Activity mainActivity, int signalStrength) {
        mActivity = mainActivity;

        this.signalStrength = signalStrength;
        final BluetoothManager bluetoothManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBTDevicesHashMap = new HashMap<>();

    }

    public boolean isScanning() {
        return mScanning;
    }

    public void start() {
        mScanning = true;
        mBluetoothAdapter.startLeScan(mLeScanCallback);

    }

    public void stop() {
        mScanning = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);

    }

    public void clean() {
        mBTDevicesHashMap.clear();

    }

    public HashMap<String, Integer> result() {
        Iterator iter = mBTDevicesHashMap.entrySet().iterator();
        HashMap<String, Integer> resultList = new HashMap<>();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            List<Integer> value = (List<Integer>) entry.getValue();
            int sum = 0;
            for (int i = 0; i < value.size(); i++) {
                sum += value.get(i);
            }
            int aver = sum / value.size();
            resultList.put(key, aver);
            System.out.println("Test  result " + key + " " + value + " " + aver);

        }
        return resultList;

    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    final int new_rssi = rssi;
                    if (rssi > signalStrength) {
                        addDevice(device, new_rssi);
                        /*
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                addDevice(device, new_rssi);
                            }
                        });
                        */
                    }
                }
            };

    private void addDevice(BluetoothDevice device, int new_rssi) {

        String address = device.getAddress();

        if (!mBTDevicesHashMap.containsKey(address)) {
            //BluetoothDevice btle_device = new BluetoothDevice(device);
            //btle_device.setRSSI(new_rssi);
            List<Integer> putlist = new ArrayList<>();
            putlist.add(new_rssi);
            mBTDevicesHashMap.put(address, putlist);

        } else {
            mBTDevicesHashMap.get(address).add(new_rssi);
        }

    }
}

