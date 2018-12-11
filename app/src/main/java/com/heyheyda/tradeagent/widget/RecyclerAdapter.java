package com.heyheyda.tradeagent.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public interface ViewItem {

        int getLayoutResource();

        void draw(View view, boolean isSelected);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

    private List<ViewItem> itemList;
    private List<Integer> idList;
    private List<Boolean> isSelectedList;
    private View.OnClickListener itemClickListener;
    private View.OnLongClickListener itemLongClickListener;

    public RecyclerAdapter() {
        this.itemList = new ArrayList<>();
        this.idList = new ArrayList<>();
        this.isSelectedList = new ArrayList<>();
        this.itemClickListener = null;
        this.itemLongClickListener = null;
    }

    public void addItem(@NonNull ViewItem item, int id) {
        this.itemList.add(item);
        this.idList.add(id);
        this.isSelectedList.add(false);
    }

    public void removeItem(int position) {
        this.itemList.remove(position);
        this.idList.remove(position);
        this.isSelectedList.remove(position);
    }

    public void clearItems() {
        this.itemList = new ArrayList<>();
        this.idList = new ArrayList<>();
        this.isSelectedList = new ArrayList<>();
    }

    public List<Integer> getIdList() {
        return idList;
    }

    public List<Boolean> getSelectStateList() {
        return isSelectedList;
    }

    /**
     * move data (item, id, isSelected), then trigger animation
     */
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(this.itemList, i, i + 1);
                Collections.swap(this.idList, i, i + 1);
                Collections.swap(this.isSelectedList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(this.itemList, i, i - 1);
                Collections.swap(this.idList, i, i - 1);
                Collections.swap(this.isSelectedList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public void setItemSelect(int position, boolean select) {
        this.isSelectedList.set(position, select);
    }

    public void clearAllItemSelection() {
        for (int i = 0; i < isSelectedList.size(); i++) {
            isSelectedList.set(i, false);
        }
    }

    public void selectAllItems() {
        for (int i = 0; i < isSelectedList.size(); i++) {
            isSelectedList.set(i, true);
        }
    }

    public void setOnItemClickListener(View.OnClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnItemLongClickListener(View.OnLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    public boolean isItemSelected(int position) {
        return this.isSelectedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.idList.get(position);
    }

    /**
     * viewType is same value as LayoutResource
     */
    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        return new RecyclerAdapter.ViewHolder(itemView);
    }

    /**
     * return LayoutResource as viewType
     */
    @Override
    public int getItemViewType(int position) {
        return this.itemList.get(position).getLayoutResource();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder viewHolder, int position) {
        //set view contents
        ViewItem item = this.itemList.get(position);
        if (item != null) {
            item.draw(viewHolder.itemView, isSelectedList.get(position));
        }

        //set click listener
        if (itemClickListener != null) {
            viewHolder.itemView.setOnClickListener(itemClickListener);
        }

        //set long-click listener
        if (itemLongClickListener != null) {
            viewHolder.itemView.setOnLongClickListener(itemLongClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
