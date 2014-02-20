package net.paybble.app;

import java.util.UUID;

import android.content.Context;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;

public class PayConfirmationReceiver extends PebbleDataReceiver {
    private static final int PAYBLE_CONFIRM_KEY = 0;
    private Context activityContext;

    protected PayConfirmationReceiver(UUID arg0, Context context) {
        super(arg0);
        this.activityContext = context;
    }

    @Override
    public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
        PebbleKit.sendAckToPebble(context, transactionId);
        int cmd = data.getUnsignedInteger(PAYBLE_CONFIRM_KEY).intValue();
        String paybbleTrx = data.getString(PaybbleConsts.KEY_TRX).trim();
        new PayConfirmationTask(activityContext).execute(new PayRequestVO(paybbleTrx, cmd));
    }
}