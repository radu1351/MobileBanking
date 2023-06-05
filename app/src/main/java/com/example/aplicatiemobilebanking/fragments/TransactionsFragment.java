package com.example.aplicatiemobilebanking.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.list_view_adapters.TransactionHeaderAdapter;
import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Transaction;
import com.example.aplicatiemobilebanking.classes.User;
import com.example.aplicatiemobilebanking.dialogs.StatementDialog;
import com.example.aplicatiemobilebanking.dialogs.ViewTransactionDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


public class TransactionsFragment extends Fragment {

    private User user;
    private PieChart pieChartTransactions;
    private TransactionHeaderAdapter transactionHeaderAdapter;
    private ListView lvTransactions;
    private TextView tvName, tvSum;
    private Spinner spinMonth;
    private Toolbar toolbar;
    private BankAccount bankAccount;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private ArrayList<CreditCard> creditCards = new ArrayList<>();

    public TransactionsFragment() {
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
            bankAccount = (BankAccount) getArguments().getSerializable("BANKACCOUNT");
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
        toolbar = view.findViewById(R.id.transactionsFrag_toolbar);

        toolbar.inflateMenu(R.menu.menu_transactions);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_account_statement:
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("USER", user);
                        bundle.putSerializable("BANKACCOUNT", bankAccount);
                        bundle.putSerializable("TRANSACTIONS", transactions);
                        StatementDialog dialog = new StatementDialog();
                        dialog.setArguments(bundle);
                        dialog.show(getActivity().getSupportFragmentManager(), "PayDialogFragment");
                        return true;
                    default:
                        return false;
                }
            }
        });

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

                    if (!spinMonth.getSelectedItem().toString().equals("All")) {
                        ArrayList<Transaction> filteredTransactions = filterTransactionsByMonth(transactions,
                                spinMonth.getSelectedItem().toString());
                        openViewTransactionDialog(filteredTransactions.get(actualPosition));
                    } else {
                        openViewTransactionDialog(transactions.get((actualPosition)));
                    }
                }
            });
        }

        return view;
    }

    private void openViewTransactionDialog(Transaction transaction) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("TRANSACTION", transaction);
        bundle.putSerializable("CREDITCARDS", creditCards);
        ViewTransactionDialog viewTransactionDialog = new ViewTransactionDialog();
        viewTransactionDialog.setArguments(bundle);
        viewTransactionDialog.show(getActivity().getSupportFragmentManager(), "ViewTransactionDialog");
    }

    private ArrayList<Transaction> filterTransactionsByMonth(List<Transaction> transactions, String month) {
        ArrayList<Transaction> filteredList = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (new SimpleDateFormat("MMMM").format(transaction.getDate()).equals(month)) {
                filteredList.add(transaction);
            }
        }
        return filteredList;
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
                .mapToDouble(Transaction::getAmount)
                .sum();

        tvSum.setText(String.format("%.2f", sum) + " RON");
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
        diableLvTransactionsRefresh();
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

    @SuppressLint("ClickableViewAccessibility")
    public void diableLvTransactionsRefresh() {
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.mainAct_swipeRefreshLayout);
        lvTransactions.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disable refreshing when the user starts touching the ListView
                        swipeRefreshLayout.setEnabled(false);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Enable refreshing when the user stops touching the ListView
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }

                // Pass the touch event to the ListView
                return false;
            }
        });
    }

}