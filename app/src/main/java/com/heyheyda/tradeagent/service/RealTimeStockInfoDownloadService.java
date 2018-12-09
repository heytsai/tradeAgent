package com.heyheyda.tradeagent.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.heyheyda.tradeagent.util.Log;
import com.heyheyda.tradeagent.data.RealTimeStockInfo;
import com.heyheyda.tradeagent.data.YahooRealTimeStockInfo;
import com.heyheyda.tradeagent.data.YahooStockApiConstant;
import com.heyheyda.tradeagent.util.UrlReadHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class RealTimeStockInfoDownloadService extends IntentService {

    public static final String KEY_SYMBOL = "symbol";
    public static final String KEY_RECEIVER = "receiver";
    public static final String KEY_RESULT = "result";
    public static final int CODE_SUCCESS = 0;

    private static final String TAG = "RTSIDS";

    public RealTimeStockInfoDownloadService() {
        super(RealTimeStockInfoDownloadService.class.getName());
    }

    private void downloadYahooData(@NonNull String stockSymbol, final ResultReceiver resultReceiver) throws IOException, URISyntaxException {
        String fields = YahooStockApiConstant.NAME
                + YahooStockApiConstant.FIELD_SEPARATION_MARK + YahooStockApiConstant.PRICE
                + YahooStockApiConstant.FIELD_SEPARATION_MARK + YahooStockApiConstant.HIGH
                + YahooStockApiConstant.FIELD_SEPARATION_MARK + YahooStockApiConstant.LOW
                + YahooStockApiConstant.FIELD_SEPARATION_MARK + YahooStockApiConstant.OPEN
                + YahooStockApiConstant.FIELD_SEPARATION_MARK + YahooStockApiConstant.CHANGE
                + YahooStockApiConstant.FIELD_SEPARATION_MARK + YahooStockApiConstant.CHANGE_PERCENT
                + YahooStockApiConstant.FIELD_SEPARATION_MARK + YahooStockApiConstant.VOLUME
                + YahooStockApiConstant.FIELD_SEPARATION_MARK + YahooStockApiConstant.TIME
                + YahooStockApiConstant.FIELD_SEPARATION_MARK + YahooStockApiConstant.CURRENCY
                + YahooStockApiConstant.FIELD_SEPARATION_MARK + YahooStockApiConstant.QUANTITY;

        //get url (String)
        String urlString = String.format(YahooStockApiConstant.REAL_TIME_URL_FORMAT, fields, stockSymbol, YahooStockApiConstant.FORMATTED_FALSE);
        Log.d(TAG, "downloadYahooData: url: " + urlString);

        //handle url with unreliable sources
        URL url = new URL(urlString);
        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());

        //get data
        UrlReadHelper.readUrlViaHttps(uri.toURL(), new UrlReadHelper.OnUrlReadListener() {
            @Override
            public void onRead(@NonNull InputStream inputStream) throws IOException {
                //convert stream to String
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                //read lines
                Log.d(TAG, "downloadYahooData: lines:");
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, line);

                    stringBuilder.append(line);
                }

                //parse data
                RealTimeStockInfo stockInfo;
                if (stringBuilder.length() > 0) {
                    try {
                        // TODO: need more this part into YahooRealTimeStockInfo.parse()
                        JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                        jsonObject = jsonObject.getJSONObject("quoteResponse");
                        jsonObject = (JSONObject) jsonObject.getJSONArray("result").get(0);
                        stockInfo = YahooRealTimeStockInfo.parse(jsonObject);

                        Log.e("RealTimeStockInfo: " + "\n" + stockInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                } else {
                    return;
                }

                //send result back
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_RESULT, stockInfo);
                resultReceiver.send(CODE_SUCCESS, bundle);
            }
        });
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String stockSymbol = intent.getStringExtra(KEY_SYMBOL);
            ResultReceiver resultReceiver = intent.getParcelableExtra(KEY_RECEIVER);
            try {
                downloadYahooData(stockSymbol, resultReceiver);
            } catch (IOException e) {
                Log.printStackTrace(e);
            } catch (URISyntaxException e) {
                Log.printStackTrace(e);
            }
        }
    }
}
