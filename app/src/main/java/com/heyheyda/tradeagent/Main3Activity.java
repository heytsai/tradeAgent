package com.heyheyda.tradeagent;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.heyheyda.tradeagent.data.EmptyRealTimeStockInfo;
import com.heyheyda.tradeagent.data.RealTimeStockInfo;
import com.heyheyda.tradeagent.util.DialogManager;
import com.heyheyda.tradeagent.util.Log;
import com.heyheyda.tradeagent.util.StockInfoAgent;
import com.heyheyda.tradeagent.widget.CheckableContainerItem;
import com.heyheyda.tradeagent.widget.RealTimeStockItem;
import com.heyheyda.tradeagent.widget.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * edit mode of interested stock info list
 */
public class Main3Activity extends AppCompatActivity {

    private Toolbar mainToolBar;
    private RecyclerAdapter recyclerAdapter;
    private StockInfoAgent stockInfoAgent;
    private List<String> stockSymbolList;

    private void initialMainToolBar() {
        mainToolBar = findViewById(R.id.toolBar);
        mainToolBar.setBackgroundColor(Color.GRAY);
        mainToolBar.setTitle(R.string.edit_mode_tip);
        setSupportActionBar(mainToolBar);

        //show home button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initialStockInfoList() {
        //initial list
        final RecyclerView recyclerView = findViewById(R.id.list);
        final RecyclerView.LayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        //set divider in list
        final Drawable divider = getResources().getDrawable(R.drawable.recycler_view_divider);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                //draw divider line on bottom of all items (except the last item)
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();

                int childCount = parent.getChildCount();
                for (int i = 0; i < (childCount - 1); i++) {
                    View child = parent.getChildAt(i);

                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                    int top = child.getBottom() + params.bottomMargin;
                    int bottom = top + divider.getIntrinsicHeight();

                    divider.setBounds(left, top, right, bottom);
                    divider.draw(c);
                }
            }
        });

        //set click listener
        recyclerAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = recyclerView.getChildLayoutPosition(v);

                //reverse the selection state of item
                recyclerAdapter.setItemSelect(position, !recyclerAdapter.isItemSelected(position));
                recyclerAdapter.notifyItemChanged(position);

                refreshNumberOfSelectedItems();
                refreshAccessibilityOfToolBarItems();
            }
        });

        //let list item movable
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            private final static String TAG = "ITHC";

            /**
             * set able to drag in all directions
             */
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG
                        , ItemTouchHelper.UP | ItemTouchHelper.DOWN
                                | ItemTouchHelper.START | ItemTouchHelper.END);
            }

            /**
             * move dragged item to target position & update symbol list after moved
             */
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                //move item
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                if (adapter != null && adapter instanceof RecyclerAdapter) {
                    ((RecyclerAdapter) adapter).moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                } else {
                    Log.d(TAG, "onMove: invalid adapter.");
                }
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) { }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * get info from agent & refresh the list
     */
    private void displayStockInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //get real time stock info
                final List<RealTimeStockInfo> stockInfoList = new ArrayList<>();
                for (String symbol : stockSymbolList) {
                    stockInfoList.add(stockInfoAgent.getRealTimeStockInfo(symbol));
                }

                //display on list
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerAdapter.clearItems();
                        for (int i = 0; i < stockInfoList.size(); i++) {
                            RealTimeStockInfo stockInfo = stockInfoList.get(i);
                            if (stockInfo != null) {
                                recyclerAdapter.addItem(new CheckableContainerItem(new RealTimeStockItem(stockInfo, Main3Activity.this), Main3Activity.this), i);
                            } else {
                                recyclerAdapter.addItem(new CheckableContainerItem(new RealTimeStockItem(new EmptyRealTimeStockInfo(stockSymbolList.get(i)), Main3Activity.this), Main3Activity.this), i);
                            }
                        }
                        recyclerAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void saveStockSymbolList() {
        stockInfoAgent.setInterestedRealTimeStockSymbolList(getDisplayedStockSymbolList(), this);
    }

    @NonNull
    private List<String> getDisplayedStockSymbolList() {
        List<Integer> idList = recyclerAdapter.getIdList();
        List<String> stockSymbolListDisplayed = new ArrayList<>();
        for (int id : idList) {
            if (id < stockSymbolList.size()) {
                String symbol = stockSymbolList.get(id);
                stockSymbolListDisplayed.add(symbol);
            }
        }
        return stockSymbolListDisplayed;
    }

    private void refreshNumberOfSelectedItems() {
        //get number
        int count = 0;
        for (boolean isSelected : recyclerAdapter.getSelectStateList()) {
            if (isSelected) {
                count++;
            }
        }

        //set title
        if (count > 0) {
            String titleString = getString(R.string.edit_mode_tip_count, count);
            mainToolBar.setTitle(titleString);
        } else {
            mainToolBar.setTitle(R.string.edit_mode_tip);
        }
    }

    /**
     * refresh accessibility of items of tool bar
     */
    private void refreshAccessibilityOfToolBarItems() {
        final int ALPHA_ENABLE = 255;
        final int ALPHA_DISABLE = 130;

        Menu menu = mainToolBar.getMenu();
        List<Boolean> selectStateList = recyclerAdapter.getSelectStateList();

        //disable all menu items
        menu.findItem(R.id.delete).setEnabled(false);
        menu.findItem(R.id.checkAll).setEnabled(false);
        menu.findItem(R.id.checkAll).getIcon().setAlpha(ALPHA_DISABLE);

        //enable menu item depends on number items
        if (selectStateList.size() > 0) {
            menu.findItem(R.id.checkAll).setEnabled(true);
            menu.findItem(R.id.checkAll).getIcon().setAlpha(ALPHA_ENABLE);
        }

        //get number of selected items
        int count = 0;
        for (boolean isSelected : selectStateList) {
            if (isSelected) {
                count++;
            }
        }

        //enable menu item depends on number of selected items
        if (count > 0) {
            menu.findItem(R.id.delete).setEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final List<Boolean> selectStateList = recyclerAdapter.getSelectStateList();

        switch (item.getItemId()) {
            case R.id.checkAll:
                boolean isAllItemSelected = true;
                for (boolean isSelected : selectStateList) {
                    if (!isSelected) {
                        isAllItemSelected = false;
                    }
                }

                //clear all check box if all selected, otherwise clear all selection
                if (isAllItemSelected) {
                    recyclerAdapter.clearAllItemSelection();
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    recyclerAdapter.selectAllItems();
                    recyclerAdapter.notifyDataSetChanged();
                }

                refreshNumberOfSelectedItems();
                refreshAccessibilityOfToolBarItems();
                return true;
            case R.id.delete:
                int count = 0;
                for (boolean selectState : selectStateList) {
                    if (selectState) {
                        count++;
                    }
                }

                //set deletion message
                String message = getResources().getQuantityString(R.plurals.dialog_message_text_delete, count);

                //show dialog before deletion
                final DialogManager dialogManager = new DialogManager(DialogManager.DialogType.CONFIRM, null, message);
                dialogManager.setDialogClickListener(DialogManager.ListenerType.POSITIVE, new DialogManager.ClickListener() {
                    @Override
                    public void onClick() {
                        //remove selected items
                        for (int i = selectStateList.size() - 1; i >= 0; i--) {
                            boolean isSelected = selectStateList.get(i);
                            if (isSelected) {
                                recyclerAdapter.removeItem(i);
                            }
                        }
                        recyclerAdapter.notifyDataSetChanged();

                        refreshNumberOfSelectedItems();
                        refreshAccessibilityOfToolBarItems();
                    }
                });
                dialogManager.showDialog(this);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_2, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onPause() {
        saveStockSymbolList();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //get saved symbol list
                List<String> symbolList = stockInfoAgent.getInterestedRealTimeStockSymbolList();
                if (symbolList != null) {
                    stockSymbolList = symbolList;
                }

                //display list
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayStockInfo();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        recyclerAdapter = new RecyclerAdapter();
        stockInfoAgent = StockInfoAgent.getInstance(this);
        stockSymbolList = new ArrayList<>();

        initialMainToolBar();
        initialStockInfoList();
    }
}
