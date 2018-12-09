package com.heyheyda.tradeagent;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.heyheyda.tradeagent.widget.PageSetting;
import com.heyheyda.tradeagent.fragment.ThreeFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BasePageActivity {

    private static final int TAB_DEFAULT_PAGE_INDEX = 0;

    /**
     * layout of activity should has tool bar with id "toolBar"
     */
    private void initialMainToolBar() {
        Toolbar mainToolBar = findViewById(R.id.toolBar);
        setSupportActionBar(mainToolBar);
    }

    @Override
    protected int getLayOutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialMainToolBar();

        //add pages
        List<PageSetting> pageSettings = new ArrayList<>();
        pageSettings.add(new PageSetting(new ThreeFragment(), R.string.tab_name_watchlist, R.drawable.ic_action_hs02));
        setPages(pageSettings);

        //set default focus tab
        enableTab(TAB_DEFAULT_PAGE_INDEX);
    }

    //page 1: interested
    // TODO: page 2: hold
    //-- TODO: detailed page: history chart with pre-marked reference point (in-point)
    // TODO: page 3: history trade records
    //-- TODO: detailed page: history chart with in & out point marked, also show other records of same stock
}
