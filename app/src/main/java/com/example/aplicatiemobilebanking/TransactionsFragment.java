package com.example.aplicatiemobilebanking;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.Size;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.PixelUtils;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Transaction;
import com.example.aplicatiemobilebanking.classes.User;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class TransactionsFragment extends Fragment {

    private User user;
    private PieChart pieChartTransactions;
    private TransactionHeaderAdapter transactionHeaderAdapter;
    private ListView lvTransactions;
    private TextView tvName, tvSum;
    private Spinner spinMonth;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private ArrayList<CreditCard> creditCards = new ArrayList<>();

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
            creditCards = (ArrayList<CreditCard>) getArguments().getSerializable("CREDITCARDS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstancetransactionse) {

        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        pieChartTransactions = view.findViewById(R.id.transactionsFrag_pcTransactions);
        lvTransactions = view.findViewById(R.id.transactionsFrag_lvTransactions);
        tvSum = view.findViewById(R.id.transactionsFrag_tvSum);
        tvName = view.findViewById(R.id.transactionsFrag_tvName);
        tvName.setText(user.getFullName());

        if (!transactions.isEmpty()) {

            loadLvTransactions(this.transactions);
            createPieChart(this.transactions);
            initSpinnerMonth(view);

            lvTransactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    int actualPosition = position;
                    int headerCount = 0;
                    for (int headerPosition : transactionHeaderAdapter.getSectionHeader()) {
                        if (headerPosition < position) {
                            headerCount++;
                        } else {
                            break;
                        }
                    }
                    actualPosition -= headerCount;

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("TRANSACTION", transactions.get(actualPosition));
                    bundle.putSerializable("CREDITCARDS", creditCards);
                    ViewTransactionDialog viewTransactionDialog = new ViewTransactionDialog();
                    viewTransactionDialog.setArguments(bundle);
                    viewTransactionDialog.show(getActivity().getSupportFragmentManager(), "ViewTransactionDialog");
                }

            });
        }

        return view;
    }

    private void initSpinnerMonth(View view) {
        spinMonth = view.findViewById(R.id.transactionsFrag_spinMonth);

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Generate a list of entries for the last 3 months
        List<String> entries = getLastSixMonths();

        // Load the entries into the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, entries);

        spinMonth.setAdapter(adapter);

        spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);

                if (!selectedItem.equalsIgnoreCase("All")) {
                    Calendar calendar = Calendar.getInstance();

                    List<Transaction> transactionsInThisMonth = transactions.stream()
                            .filter(transaction -> {
                                calendar.setTime(transaction.getDate());
                                String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                                if (month.equalsIgnoreCase(selectedItem)) return true;
                                else return false;
                            }).collect(Collectors.toList());

                    loadLvTransactions((ArrayList<Transaction>) transactionsInThisMonth);

                    pieChartTransactions.getRegistry().clear();
                    createPieChart((ArrayList<Transaction>) transactionsInThisMonth);
                    pieChartTransactions.redraw();


                } else {
                    loadLvTransactions(transactions);
                    pieChartTransactions.getRegistry().clear();
                    createPieChart(transactions);
                    pieChartTransactions.redraw();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void createPieChart(ArrayList<Transaction> transactions) {
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

        tvSum.setText(String.valueOf(sum) + " RON");
    }


    private void loadLvTransactions(ArrayList<Transaction> transactions) {

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
        lvTransactions.setAdapter(transactionHeaderAdapter);

    }

    public ArrayList<String> getLastSixMonths() {

        sortTransactionsByDate();

        ArrayList<String> lastSixMonths = new ArrayList<>(0);
        lastSixMonths.add("All");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(transactions.get(0).getDate());
        String lastMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

        lastSixMonths.add(lastMonth);

        for (Transaction t : transactions) {
            if (lastSixMonths.size() >= 6) break;
            else {
                calendar.setTime(t.getDate());
                String thisMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

                if (!lastMonth.equalsIgnoreCase(thisMonth)) {
                    lastSixMonths.add(thisMonth);
                    lastMonth = thisMonth;
                }
            }
        }
        return lastSixMonths;
    }

    private void sortTransactionsByDate() {
        transactions.sort(new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                if (t1.getDate().compareTo(t2.getDate()) < 0)
                    return 1;
                else if (t1.getDate().compareTo(t2.getDate()) > 0)
                    return -1;
                else return 0;
            }
        });
    }

}