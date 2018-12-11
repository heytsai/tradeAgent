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

    private RealTimeStockInfo realTimeStockInfo;
    private String nameString;
    private String timeString;
    private String priceString;
    private String changeString;
    private int changeColor;

    public RealTimeStockItem(@NonNull RealTimeStockInfo realTimeStockInfo, @NonNull Context context) {
        this.realTimeStockInfo = realTimeStockInfo;
        initialStringsAndColors(context);
    }

    private void initialStringsAndColors(Context context) {
        final String NULL_MARK = " - ";
        final String NAME_STRING_FORMAT = "%s (%s)";
        final String TIME_STRING_FORMAT = "(%s)";
        final String PRICE_STRING_FORMAT = "%.2f";
        final String CHANGE_STRING_FORMAT = "%.2f (%.2f%%)";
        final String DATE_PATTERN = "MMM dd HH:mm";

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
        String time;
        Date datetime = realTimeStockInfo.getTime();
        TimeZone timeZone = realTimeStockInfo.getTimeZone();
        if (datetime != null && timeZone != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
            dateFormat.setTimeZone(timeZone);
            time = dateFormat.format(datetime);
        } else {
            time = NULL_MARK;
        }

        //set strings
        nameString = String.format(NAME_STRING_FORMAT, name, symbol);
        timeString = String.format(TIME_STRING_FORMAT, time);
        priceString = String.format(Locale.getDefault(), PRICE_STRING_FORMAT, price);
        changeString = String.format(Locale.getDefault(), CHANGE_STRING_FORMAT, change, changePercent);

        //set color
        if (change > 0) {
            changeColor = ContextCompat.getColor(context, R.color.colorStatusIncrease);
        } else if (change < 0) {
            changeColor = ContextCompat.getColor(context, R.color.colorStatusDecrease);
        } else {
            changeColor = ContextCompat.getColor(context, R.color.colorStatusNone);
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.real_time_stock_item;
    }

    @Override
    public void draw(View view, boolean isSelected) {
        TextView txtView01 = view.findViewById(R.id.text1);
        TextView txtView02 = view.findViewById(R.id.text2);
        TextView txtView03 = view.findViewById(R.id.text3);
        TextView txtView04 = view.findViewById(R.id.text4);

        //set text
        txtView01.setText(nameString);
        txtView02.setText(timeString);
        txtView03.setText(priceString);
        txtView04.setText(changeString);
        txtView04.setTextColor(changeColor);
    }
}
