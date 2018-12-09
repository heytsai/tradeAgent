package com.heyheyda.tradeagent.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.heyheyda.tradeagent.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class YahooRealTimeStockInfo implements RealTimeStockInfo {

    private String name;
    private String symbol;
    private double open;
    private double high;
    private double low;
    private double price;
    private double change;
    private double changePercent;
    private long vol;
    private Date time;
    private TimeZone timeZone;

    private YahooRealTimeStockInfo() {
        this.name = null;
        this.symbol = null;
        this.open = 0;
        this.high = 0;
        this.low = 0;
        this.price = 0;
        this.change = 0;
        this.changePercent = 0;
        this.vol = 0;
        this.time = null;
        this.timeZone = null;
    }

    @Nullable
    public static YahooRealTimeStockInfo parse(@NonNull JSONObject object) {
        YahooRealTimeStockInfo info = new YahooRealTimeStockInfo();
        try {
            info.name = object.getString(YahooStockApiConstant.NAME);
            info.symbol = object.getString(YahooStockApiConstant.SYMBOL);
            info.open = object.getDouble(YahooStockApiConstant.OPEN);
            info.high = object.getDouble(YahooStockApiConstant.HIGH);
            info.low = object.getDouble(YahooStockApiConstant.LOW);
            info.price = object.getDouble(YahooStockApiConstant.PRICE);
            info.change = object.getDouble(YahooStockApiConstant.CHANGE);
            info.changePercent = object.getDouble(YahooStockApiConstant.CHANGE_PERCENT);
            info.vol = object.getLong(YahooStockApiConstant.VOLUME);

            //set time
            long timeInSecond  = object.getLong(YahooStockApiConstant.TIME);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeInSecond * 1000);
            info.time = calendar.getTime();

            //set timezone
            String timeZoneId = object.getString(YahooStockApiConstant.TIMEZONE);
            info.timeZone = TimeZone.getTimeZone(timeZoneId);
        } catch (JSONException e) {
            Log.printStackTrace(e);
            return null;
        }

        return info;
    }

    @Nullable
    @Override
    public String getName() {
        return this.name;
    }

    @Nullable
    @Override
    public String getSymbol() {
        return this.symbol;
    }

    @Override
    public double getOpen() {
        return this.open;
    }

    @Override
    public double getHigh() {
        return this.high;
    }

    @Override
    public double getLow() {
        return this.low;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public double getChange() {
        return this.change;
    }

    @Override
    public double getChangePercent() {
        return this.changePercent;
    }

    @Override
    public long getVol() {
        return this.vol;
    }

    @Nullable
    @Override
    public Date getTime() {
        return this.time;
    }

    @Nullable
    @Override
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    @Override
    public String toString() {
        return "name: " + this.name
                + "\n" + "symbol: " + this.symbol
                + "\n" + "open: " + this.open
                + "\n" + "high: " + this.high
                + "\n" + "low: " + this.low
                + "\n" + "price: " + this.price
                + "\n" + "change: " + this.change
                + "\n" + "changePercent: " + this.changePercent
                + "\n" + "vol: " + this.vol
                + "\n" + "time: " + this.time
                + "\n" + "timeZone: " + this.timeZone;
    }
}
