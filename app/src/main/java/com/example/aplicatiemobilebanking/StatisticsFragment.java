package com.example.aplicatiemobilebanking;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextPaint;
import android.util.Log;

import com.androidplot.ui.Anchor;
import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.PositionMetric;
import com.androidplot.ui.Size;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.example.aplicatiemobilebanking.classes.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class StatisticsFragment extends Fragment {

    PieChart pieChartTransactions;
    TransactionHeaderAdapter transactionHeaderAdapter;
    ListView lvTransatcions;
    ArrayList<Transaction> transactions = new ArrayList<>();

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
            transactions = (ArrayList<Transaction>) getArguments().getSerializable("TRANSACTIONS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        pieChartTransactions = view.findViewById(R.id.statFrag_pcTransactions);
        lvTransatcions = view.findViewById(R.id.statFrag_lvTransactions);

        loadLvTransactions();

        createPieChart();

        return view;
    }

    private void createPieChart() {
        HashMap<String, Float> categoriesPercentage= new HashMap<>(0);

        for (Transaction transaction : transactions) {
            Float procent = 0.0f;
            for (Transaction transaction1 : transactions) {
                if (transaction1.getCategory().equals(transaction.getCategory()))
                    procent++;
            }
            procent = procent / transactions.size();
            categoriesPercentage.put(transaction.getCategory(), procent);
        }

        int[] pieChartColors = {Color.rgb(0, 15, 75), Color.rgb(15, 35, 95),
                Color.rgb(30, 65, 125), Color.rgb(50, 90, 150),
                Color.rgb(50, 90, 150), Color.rgb(70, 115, 175),
                Color.rgb(90, 140, 200)};
        int colorIndex = 0;

        Iterator<Map.Entry<String, Float>> iterator = categoriesPercentage.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Float> entry = iterator.next();
            String category = entry.getKey();
            Float procent = entry.getValue();

            Segment segment = new Segment(category, procent);
            SegmentFormatter segmentFormatter = new SegmentFormatter(pieChartColors[colorIndex++]);
            segmentFormatter.getLabelPaint().setTextSize(35f);
            pieChartTransactions.addSegment(segment, segmentFormatter);
        }


        double sum = transactions.stream()
                .mapToDouble(Transaction::getAmmount)
                .sum();;

        TextLabelWidget centerText = new TextLabelWidget(pieChartTransactions.getLayoutManager(),
                new Size(1.0f, SizeMode.RELATIVE, 1.0f, SizeMode.RELATIVE));
        centerText.setText(String.valueOf(sum) + " RON");
        centerText.getLabelPaint().setColor(Color.BLACK);
        centerText.getLabelPaint().setTextSize(PixelUtils.dpToPix(13));
        centerText.getLabelPaint().setTextAlign(Paint.Align.CENTER);
        int centerX = pieChartTransactions.getWidth() / 2;
        int centerY = pieChartTransactions.getHeight() / 2;
        centerText.position(centerX-30, HorizontalPositioning.ABSOLUTE_FROM_CENTER, centerY, VerticalPositioning.ABSOLUTE_FROM_CENTER);
        pieChartTransactions.getLayoutManager().add(centerText);

    }


    private void loadLvTransactions() {
        transactionHeaderAdapter = new TransactionHeaderAdapter(getContext());

        Date lastDate = transactions.get(0).getDate();
        transactionHeaderAdapter.addSectionHeaderItem(transactions.get(0));

        for (Transaction t : transactions) {
            if (t.getDate().compareTo(lastDate) != 0) {
                transactionHeaderAdapter.addSectionHeaderItem(t);
                transactionHeaderAdapter.addItem(t);
                lastDate = t.getDate();
            } else {
                transactionHeaderAdapter.addItem(t);
            }
        }

        lvTransatcions.setAdapter(transactionHeaderAdapter);
    }


}