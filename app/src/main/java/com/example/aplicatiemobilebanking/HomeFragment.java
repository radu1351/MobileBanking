package com.example.aplicatiemobilebanking;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinaygaba.creditcardview.CreditCardView;


public class HomeFragment extends Fragment {

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        CreditCardView card1 = view.findViewById(R.id.card1);
        card1.setBackgroundResource(com.vinaygaba.creditcardview.R.drawable.cardbackground_world);
        CreditCardView card2 = view.findViewById(R.id.card2);
        card2.setBackgroundResource(com.vinaygaba.creditcardview.R.drawable.cardbackground_world);


        return view;
    }
}