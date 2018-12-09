package com.heyheyda.tradeagent.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public interface HistoryStockInfo extends Serializable {

    @Nullable
    String getSymbol();

    @Nullable
    TimeZone getTimeZone();

    @NonNull
    List<Double> getOpenList();

    @NonNull
    List<Double> getHighList();

    @NonNull
    List<Double> getLowList();

    @NonNull
    List<Double> getCloseList();

    @NonNull
    List<Long> getVolList();

    @NonNull
    List<Date> getTimeList();
}
