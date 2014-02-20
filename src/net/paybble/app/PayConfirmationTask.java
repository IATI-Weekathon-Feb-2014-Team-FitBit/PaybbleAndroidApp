package net.paybble.app;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class PayConfirmationTask extends AsyncTask<PayRequestVO, Void, String> {
    private static final String BASE_CONFIRMATION = "http://paybble.azure-mobile.net/api/deviceconfirmpayment?transactionid=";
    private static final int PAYBLE_CONFIRM_YES = 1;
    private static final int PAYBLE_CONFIRM_NO = 0;

    private Context context;

    public PayConfirmationTask(Context activityContext) {
        this.context = activityContext;
    }

    @Override
    protected String doInBackground(PayRequestVO... trxs) {
        TransactionRepository.getInstance().mutex.acquireUninterruptibly();
        String trx = trxs[0].id;
        String urlString = BASE_CONFIRMATION + trx + "&status=";
        switch (trxs[0].status) {
        case PAYBLE_CONFIRM_NO:
            urlString = urlString + 0;
            break;
        case PAYBLE_CONFIRM_YES:
            urlString = urlString + 1;
            break;
        }
        String result = DumbUtils.readUrlToString(urlString);
        TransactionRepository.getInstance().removeTrx(trx);
        TransactionRepository.getInstance().mutex.release();
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        String toastMessage;
        if ("ok".equalsIgnoreCase(result)) {
            toastMessage = "sent payment confirmation";
        } else {
            toastMessage = result;
        }
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

}
