package com.example.aplicatiemobilebanking;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.Size;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.example.aplicatiemobilebanking.classes.Transaction;
import com.example.aplicatiemobilebanking.classes.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class TransactionsFragment extends Fragment {

    private User user;
    private PieChart pieChartTransactions;
    private TransactionHeaderAdapter transactionHeaderAdapter;
    private ListView lvTransatcions;
    private TextView tvName;
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public TransactionsFragment() {
        // Required empty public constructor
    }

    public static TransactionsFragment newInstance(String param1, String param2) {
        TransactionsFragment fragment = new TransactionsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstancetransactionse) {
        super.onCreate(savedInstancetransactionse);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("USER");
            transactions = (ArrayList<Transaction>) getArguments().getSerializable("TRANSACTIONS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstancetransactionse) {

        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        pieChartTransactions = view.findViewById(R.id.transactionsFrag_pcTransactions);
        lvTransatcions = view.findViewById(R.id.transactionsFrag_lvTransactions);

        loadLvTransactions();

        createPieChart();

        tvName = view.findViewById(R.id.transactionsFrag_tvName);
        tvName.setText(user.getFullName());

        return view;
    }

    private void createPieChart() {
        HashMap<String, Float> categoriesPercentage = new HashMap<>(0);

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
                .sum();
        ;

        TextLabelWidget centerText = new TextLabelWidget(pieChartTransactions.getLayoutManager(),
                new Size(1.0f, SizeMode.RELATIVE, 1.0f, SizeMode.RELATIVE));
        centerText.setText(String.valueOf(sum) + " RON");
        centerText.getLabelPaint().setColor(Color.BLACK);
        centerText.getLabelPaint().setTextSize(PixelUtils.dpToPix(13));
        centerText.getLabelPaint().setTextAlign(Paint.Align.CENTER);
        int centerX = pieChartTransactions.getWidth() / 2;
        int centerY = pieChartTransactions.getHeight() / 2;
        centerText.position(centerX - 30, HorizontalPositioning.ABSOLUTE_FROM_CENTER, centerY, VerticalPositioning.ABSOLUTE_FROM_CENTER);
        pieChartTransactions.getLayoutManager().add(centerText);

    }


    private void loadLvTransactions() {
        transactionHeaderAdapter = new TransactionHeaderAdapter(getContext());
        transactionHeaderAdapter.addSectionHeaderItem(transactions.get(0));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(transactions.get(0).getDate());
        int lastMonth = calendar.get(Calendar.MONTH);
        int lastDay = calendar.get(Calendar.DAY_OF_MONTH);

        for (Transaction t : transactions) {
            calendar.setTime(t.getDate());
            int thisMonth = calendar.get(Calendar.MONTH);
            int thisDay = calendar.get(Calendar.DAY_OF_MONTH);

            if (lastMonth == thisMonth && lastDay == thisDay) {
                transactionHeaderAdapter.addItem(t);
            } else {
                transactionHeaderAdapter.addSectionHeaderItem(t);
                transactionHeaderAdapter.addItem(t);
                lastMonth = thisMonth;
                lastDay = thisDay;
            }
        }

        lvTransatcions.setAdapter(transactionHeaderAdapter);
    }


}