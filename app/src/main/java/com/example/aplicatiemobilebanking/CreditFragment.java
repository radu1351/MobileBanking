package com.example.aplicatiemobilebanking;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Credit;
import com.example.aplicatiemobilebanking.classes.Credit;
import com.example.aplicatiemobilebanking.classes.User;

import java.util.ArrayList;

public class CreditFragment extends Fragment {

    private User user;
    private BankAccount bankAccount;
    private Button btAddCredit;
    private TextView tvName;
    private ListView lvCredits;
    private ImageButton ibBack;
    private ArrayList<Credit> credits = new ArrayList<>(0);


    public CreditFragment() {
    }

    public static CreditFragment newInstance() {
        CreditFragment fragment = new CreditFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("USER");
            bankAccount = (BankAccount) getArguments().getSerializable("BANKACCOUNT");
            credits = (ArrayList<Credit>) getArguments().getSerializable("CREDITS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit, container, false);

        tvName = view.findViewById(R.id.creditFrag_tvName);
        tvName.setText(user.getFullName());

        ibBack = view.findViewById(R.id.creditFrag_ibBack);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).openHomeFragment();
            }
        });

        btAddCredit = view.findViewById(R.id.creditFrag_btAddCredit);
        btAddCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCreditDialog dialog = new AddCreditDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("BANKACCOUNT", bankAccount);
                bundle.putSerializable("USER", user);
                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "AddCreditDialog");
            }
        });

        loadLvCredits(view);

        return view;
    }

    private void loadLvCredits(View view) {
        lvCredits = view.findViewById(R.id.creditFrag_lvCredits);
        CreditAdapter creditAdapter = new CreditAdapter(getContext(), R.layout.lv_credit_row_item, credits, getLayoutInflater());
        lvCredits.setAdapter(creditAdapter);
        diableLvCreditsRefresh();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void diableLvCreditsRefresh() {
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.mainAct_swipeRefreshLayout);
        lvCredits.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        swipeRefreshLayout.setEnabled(false);
                        break;

                    case MotionEvent.ACTION_UP:
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });
    }
}