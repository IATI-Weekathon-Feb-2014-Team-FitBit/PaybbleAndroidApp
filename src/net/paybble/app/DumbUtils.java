package net.paybble.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class DumbUtils {
    public static String readUrlToString(String urlString) {
        StringBuilder lines = new StringBuilder();
        int doRetry = 5;
        while (doRetry-- > 0) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                if (conn.getResponseCode() != 200) {
                    Log.wtf(DumbUtils.class.getName(), "Failed : HTTP error code : " + conn.getResponseCode());
                    Thread.sleep(1000);
                    continue;
                }
                doRetry = 0;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    lines.append(line);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.wtf(DumbUtils.class.getName(), "wtf", e);
            }
        }
        return lines.toString();
    }

    public static <T> String join(Iterable<T> iterable) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (T t : iterable) {
            if (!isFirst) {
                sb.append(",");
                isFirst = false;
            }
            sb.append(t.toString());
        }
        return sb.toString();
    }
}
