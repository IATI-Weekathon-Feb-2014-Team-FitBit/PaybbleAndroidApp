package net.paybble.app;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

class AnnounceDeviceTask extends AsyncTask<BluetoothDevice, Void, Void> {

    @Override
    protected Void doInBackground(BluetoothDevice... devices) {
        //        BluetoothDevice device = devices[0];
        StringBuilder sb = new StringBuilder("http://paybble.azure-mobile.net/api/deviceannounce");
        sb.append("?").append("devicename=").append(PaybbleConsts.DEVICE_ID);
        //FIXME: cloud bug is here. ble should be device id
        sb.append("&").append("ble=").append("lobby"); // device.getName()
        sb.append("&").append("username=").append("Barak%20Gitsis");
        DumbUtils.readUrlToString(sb.toString());
        return null;
    }
}