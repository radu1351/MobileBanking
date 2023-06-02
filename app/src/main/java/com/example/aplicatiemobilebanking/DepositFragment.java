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
import android.widget.ListView;
import android.widget.TextView;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Deposit;
import com.example.aplicatiemobilebanking.classes.User;

import java.util.ArrayList;

public class DepositFragment extends Fragment {

    private User user;
    private BankAccount bankAccount;
    private Button btAddDeposit;
    private TextView tvName;
    private ListView lvDeposits;
    private ArrayList<Deposit> deposits = new ArrayList<>(0);


    public DepositFragment() {
    }

    public static DepositFragment newInstance() {
        DepositFragment fragment = new DepositFragment();
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
            deposits = (ArrayList<Deposit>) getArguments().getSerializable("DEPOSITS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deposit, container, false);

        tvName = view.findViewById(R.id.depositFrag_tvName);
        tvName.setText(user.getFullName());

        btAddDeposit = view.findViewById(R.id.depositFrag_btAddDeposit);
        btAddDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDepositDialog dialog = new AddDepositDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("BANKACCOUNT", bankAccount);
                bundle.putSerializable("USER", user);
                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "AddDepositDialog");
            }
        });

        loadLvDeposits(view);

        return view;
    }

    private void loadLvDeposits(View view){
        lvDeposits = view.findViewById(R.id.depositFrag_lvDeposits);
        DepositAdapter depositAdapter = new DepositAdapter(getContext(),R.layout.lv_deposits_row_item,deposits,getLayoutInflater());
        lvDeposits.setAdapter(depositAdapter);
        diableLvDepositsRefresh();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void diableLvDepositsRefresh() {
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.mainAct_swipeRefreshLayout);
        lvDeposits.setOnTouchListener(new View.OnTouchListener() {
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