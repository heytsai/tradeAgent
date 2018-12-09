package com.heyheyda.tradeagent.widget;

import android.support.v4.app.Fragment;

public class PageSetting {
    private Fragment fragment;
    private int tabNameId;
    private int tabIconId;

    public PageSetting(Fragment fragment, int tabNameId, int tabIconId) {
        this.fragment = fragment;
        this.tabNameId = tabNameId;
        this.tabIconId = tabIconId;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public int getTabNameId() {
        return tabNameId;
    }

    public int getTabIconId() {
        return tabIconId;
    }
}
