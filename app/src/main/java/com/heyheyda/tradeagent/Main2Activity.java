package com.heyheyda.tradeagent;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.heyheyda.tradeagent.data.HistoryDataRange;
import com.heyheyda.tradeagent.data.RealTimeStockInfo;
import com.heyheyda.tradeagent.fragment.OneFragment;
import com.heyheyda.tradeagent.util.StockInfoAgent;
import com.heyheyda.tradeagent.widget.PageSetting;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends BasePageActivity {

    public static final String KEY_SYMBOL = "symbol";

    private static final String TITLE_FORMAT = "%s";
    private static final String TITLE_FORMAT_WITH_NAME = "%s (%s)";
    private static final int TAB_DEFAULT_PAGE_INDEX = 0;

    private String symbol;

    private void initialMainToolBar() {
        Toolbar mainToolBar = findViewById(R.id.toolBar);
        setSupportActionBar(mainToolBar);
    }

    private OneFragment getInitialedPage(@NonNull String symbol, @NonNull HistoryDataRange range) {
        OneFragment oneFragment = new OneFragment();
        oneFragment.setSymbol(symbol);
        oneFragment.setRange(range);

        return oneFragment;
    }

    @Override
    protected int getLayOutResourceId() {
        return R.layout.activity_main2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialMainToolBar();

        //handel intent
        Intent intent = getIntent();
        if (intent != null) {
            symbol = intent.getStringExtra(KEY_SYMBOL);

            //set title
            StockInfoAgent stockInfoAgent = StockInfoAgent.getInstance(this);
            RealTimeStockInfo stockInfo = stockInfoAgent.getRealTimeStockInfo(symbol);
            if (stockInfo != null) {
                setTitle(String.format(TITLE_FORMAT_WITH_NAME, stockInfo.getName(), stockInfo.getSymbol()));
            } else {
                setTitle(String.format(TITLE_FORMAT, symbol));
            }
        }

        //add pages
        List<PageSetting> pageSettings = new ArrayList<>();
        pageSettings.add(new PageSetting(getInitialedPage(symbol, HistoryDataRange.ONE_DAY), R.string.history_info_range_1d, 0));
        pageSettings.add(new PageSetting(getInitialedPage(symbol, HistoryDataRange.FIVE_DAYS), R.string.history_info_range_5d, 0));
        pageSettings.add(new PageSetting(getInitialedPage(symbol, HistoryDataRange.ONE_MONTH), R.string.history_info_range_1m, 0));
        pageSettings.add(new PageSetting(getInitialedPage(symbol, HistoryDataRange.SIX_MONTHS), R.string.history_info_range_6m, 0));
        pageSettings.add(new PageSetting(getInitialedPage(symbol, HistoryDataRange.YTD), R.string.history_info_range_ytd, 0));
        pageSettings.add(new PageSetting(getInitialedPage(symbol, HistoryDataRange.ONE_YEAR), R.string.history_info_range_1y, 0));
        pageSettings.add(new PageSetting(getInitialedPage(symbol, HistoryDataRange.FIVE_YEARS), R.string.history_info_range_5y, 0));
        pageSettings.add(new PageSetting(getInitialedPage(symbol, HistoryDataRange.MAX), R.string.history_info_range_max, 0));
        setPages(pageSettings);

        //set default focus tab
        enableTab(TAB_DEFAULT_PAGE_INDEX);

        //-- disable swipe page
        setSwipePageEnable(false);
    }

    // TODO: user setting page for page swipeable
}
