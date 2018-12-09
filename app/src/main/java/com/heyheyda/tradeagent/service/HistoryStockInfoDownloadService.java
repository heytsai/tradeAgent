package com.heyheyda.tradeagent.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.heyheyda.tradeagent.data.HistoryDataRange;
import com.heyheyda.tradeagent.data.HistoryStockInfo;
import com.heyheyda.tradeagent.data.YahooHistoryStockInfo;
import com.heyheyda.tradeagent.data.YahooStockApiConstant;
import com.heyheyda.tradeagent.util.Log;
import com.heyheyda.tradeagent.util.UrlReadHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HistoryStockInfoDownloadService extends IntentService {

    public static final String KEY_SYMBOL = "symbol";
    public static final String KEY_RANGE = "range";
    public static final String KEY_RECEIVER = "receiver";
    public static final String KEY_RESULT = "result";
    public static final int CODE_SUCCESS = 0;

    private static final String TAG = "HSIDS";

    public HistoryStockInfoDownloadService() {
        super(HistoryStockInfoDownloadService.class.getName());
    }

    /**
     * @param dataRange : 1 day, 5 days, 1 month, 6 months, YTD, 1 year, 5 years, max
     */
    private void downloadYahooData(@NonNull String stockSymbol, @NonNull HistoryDataRange dataRange, final ResultReceiver resultReceiver) throws IOException, URISyntaxException {
        //get range & interval
        String range;
        String interval;
        switch (dataRange) {
            case ONE_DAY:
                range = YahooStockApiConstant.HISTORY_RANGE_1_DAY;
                interval = YahooStockApiConstant.HISTORY_INTERVAL_5_MIN;
                break;
            case FIVE_DAYS:
                range = YahooStockApiConstant.HISTORY_RANGE_5_DAY;
                interval = YahooStockApiConstant.HISTORY_INTERVAL_30_MIN;
                break;
            case ONE_MONTH:
                range = YahooStockApiConstant.HISTORY_RANGE_1_MONTH;
                interval = YahooStockApiConstant.HISTORY_INTERVAL_1_DAY;
                break;
            case SIX_MONTHS:
                range = YahooStockApiConstant.HISTORY_RANGE_6_MONTH;
                interval = YahooStockApiConstant.HISTORY_INTERVAL_1_DAY;
                break;
            case YTD:
                range = YahooStockApiConstant.HISTORY_RANGE_YEAR_TO_DATE;
                interval = YahooStockApiConstant.HISTORY_INTERVAL_1_DAY;
                break;
            case ONE_YEAR:
                range = YahooStockApiConstant.HISTORY_RANGE_1_YEAR;
                interval = YahooStockApiConstant.HISTORY_INTERVAL_1_DAY;
                break;
            case FIVE_YEARS:
                range = YahooStockApiConstant.HISTORY_RANGE_5_YEAR;
                interval = YahooStockApiConstant.HISTORY_INTERVAL_1_WEAK;
                break;
            case MAX:
                range = YahooStockApiConstant.HISTORY_RANGE_MAX;
                interval = YahooStockApiConstant.HISTORY_INTERVAL_1_MONTH;
                break;
            default:
                Log.d(TAG, "downloadYahooData: invalid dataRange: " + dataRange);
                return;
        }

        //get url (String)
        String urlString = String.format(YahooStockApiConstant.HISTORY_URL_FORMAT, stockSymbol, interval, range);
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
                HistoryStockInfo stockInfo;
                if (stringBuilder.length() > 0) {
                    stockInfo = YahooHistoryStockInfo.parse(stringBuilder.toString());

                    Log.e("HistoryStockInfo: " + "\n" + stockInfo);
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
            try {
                String stockSymbol = intent.getStringExtra(KEY_SYMBOL);
                HistoryDataRange dataRange = (HistoryDataRange) intent.getSerializableExtra(KEY_RANGE);
                ResultReceiver resultReceiver = intent.getParcelableExtra(KEY_RECEIVER);

                downloadYahooData(stockSymbol, dataRange, resultReceiver);
            } catch (IOException e) {
                Log.printStackTrace(e);
            } catch (URISyntaxException e) {
                Log.printStackTrace(e);
            }
        }
    }
}
