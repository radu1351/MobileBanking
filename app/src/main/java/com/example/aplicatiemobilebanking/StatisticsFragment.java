package com.example.aplicatiemobilebanking;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.ui.DynamicTableModel;
import com.androidplot.util.PixelUtils;


public class StatisticsFragment extends Fragment {

    PieChart pieChartTransactions;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        pieChartTransactions = view.findViewById(R.id.statFrag_pcTransactions);

        createPieChart();

        return view;
    }

    private void createPieChart() {
        Segment segmentGroceries = new Segment("Groceries", 20);
        Segment segmentShopping = new Segment("Shopping", 20);
        Segment segmentTransport = new Segment("Transport", 20);
        Segment segmentRestaurant = new Segment("Restaurant", 10);
        Segment segmentUtilities = new Segment("Utilities", 10);
        Segment segmentOthers = new Segment("Others", 10);

        SegmentFormatter segmentFormatterGroceries = new SegmentFormatter(Color.rgb(0, 15, 75));
        SegmentFormatter segmentFormatterShopping = new SegmentFormatter(Color.rgb(15, 35, 95));
        SegmentFormatter segmentFormatterTransport = new SegmentFormatter(Color.rgb(30, 65, 125));
        SegmentFormatter segmentFormatterRestaurant = new SegmentFormatter(Color.rgb(50, 90, 150));
        SegmentFormatter segmentFormatterUtilities = new SegmentFormatter(Color.rgb(70, 115, 175));
        SegmentFormatter segmentFormatterOthers = new SegmentFormatter(Color.rgb(90, 140, 200));



        segmentFormatterGroceries.getLabelPaint().setTextSize(35f);
        segmentFormatterShopping.getLabelPaint().setTextSize(35f);
        segmentFormatterTransport.getLabelPaint().setTextSize(35f);
        segmentFormatterRestaurant.getLabelPaint().setTextSize(35f);
        segmentFormatterUtilities.getLabelPaint().setTextSize(35f);
        segmentFormatterOthers.getLabelPaint().setTextSize(35f);

        pieChartTransactions.addSegment(segmentGroceries, segmentFormatterGroceries);
        pieChartTransactions.addSegment(segmentShopping, segmentFormatterShopping);
        pieChartTransactions.addSegment(segmentTransport, segmentFormatterTransport);
        pieChartTransactions.addSegment(segmentRestaurant, segmentFormatterRestaurant);
        pieChartTransactions.addSegment(segmentUtilities, segmentFormatterUtilities);
        pieChartTransactions.addSegment(segmentOthers, segmentFormatterOthers);
    }
}