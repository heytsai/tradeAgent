package com.heyheyda.tradeagent.data;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

public interface RealTimeStockInfo extends Serializable {

    @Nullable
    String getName();

    @Nullable
    String getSymbol();

    double getOpen();

    double getHigh();

    double getLow();

    double getPrice();

    double getChange();

    double getChangePercent();

    long getVol();

    @Nullable
    Date getTime();

    @Nullable
    TimeZone getTimeZone();
}
