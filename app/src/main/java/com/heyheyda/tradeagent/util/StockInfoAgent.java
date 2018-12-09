package com.heyheyda.tradeagent.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.heyheyda.tradeagent.data.HistoryDataRange;
import com.heyheyda.tradeagent.data.HistoryStockInfo;
import com.heyheyda.tradeagent.data.RealTimeStockInfo;
import com.heyheyda.tradeagent.service.HistoryStockInfoDownloadService;
import com.heyheyda.tradeagent.service.RealTimeStockInfoDownloadService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockInfoAgent {

    public static final String BROADCAST_REAL_TIME_INFO_UPDATED = "com.heyheyda.tradeagent.REAL_TIME_INFO_UPDATED";
    public static final String BROADCAST_HISTORY_INFO_UPDATED = "com.heyheyda.tradeagent.HISTORY_INFO_UPDATED";

    private static final String STORAGE_KEY_INTERESTED_REAL_TIME_STOCKS = "real_time_stock_symbols";
    private static final String TAG = "SIA";

    private static final Object lock = new Object();
    private static StockInfoAgent instance = null;

    private Map<String, RealTimeStockInfo> realTimeStockInfoMap;
    private Map<String, Map<HistoryDataRange, HistoryStockInfo>> historyStockInfoMap;
    private List<String> interestedRealTimeStockSymbolList;

    public static StockInfoAgent getInstance(@NonNull Context context) {
        StockInfoAgent inst = instance;
        if (inst == null) {
            synchronized (lock) {
                inst = instance;
                if (inst == null) {
                    inst = new StockInfoAgent(context);
                    instance = inst;
                }
            }
        }
        return inst;
    }

    private StockInfoAgent(@NonNull Context context) {
        this.realTimeStockInfoMap = new HashMap<>();
        this.historyStockInfoMap = new HashMap<>();
        this.interestedRealTimeStockSymbolList = new ArrayList<>();

        //real from storage (if exist)
        try {
            List<String > symbolList = JsonDataStorage.readFromFile(STORAGE_KEY_INTERESTED_REAL_TIME_STOCKS, List.class, context);
            if (symbolList != null) {
                this.interestedRealTimeStockSymbolList = symbolList;
            }
        } catch (IOException e) {
            Log.printStackTrace(e);
        }
    }

    public List<String> getInterestedRealTimeStockSymbolList() {
        return interestedRealTimeStockSymbolList;
    }

    /**
     * update list & save into storage
     */
    public void setInterestedRealTimeStockSymbolList(@NonNull List<String> stockSymbolList, @NonNull Context context) {
        this.interestedRealTimeStockSymbolList = stockSymbolList;
        try {
            JsonDataStorage.writeToFile(STORAGE_KEY_INTERESTED_REAL_TIME_STOCKS, stockSymbolList, context);
        } catch (IOException e) {
            Log.printStackTrace(e);
        }
    }

    @Nullable
    public RealTimeStockInfo getRealTimeStockInfo(@NonNull String symbol) {
        if (symbol.isEmpty()) {
            return null;
        } else {
            return realTimeStockInfoMap.get(symbol);
        }
    }

    /**
     * call service to download info from web
     */
    public void requestRealTimeStockInfo(@NonNull final String symbol, @NonNull final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Intent intent = new Intent(context.getApplicationContext(), RealTimeStockInfoDownloadService.class);
                intent.putExtra(RealTimeStockInfoDownloadService.KEY_SYMBOL, symbol);
                intent.putExtra(RealTimeStockInfoDownloadService.KEY_RECEIVER, new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        if (resultCode == RealTimeStockInfoDownloadService.CODE_SUCCESS) {
                            RealTimeStockInfo stockInfo = (RealTimeStockInfo) resultData.getSerializable(RealTimeStockInfoDownloadService.KEY_RESULT);
                            if (stockInfo != null) {
                                addRealTimeStockInfo(symbol, stockInfo);
                                notifyRealTimeInfoUpdated(context);
                            }
                        }
                    }
                });
                context.getApplicationContext().startService(intent);
                Looper.loop();
            }
        }).start();
    }

    private void addRealTimeStockInfo(@NonNull String symbol, @NonNull RealTimeStockInfo stockInfo) {
        if (!symbol.isEmpty()) {
            realTimeStockInfoMap.put(symbol, stockInfo);
        }
    }

    @Nullable
    public HistoryStockInfo getHistoryStockInfo(@NonNull String symbol, @NonNull HistoryDataRange range) {
        if (symbol.isEmpty()) {
            return null;
        } else {
            Map<HistoryDataRange, HistoryStockInfo> rangeHistoryStockInfoMap = historyStockInfoMap.get(symbol);
            if (rangeHistoryStockInfoMap == null) {
                return null;
            } else {
                return rangeHistoryStockInfoMap.get(range);
            }
        }
    }

    /**
     * call service to download info from web
     */
    public void requestHistoryStockInfo(@NonNull final String symbol, final @NonNull HistoryDataRange range, @NonNull final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Intent intent = new Intent(context.getApplicationContext(), HistoryStockInfoDownloadService.class);
                intent.putExtra(HistoryStockInfoDownloadService.KEY_SYMBOL, symbol);
                intent.putExtra(HistoryStockInfoDownloadService.KEY_RANGE, range);
                intent.putExtra(HistoryStockInfoDownloadService.KEY_RECEIVER, new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        if (resultCode == HistoryStockInfoDownloadService.CODE_SUCCESS) {
                            HistoryStockInfo stockInfo = (HistoryStockInfo) resultData.getSerializable(HistoryStockInfoDownloadService.KEY_RESULT);
                            if (stockInfo != null) {
                                addHistoryStockInfo(symbol, range, stockInfo);
                                notifyHistoryInfoUpdated(context);
                            }
                        }
                    }
                });
                context.getApplicationContext().startService(intent);
                Looper.loop();
            }
        }).start();
    }

    private void addHistoryStockInfo(@NonNull String symbol, @NonNull HistoryDataRange range, @NonNull HistoryStockInfo stockInfo) {
        if (!symbol.isEmpty()) {
            Map<HistoryDataRange, HistoryStockInfo> rangeHistoryStockInfoMap;
            if (historyStockInfoMap.containsKey(symbol)) {
                rangeHistoryStockInfoMap = historyStockInfoMap.get(symbol);
            } else {
                rangeHistoryStockInfoMap = new HashMap<>();
                historyStockInfoMap.put(symbol, rangeHistoryStockInfoMap);
            }
            rangeHistoryStockInfoMap.put(range, stockInfo);
        }
    }

    /**
     * TODO: may need to be modified to only notify registered listeners
     */
    private void notifyRealTimeInfoUpdated(@NonNull Context context) {
        Intent intent = new Intent(BROADCAST_REAL_TIME_INFO_UPDATED);
        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * TODO: may need to be modified to only notify registered listeners
     */
    private void notifyHistoryInfoUpdated(@NonNull Context context) {
        Intent intent = new Intent(BROADCAST_HISTORY_INFO_UPDATED);
        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }
}
