package net.paybble.app;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PayRequestPollingService extends IntentService {
    private static final String EMPTY_RESPONSE = "[]";
    private Gson gson = new Gson();

    public PayRequestPollingService() {
        super(PayRequestPollingService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //        String urlString = "http://paybble.azure-mobile.net/api/devicepromptforpayment?deviceid="
        //                + PaybbleConsts.DEVICE_ID;
        String urlString = "http://paybble.azure-mobile.net/Tables/Transactions";
        while (true) {
            TransactionRepository.getInstance().mutex.acquireUninterruptibly();
            String result = DumbUtils.readUrlToString(urlString);
            TransactionRepository.getInstance().mutex.release();
            if (EMPTY_RESPONSE.equals(result)) {
                sleepUninterrupted(3000);
                continue;
            }
            List<PayRequestVO> trxs = gson.fromJson(result, new TypeToken<List<PayRequestVO>>() {
            }.getType());
            if (trxs == null || trxs.size() == 0) {
                sleepUninterrupted(3000);
                continue;
            }
            PayRequestVO trx = findReqToProcess(trxs);
            if (trx == null) {
                sleepUninterrupted(3000);
                continue;
            }
            if (!TransactionRepository.getInstance().contains(trx.id)) {
                TransactionRepository.getInstance().addTrx(trx.id);
                sendDataToPebble(1, trx);
            }
        }
    }

    private PayRequestVO findReqToProcess(List<PayRequestVO> trxs) {
        PayRequestVO toProcess = null;
        for (PayRequestVO payRequestVO : trxs) {
            if (payRequestVO.status == -1) {
                toProcess = payRequestVO;
                break;
            }
        }
        return toProcess;
    }

    private void sendDataToPebble(int key, PayRequestVO payRequestVO) {
        PebbleKit.startAppOnPebble(getApplicationContext(), PaybbleConsts.PAYBBLE_WATCHAPP_UUID);
        sleepUninterrupted(1000);
        PebbleDictionary data = new PebbleDictionary();
        data.addString(PaybbleConsts.KEY_PLACE, "Emily's Cafe"); // "Star Bucks" payRequestVO.description1
        data.addString(PaybbleConsts.KEY_AMMOUNT, String.format("%.2f $", payRequestVO.amount)); // "3.99 $"
        data.addString(PaybbleConsts.KEY_TRX, payRequestVO.id); //"trx_0001"
        PebbleKit.sendDataToPebble(getApplicationContext(), PaybbleConsts.PAYBBLE_WATCHAPP_UUID, data);
    }

    private static void sleepUninterrupted(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Log.wtf(PayRequestPollingService.class.getName(), "wtf", e);
        }
    }

}
