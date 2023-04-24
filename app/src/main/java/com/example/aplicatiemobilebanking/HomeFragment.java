package com.example.aplicatiemobilebanking;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aplicatiemobilebanking.classes.Transaction;
import com.vinaygaba.creditcardview.CreditCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    List<Transaction> transactions = new ArrayList<Transaction>();
    TransactionAdapter adapter;
    ListView lvTransactions;

    public HomeFragment() {

    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        //***TEST****
        try {
            Transaction t1 = new Transaction(1, "Carrefour", 500.0f, new SimpleDateFormat("dd MMM").parse("24 Apr"));
            Transaction t2 = new Transaction(1, "Mega Image", 200.50f, new SimpleDateFormat("dd MMM").parse("23 Apr"));

            transactions.add(t1);
            transactions.add(t2);
            //adapter.notifyDataSetChanged();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //*************

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //***TEST****
        CreditCardView card1 = view.findViewById(R.id.card1);
        card1.setBackgroundResource(com.vinaygaba.creditcardview.R.drawable.cardbackground_world);
        CreditCardView card2 = view.findViewById(R.id.card2);
        card2.setBackgroundResource(com.vinaygaba.creditcardview.R.drawable.cardbackground_world);
        //*************

        adapter = new TransactionAdapter(getContext(), R.layout.lv_row_item, transactions, getLayoutInflater());
        lvTransactions = view.findViewById(R.id.homeFrag_lvTransactions);
        lvTransactions.setAdapter(adapter);
        return view;
    }
}