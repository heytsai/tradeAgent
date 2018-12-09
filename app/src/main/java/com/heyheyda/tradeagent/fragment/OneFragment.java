package com.heyheyda.tradeagent.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.heyheyda.tradeagent.R;
import com.heyheyda.tradeagent.data.HistoryDataRange;
import com.heyheyda.tradeagent.data.HistoryStockInfo;
import com.heyheyda.tradeagent.util.Log;
import com.heyheyda.tradeagent.util.StockInfoAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class OneFragment extends Fragment {

    private class CustomMarkerView extends MarkerView {

        private static final int Y_OFFSET = 12;

        private TextView tvContent;

        public CustomMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);

            // find your layout components
            tvContent = findViewById(R.id.tvContent);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            tvContent.setText((String) e.getData());

            //this will perform necessary layout
            super.refreshContent(e, highlight);
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), (-getHeight() - Y_OFFSET));
        }
    }

    private static final String TAG = "OF";
    private static final String KEY_SYMBOL = "symbol";
    private static final String KEY_RANGE = "range";
    private static final String DATE_PATTERN_TIME = "hh:mm";
    private static final String DATE_PATTERN_DATE_TIME = "MM/dd hh:mm";
    private static final String DATE_PATTERN_DATE = "MMM dd";
    private static final String DATE_PATTERN_MONTH_DATE = "YY/MM/dd";
    private static final String DATE_PATTERN_MONTH = "YYYY/MM";
    private static final String VOL_FORMAT = "%s";
    private static final String VOL_FORMAT_THOUSAND = "%.2fk";
    private static final String VOL_FORMAT_MILLION = "%.2fM";
    private static final String PRICE_FORMAT = "%.2f";
    private static final String MARKER_FORMAT_PRICE = "%s";
    private static final String MARKER_FORMAT_VOL = "%s (%s)";
    private static final int BAR_CHART_MAXIMUM_FACTOR = 3;

    private View fragmentView;
    private CombinedChart chart;
    private BroadcastReceiver broadcastReceiver;
    private StockInfoAgent stockInfoAgent;
    private String symbol;
    private HistoryDataRange range;
    private HistoryStockInfo stockInfo;

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setRange(HistoryDataRange range) {
        this.range = range;
    }

    private void enableBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null) {
                    switch (action) {
                        case StockInfoAgent.BROADCAST_HISTORY_INFO_UPDATED:
                            displayStockInfo();
                            break;
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(StockInfoAgent.BROADCAST_HISTORY_INFO_UPDATED);
        LocalBroadcastManager.getInstance(fragmentView.getContext()).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void disableBroadcastReceiver() {
        LocalBroadcastManager.getInstance(fragmentView.getContext()).unregisterReceiver(broadcastReceiver);
    }

    private void requestStockInfo() {
        stockInfoAgent.requestHistoryStockInfo(symbol, range, fragmentView.getContext());
    }

    /**
     * also reduce the height of the bar chart
     */
    private void displayStockInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //get history data
                stockInfo = stockInfoAgent.getHistoryStockInfo(symbol, range);

                //draw chart
                if (stockInfo != null) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LineData lineData = generateLineData(getString(R.string.chart_label_close));
                                BarData barData = generateBarData(getString(R.string.chart_label_vol));

                                //shrink bar chart by increasing y-axis maximum with given factor
                                YAxis axisRight = chart.getAxisRight();
                                axisRight.setAxisMaximum(barData.getYMax() * BAR_CHART_MAXIMUM_FACTOR);

                                //produce combined data
                                CombinedData combinedData = new CombinedData();
                                combinedData.setData(lineData);
                                combinedData.setData(barData);

                                //draw data
                                drawData(combinedData);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private LineData generateLineData(@NonNull String label) {
        int dataColor = getResources().getColor(R.color.colorChartDataRed);
        int highlightLineColor = Color.BLACK;

        //produce line data entry (x: millisecond, y: close)
        List<Double> closeList = stockInfo.getCloseList();
        List<Entry> values = new ArrayList<>();
        for (int i = 0; i < closeList.size(); i++) {
            float price =  closeList.get(i).floatValue();

            //produce marker string
            String priceString = String.format(Locale.US, PRICE_FORMAT, price);
            String markerString = String.format(MARKER_FORMAT_PRICE, priceString);

            values.add(new Entry(i, price, markerString));
        }

        //set draw style of data set
        LineDataSet dataSet = new LineDataSet(values, label);
        //-- line
        dataSet.setColor(dataColor);
        dataSet.setLineWidth(2.5f);
        //-- fill
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(ContextCompat.getDrawable(fragmentView.getContext(), R.drawable.fade_red));
        //-- hide point
        dataSet.setDrawCircles(false);
        //-- hide point text
        dataSet.setDrawValues(false);
        //-- highlight line
        dataSet.enableDashedHighlightLine(10f, 10f, 0f);
        dataSet.setHighLightColor(highlightLineColor);
        //-- hide horizontal highlight line
        dataSet.setDrawHorizontalHighlightIndicator(false);
        //-- depend to left axis (use a different axis than used by BarData)
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        //produce line data
        List<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(dataSet);

        return new LineData(iLineDataSets);
    }

    private BarData generateBarData(@NonNull String label) {
        final int oneThousand = 1000;
        final int oneMillion = 1000000;

        //produce bar data entry (x: millisecond, y: vol)
        List<Long> volList = stockInfo.getVolList();
        List<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < volList.size(); i++) {
            float vol =  volList.get(i).floatValue();

            //produce marker string
            String volString;
            if (vol >= oneMillion) {
                volString = String.format(Locale.US, VOL_FORMAT_MILLION, vol / oneMillion);
            } else if (vol >= oneThousand) {
                volString = String.format(Locale.US, VOL_FORMAT_THOUSAND, vol / oneThousand);
            } else {
                volString = String.format(Locale.US, VOL_FORMAT, (int) vol);
            }
            String markerString = String.format(MARKER_FORMAT_VOL, volString, getStockDateByIndex(range, i));

            values.add(new BarEntry(i, vol, markerString));
        }
        BarDataSet dataSet = new BarDataSet(values, label);

        //set draw style of data set
        //-- hide point text
        dataSet.setDrawValues(false);
        //-- depend to right axis (use a different axis than used by LineData)
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        return new BarData(dataSet);
    }

    /**
     * set data & refresh
     */
    private void drawData(@NonNull CombinedData data) {
        chart.setData(data);
        chart.invalidate();
    }

    private void initialChart() {
        chart = fragmentView.findViewById(R.id.chart);

        //set x-axis
        //-- style
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        //-- x value formatter
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return getStockDateByIndex(range, (int) value);
            }
        });

        //set y-axis style
        YAxis axisRight = chart.getAxisRight();
        //-- set minimum as 0
        axisRight.setAxisMinimum(0);
        //-- hide right axis
        axisRight.setEnabled(false);

        //hide description label
        chart.getDescription().setEnabled(false);

        //set highlight marker style
        CustomMarkerView markerView = new CustomMarkerView(fragmentView.getContext(), R.layout.chart_highlight_block);
        //-- for bounds control
        markerView.setChartView(chart);
        //-- set the marker to the chart
        chart.setMarker(markerView);

        //disable zooming
        chart.setScaleEnabled(false);

        //highlight both bar & line when value selected
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                float xPx = h.getXPx();

                //the implementation can only get the two highlights near top & bottom respectively, not for more than 2 dataSets in the chart
                Highlight[] highlights = {chart.getHighlightByTouchPoint(xPx, 0), chart.getHighlightByTouchPoint(xPx, chart.getHeight())};
                chart.highlightValues(highlights);
            }

            @Override
            public void onNothingSelected() { }
        });
    }

    /**
     * date format depends on range
     */
    @Nullable
    private String getStockDateByIndex(HistoryDataRange range, int index) {
        String datePattern;
        switch (range) {
            case ONE_DAY:
                datePattern = DATE_PATTERN_TIME;
                break;
            case FIVE_DAYS:
                datePattern = DATE_PATTERN_DATE_TIME;
                break;
            case ONE_MONTH:
            case SIX_MONTHS:
            case YTD:
            case ONE_YEAR:
                datePattern = DATE_PATTERN_DATE;
                break;
            case FIVE_YEARS:
                datePattern = DATE_PATTERN_MONTH_DATE;
                break;
            case MAX:
                datePattern = DATE_PATTERN_MONTH;
                break;
            default:
                Log.d(TAG, "getStockDateByIndex: invalid range: " + range);
                return null;
        }

        //set date string format
        TimeZone timeZone = stockInfo.getTimeZone();
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern, Locale.getDefault());
        dateFormat.setTimeZone(timeZone);

        //millisecond to date
        List<Date> timeList = stockInfo.getTimeList();
        long millisecond = timeList.get(index).getTime();
        Date date = new Date();
        date.setTime(millisecond);

        return dateFormat.format(date);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //save variable for rebuild
        outState.putString(KEY_SYMBOL, symbol);
        outState.putSerializable(KEY_RANGE, range);
    }

    @Override
    public void onResume() {
        super.onResume();
        enableBroadcastReceiver();

        new Thread(new Runnable() {
            @Override
            public void run() {
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_one, container, false);
        stockInfoAgent = StockInfoAgent.getInstance(fragmentView.getContext());

        //get variable for rebuild
        if (savedInstanceState != null) {
            symbol = savedInstanceState.getString(KEY_SYMBOL);
            range = (HistoryDataRange) savedInstanceState.getSerializable(KEY_RANGE);
        }

        initialChart();

        return fragmentView;
    }

    // TODO: add mechanism to compute price & vol difference
    //-- TODO: may use button to "fix" one reference point & enter the "computation" mode
    //-- TODO: then display the difference between "selected" & "reference" point
}
