package com.heyheyda.tradeagent;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.heyheyda.tradeagent.widget.CustomSwipeableViewPager;
import com.heyheyda.tradeagent.widget.PageSetting;
import com.heyheyda.tradeagent.widget.ViewPagerAdapter;

import java.util.List;

public abstract class BasePageActivity extends AppCompatActivity {

    private static final int TAB_DEFAULT_SIZE = 3;

    private TabLayout tabLayout;
    private CustomSwipeableViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private void initialViewPager() {
        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        //bind adapter & set click events
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                enableTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    private void initialTabLayout() {
        tabLayout = findViewById(R.id.tab);

        //bind with viewPager
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setTabs(List<PageSetting> pageSettings) {
        for (int i = 0 ;i < pageSettings.size(); i++) {
            PageSetting pageSetting = pageSettings.get(i);

            //set icon & text
            View tabView = getLayoutInflater().inflate(R.layout.icon_tab, tabLayout, false);
            tabView.findViewById(R.id.tab_icon).setBackgroundResource(pageSetting.getTabIconId());
            ((TextView) tabView.findViewById(R.id.tab_text)).setText(pageSetting.getTabNameId());

            //bind to tab
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(tabView);
            }
        }

        //hide tab if page size is 1, hide text of tab if size over default
        if (viewPagerAdapter.getCount() == 1) {
            tabLayout.setVisibility(View.GONE);
        } else if (viewPagerAdapter.getCount() > TAB_DEFAULT_SIZE) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        }
    }

    protected void setPages(List<PageSetting> pageSettings) {
        //add pages
        for (PageSetting pageSetting : pageSettings) {
            viewPagerAdapter.addFragment(pageSetting.getFragment());
        }
        viewPagerAdapter.notifyDataSetChanged();

        //set tab icon & text
        setTabs(pageSettings);
    }

    /**
     * default enable page swipe
     */
    protected void setSwipePageEnable(boolean enable) {
        viewPager.setSwipePageEnable(enable);
    }

    /**
     * enable target tab & disable others
     */
    protected void enableTab(int position) {
        final float ALPHA_ENABLE = 1.0f;
        final float ALPHA_DISABLE = 0.5f;

        if (tabLayout != null) {
            int count = tabLayout.getTabCount();
            if (count > position) {
                for (int i = 0; i < count; i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null) {
                        View tabCustomView = tab.getCustomView();
                        if (tabCustomView != null) {
                            if (i != position) {
                                tabCustomView.setAlpha(ALPHA_DISABLE);
                            } else {
                                tabCustomView.setAlpha(ALPHA_ENABLE);
                            }
                        }
                    }
                }
                viewPager.setCurrentItem(position);
            }
        }
    }

    /**
     * layout of activity should has CustomSwipeableViewPager & TabLayout with id "viewPager" & "tab" respectively
     */
    protected abstract int getLayOutResourceId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayOutResourceId());

        initialViewPager();
        initialTabLayout();
    }
}
