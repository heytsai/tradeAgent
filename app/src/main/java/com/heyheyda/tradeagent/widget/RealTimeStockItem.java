package com.heyheyda.tradeagent.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.heyheyda.tradeagent.R;
import com.heyheyda.tradeagent.data.RealTimeStockInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RealTimeStockItem implements RecyclerAdapter.ViewItem {

    private Context context;
    private RealTimeStockInfo realTimeStockInfo;

    public RealTimeStockItem(@NonNull RealTimeStockInfo realTimeStockInfo, @NonNull Context context) {
        this.realTimeStockInfo = realTimeStockInfo;
        this.context = context;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.real_time_stock_item;
    }

    @Override
    public void draw(View view, boolean isSelected) {
        final String NULL_MARK=" - ";

//        final String MARKET_STRING="%s";
        final String NAME_STRING="%s (%s)";
        final String TIME_STRING="(%s)";
        final String PRICE_STRING="%.2f";
        final String CHANGE_STRING="%.2f (%.2f%%)";

        TextView txtView01 = view.findViewById(R.id.text1);
        TextView txtView02 = view.findViewById(R.id.text2);
        TextView txtView03 = view.findViewById(R.id.text3);
        TextView txtView04 = view.findViewById(R.id.text4);

        //get info
        String name = realTimeStockInfo.getName();
        if (name == null || name.isEmpty()) {
            name = NULL_MARK;
        }
        String symbol = realTimeStockInfo.getSymbol();
        if (symbol == null || symbol.isEmpty()) {
            symbol = NULL_MARK;
        }
        double price = realTimeStockInfo.getPrice();
        double change = realTimeStockInfo.getChange();
        double changePercent = realTimeStockInfo.getChangePercent();

        //get time
        String timeString;
        Date time = realTimeStockInfo.getTime();
        TimeZone timeZone = realTimeStockInfo.getTimeZone();
        if (time != null && timeZone != null) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm z", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm", Locale.getDefault());
            dateFormat.setTimeZone(timeZone);
            timeString = dateFormat.format(time);
        } else {
            timeString = NULL_MARK;
        }

        //get color
        int changeColor;
        if (change > 0) {
            changeColor = ContextCompat.getColor(context, R.color.colorStatusIncrease);
        } else if (change < 0) {
            changeColor = ContextCompat.getColor(context, R.color.colorStatusDecrease);
        } else {
            changeColor = ContextCompat.getColor(context, R.color.colorStatusNone);
        }

        //set text
        txtView01.setText(String.format(NAME_STRING, name, symbol));
        txtView02.setText(String.format(TIME_STRING, timeString));
        txtView03.setText(String.format(Locale.getDefault(), PRICE_STRING, price));
        txtView04.setText(String.format(Locale.getDefault(), CHANGE_STRING, change, changePercent));
        txtView04.setTextColor(changeColor);
    }
}
