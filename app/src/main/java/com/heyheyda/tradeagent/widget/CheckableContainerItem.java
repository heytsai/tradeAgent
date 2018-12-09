package com.heyheyda.tradeagent.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.heyheyda.tradeagent.R;

public class CheckableContainerItem implements RecyclerAdapter.ViewItem {

    private Context context;
    private RecyclerAdapter.ViewItem containedViewItem;

    public CheckableContainerItem(RecyclerAdapter.ViewItem viewItem, Context context) {
        this.context = context;
        this.containedViewItem = viewItem;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.checkable_container_item;
    }

    @Override
    public void draw(View view, boolean isSelected) {
        //draw contained view
        LinearLayout containedViewGroup = view.findViewById(R.id.containedViewGroup);
        containedViewGroup.removeAllViews();
        View containedView = LayoutInflater.from(context).inflate(containedViewItem.getLayoutResource(), containedViewGroup);
        containedViewItem.draw(containedView, isSelected);

        //set check state
        CheckBox checkBox = view.findViewById(R.id.checkBox);
        checkBox.setChecked(isSelected);
    }
}
