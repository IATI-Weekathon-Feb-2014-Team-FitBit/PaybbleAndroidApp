package net.paybble.app;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;

public class MainActivity extends Activity {

    private static final long SCAN_PERIOD = 10000;

    private PebbleKit.PebbleDataReceiver dataReceiver;

    private Handler mHandler;
    private BluetoothAdapter btAdapter;
    private Timer timer = new Timer();
    @SuppressWarnings("unused") private boolean mScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        dataReceiver = new PayConfirmationReceiver(PaybbleConsts.PAYBBLE_WATCHAPP_UUID, getApplicationContext());
        PebbleKit.registerReceivedDataHandler(this, dataReceiver);
        startPayRequestPollingService();
        btAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        timer.schedule(new ScanBleDevicesTask(), 0, SCAN_PERIOD + 5000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void startWatchApp(View view) {
        PebbleKit.startAppOnPebble(getApplicationContext(), PaybbleConsts.PAYBBLE_WATCHAPP_UUID);
    }

    public void stopWatchApp(View view) {
        PebbleKit.closeAppOnPebble(getApplicationContext(), PaybbleConsts.PAYBBLE_WATCHAPP_UUID);
    }

    public void startPayRequestPollingService() {
        payReqServiceCompName = startService(new Intent(this, PayRequestPollingService.class));
        Log.d(MainActivity.class.getName(), "PayRequestPollingService started=" + (payReqServiceCompName != null));
    }

    public void startPayRequestPollingService(View view) {
        startPayRequestPollingService();
    }

    public void stopPayRequestPollingService(View view) {
        boolean state = stopService(new Intent(this, PayRequestPollingService.class));
        Log.d(MainActivity.class.getName(), "PayRequestPollingService stoped=" + state);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btAdapter.stopLeScan(mLeScanCallback);
        unregisterReceiver(dataReceiver);
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), device.getName() + ":" + rssi, Toast.LENGTH_SHORT).show();
                }
            });
            if (rssi > PaybbleConsts.RSSI_CUTOFF) {
                new AnnounceDeviceTask().execute(device);
            }
        }
    };

    private ComponentName payReqServiceCompName;

    private final class ScanBleDevicesTask extends TimerTask {
        @Override
        public void run() {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
            btAdapter.startLeScan(mLeScanCallback);
        }
    }

    //    mHandler.postDelayed(new Runnable() {
    //        @Override
    //        public void run() {
    //            scanBleDevice();
    //        }
    //    }, SCAN_PERIOD);

}
