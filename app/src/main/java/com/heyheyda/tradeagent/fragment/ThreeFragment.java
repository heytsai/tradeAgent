package com.heyheyda.tradeagent.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heyheyda.tradeagent.Main2Activity;
import com.heyheyda.tradeagent.Main3Activity;
import com.heyheyda.tradeagent.R;
import com.heyheyda.tradeagent.data.EmptyRealTimeStockInfo;
import com.heyheyda.tradeagent.data.RealTimeStockInfo;
import com.heyheyda.tradeagent.util.DialogManager;
import com.heyheyda.tradeagent.util.StockInfoAgent;
import com.heyheyda.tradeagent.widget.RealTimeStockItem;
import com.heyheyda.tradeagent.widget.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ThreeFragment extends Fragment {

    private View fragmentView;
    private RecyclerAdapter recyclerAdapter;
    private BroadcastReceiver broadcastReceiver;
    private StockInfoAgent stockInfoAgent;
    private List<String> stockSymbolList;

    private void enableBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null) {
                    switch (action) {
                        case StockInfoAgent.BROADCAST_REAL_TIME_INFO_UPDATED:
                            displayStockInfo();
                            refreshStockSummaryInfo();
                            break;
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(StockInfoAgent.BROADCAST_REAL_TIME_INFO_UPDATED);
        LocalBroadcastManager.getInstance(fragmentView.getContext()).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void disableBroadcastReceiver() {
        LocalBroadcastManager.getInstance(fragmentView.getContext()).unregisterReceiver(broadcastReceiver);
    }

    private void refreshStockSummaryInfo() {
        final String RECORD_STRING = getResources().getQuantityString(R.plurals.record_count, stockSymbolList.size());

        TextView textView01 = fragmentView.findViewById(R.id.text1);
        TextView textView02 = fragmentView.findViewById(R.id.text2);
        TextView textView03 = fragmentView.findViewById(R.id.text3);
        TextView textView04 = fragmentView.findViewById(R.id.text4);

        textView01.setText(R.string.title_real_time_stock_info);
        textView02.setText(String.format(RECORD_STRING, stockSymbolList.size()));
        textView03.setVisibility(View.GONE);
        textView04.setVisibility(View.GONE);
    }

    private void initialStockInfoList() {
        //initial list
        final RecyclerView recyclerView = fragmentView.findViewById(R.id.list);
        final RecyclerView.LayoutManager recyclerLayoutManager = new LinearLayoutManager(fragmentView.getContext());
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
                String symbol = stockSymbolList.get(position);

                Intent intent = new Intent(getActivity(), Main2Activity.class);
                intent.putExtra(Main2Activity.KEY_SYMBOL, symbol);
                startActivity(intent);
            }
        });


        //set on long click
        recyclerAdapter.setOnItemLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(getActivity(), Main3Activity.class);
                startActivity(intent);

                return true;
            }
        });
    }

    private void initialAddStockButton() {
        final String title = "Add Interested Stock";
        final String message = "Click ACCEPT to add stock.";
        final String hint = "Stock Symbol";

        FloatingActionButton addButton = fragmentView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogManager dialogManager = new DialogManager(DialogManager.DialogType.INPUT, title, message);
                dialogManager.setArgument(DialogManager.DialogArgument.STRING_DIALOG_MESSAGE_INPUT_HINT, hint);
                dialogManager.setDialogClickListener(DialogManager.ListenerType.POSITIVE, new DialogManager.ClickListener() {
                    @Override
                    public void onClick() {
                        String symbol = dialogManager.getStringResult(DialogManager.DialogResult.STRING_DIALOG_MESSAGE_INPUT);
                        if (symbol != null) {
                            stockSymbolList.add(symbol);

                            //save stock symbol list
                            saveStockSymbolList();

                            //display on list
                            displayStockInfo();
                            refreshStockSummaryInfo();

                            //request data to refresh list content
                            requestStockInfo();
                        }
                    }
                });
                dialogManager.showDialog(fragmentView.getContext());
            }
        });
    }

    private void saveStockSymbolList() {
        stockInfoAgent.setInterestedRealTimeStockSymbolList(stockSymbolList, fragmentView.getContext());
    }

    private void requestStockInfo() {
        for (String symbol : stockSymbolList) {
            stockInfoAgent.requestRealTimeStockInfo(symbol, fragmentView.getContext());
        }
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
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerAdapter.clearItems();
                            for (int i = 0; i < stockInfoList.size(); i++) {
                                RealTimeStockInfo stockInfo = stockInfoList.get(i);
                                if (stockInfo != null) {
                                    recyclerAdapter.addItem(new RealTimeStockItem(stockInfo, fragmentView.getContext()), i);
                                } else {
                                    recyclerAdapter.addItem(new RealTimeStockItem(new EmptyRealTimeStockInfo(stockSymbolList.get(i)), fragmentView.getContext()), i);
                                }
                            }
                            recyclerAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableBroadcastReceiver();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //get saved symbol list
                List<String> symbolList = stockInfoAgent.getInterestedRealTimeStockSymbolList();
                if (symbolList != null) {
                    stockSymbolList = symbolList;
                }

                //display list
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayStockInfo();
                            refreshStockSummaryInfo();
                        }
                    });
                }

                //request data to refresh list content
                requestStockInfo();
            }
        }).start();
    }

    @Override
    public void onPause() {
        disableBroadcastReceiver();
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_three, container, false);
        recyclerAdapter = new RecyclerAdapter();
        stockInfoAgent = StockInfoAgent.getInstance(fragmentView.getContext());
        stockSymbolList = new ArrayList<>();

        initialStockInfoList();
        initialAddStockButton();

        // TODO: need to add mechanism to remove stocks
        //-- TODO: maybe use edit mode

        // TODO: need to add alert when no internet (or info cant get)

        // TODO: need to show dialog if symbol is not invalid (not found)

        return fragmentView;
    }
}
