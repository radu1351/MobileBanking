package com.example.aplicatiemobilebanking.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aplicatiemobilebanking.R;

public class NoTransactionsFragment extends Fragment {


    public NoTransactionsFragment() {
        // Required empty public constructor
    }


    public static NoTransactionsFragment newInstance(String param1, String param2) {
        NoTransactionsFragment fragment = new NoTransactionsFragment();
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
        return inflater.inflate(R.layout.fragment_no_transactions, container, false);
    }
}