package com.heyheyda.tradeagent.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.heyheyda.tradeagent.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class YahooHistoryStockInfo implements HistoryStockInfo {

    private String symbol;
    private TimeZone timeZone;
    private List<Double> openList;
    private List<Double> highList;
    private List<Double> lowList;
    private List<Double> closeList;
    private List<Long> volList;
    private List<Date> timeList;

    private YahooHistoryStockInfo() {
        this.symbol = null;
        this.openList = new ArrayList<>();
        this.highList = new ArrayList<>();
        this.lowList = new ArrayList<>();
        this.closeList = new ArrayList<>();
        this.volList = new ArrayList<>();
        this.timeList = new ArrayList<>();
    }

    @Nullable
    public static YahooHistoryStockInfo parse(@NonNull String jsonString) {
        YahooHistoryStockInfo info = new YahooHistoryStockInfo();
        try {
            JSONObject object = new JSONObject(jsonString);
            object = object.getJSONObject("chart");
            object = (JSONObject) object.getJSONArray("result").get(0);
            JSONObject metaObject = object.getJSONObject("meta");
            JSONArray timeStampArray = object.getJSONArray(YahooStockApiConstant.TIME_STAMP);
            JSONObject priceObject = (JSONObject) object.getJSONObject("indicators").getJSONArray("quote").get(0);

            //set info
            info.symbol = metaObject.getString(YahooStockApiConstant.SYMBOL);

            //set timezone
            String timeZoneId = metaObject.getString(YahooStockApiConstant.TIMEZONE);
            info.timeZone = TimeZone.getTimeZone(timeZoneId);

            //set open list
            JSONArray openArray = priceObject.getJSONArray(YahooStockApiConstant.HISTORY_OPEN);
            for (int i = 0; i < openArray.length(); i++) {
                info.openList.add(openArray.getDouble(i));
            }

            //set high list
            JSONArray highArray = priceObject.getJSONArray(YahooStockApiConstant.HISTORY_HIGH);
            for (int i = 0; i < highArray.length(); i++) {
                info.highList.add(highArray.getDouble(i));
            }

            //set low list
            JSONArray lowArray = priceObject.getJSONArray(YahooStockApiConstant.HISTORY_LOW);
            for (int i = 0; i < lowArray.length(); i++) {
                info.lowList.add(lowArray.getDouble(i));
            }

            //set close list
            JSONArray closeArray = priceObject.getJSONArray(YahooStockApiConstant.HISTORY_CLOSE);
            for (int i = 0; i < closeArray.length(); i++) {
                info.closeList.add(closeArray.getDouble(i));
            }

            //set volume list
            JSONArray volArray = priceObject.getJSONArray(YahooStockApiConstant.HISTORY_VOLUME);
            for (int i = 0; i < volArray.length(); i++) {
                info.volList.add(volArray.getLong(i));
            }

            //set time list
            for (int i = 0; i < timeStampArray.length(); i++) {
                long timeInSecond  = timeStampArray.getLong(i);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timeInSecond * 1000);
                info.timeList.add(calendar.getTime());
            }
        } catch (JSONException e) {
            Log.printStackTrace(e);
            return null;
        }

        return info;
    }

    @Nullable
    @Override
    public String getSymbol() {
        return symbol;
    }

    @Nullable
    @Override
    public TimeZone getTimeZone() {
        return timeZone;
    }

    @NonNull
    @Override
    public List<Double> getOpenList() {
        return openList;
    }

    @NonNull
    @Override
    public List<Double> getHighList() {
        return highList;
    }

    @NonNull
    @Override
    public List<Double> getLowList() {
        return lowList;
    }

    @NonNull
    @Override
    public List<Double> getCloseList() {
        return closeList;
    }

    @NonNull
    @Override
    public List<Long> getVolList() {
        return volList;
    }

    @NonNull
    @Override
    public List<Date> getTimeList() {
        return timeList;
    }

    @Override
    public String toString() {
        return "symbol: " + this.symbol
                + "\n" + "timeZone: " + this.timeZone
                + "\n" + "open: " + this.openList
                + "\n" + "high: " + this.highList
                + "\n" + "low: " + this.lowList
                + "\n" + "close: " + this.closeList
                + "\n" + "vol: " + this.volList;
    }
}
