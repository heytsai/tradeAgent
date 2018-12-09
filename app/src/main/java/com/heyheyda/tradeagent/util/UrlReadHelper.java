package com.heyheyda.tradeagent.util;

import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UrlReadHelper {

    public interface OnUrlReadListener {
        void onRead(@NonNull InputStream inputStream) throws IOException;
    }

    public static void readUrlViaHttp(@NonNull URL url, OnUrlReadListener onUrlReadListener) throws IOException {
        final int TIMEOUT_LENGTH = 3000;
        final String CONTENT_TYPE = "text/xml; charset=utf-8";
        final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";

        InputStream inputStream = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            //set HTTP method
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", CONTENT_TYPE);
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setDoInput(true);
            //set timeout
            connection.setReadTimeout(TIMEOUT_LENGTH);
            connection.setConnectTimeout(TIMEOUT_LENGTH);
            //open network traffic
            connection.connect();
            //get response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = new BufferedInputStream(connection.getInputStream());
                if (onUrlReadListener != null) {
                    onUrlReadListener.onRead(inputStream);
                }
            } else {
                throw new IOException("HTTP retrieving error, code: " + responseCode);
            }
        } catch (Exception e) {
            Log.printStackTrace(e);
        } finally {
            //close stream
            if (inputStream != null) {
                inputStream.close();
            }
            //disconnect
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void readUrlViaHttps(@NonNull URL url, OnUrlReadListener onUrlReadListener) throws IOException {
        final int TIMEOUT_LENGTH = 3000;
        final String CONTENT_TYPE = "text/xml; charset=utf-8";
        final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";

        InputStream inputStream = null;
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            //set HTTP method
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", CONTENT_TYPE);
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setDoInput(true);
            //set timeout
            connection.setReadTimeout(TIMEOUT_LENGTH);
            connection.setConnectTimeout(TIMEOUT_LENGTH);
            //open network traffic
            connection.connect();
            //get response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                inputStream = new BufferedInputStream(connection.getInputStream());
                if (onUrlReadListener != null) {
                    onUrlReadListener.onRead(inputStream);
                }
            } else {
                throw new IOException("HTTPS retrieving error, code: " + responseCode);
            }
        } catch (Exception e) {
            Log.printStackTrace(e);
        } finally {
            //close stream
            if (inputStream != null) {
                inputStream.close();
            }
            //disconnect
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
