package com.heyheyda.tradeagent.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.TimeZone;

public class EmptyRealTimeStockInfo implements RealTimeStockInfo {

    private String symbol;

    public EmptyRealTimeStockInfo(@NonNull String symbol) {
        this.symbol = symbol;
    }

    @Nullable
    @Override
    public String getName() {
        return null;
    }

    @Nullable
    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public double getOpen() {
        return 0;
    }

    @Override
    public double getHigh() {
        return 0;
    }

    @Override
    public double getLow() {
        return 0;
    }

    @Override
    public double getPrice() {
        return 0;
    }

    @Override
    public double getChange() {
        return 0;
    }

    @Override
    public double getChangePercent() {
        return 0;
    }

    @Override
    public long getVol() {
        return 0;
    }

    @Nullable
    @Override
    public Date getTime() {
        return null;
    }

    @Nullable
    @Override
    public TimeZone getTimeZone() {
        return null;
    }
}
