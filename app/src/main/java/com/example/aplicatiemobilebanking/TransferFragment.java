package com.example.aplicatiemobilebanking;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Transaction;
import com.example.aplicatiemobilebanking.classes.Transfer;
import com.example.aplicatiemobilebanking.classes.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TransferFragment extends Fragment {

    private User user;
    private BankAccount bankAccount;
    private ListView lvTransfers;
    private List<Transfer> transfers = new ArrayList<>(0);
    private ImageButton ibPay, ibTransfer;
    private TextView tvName;
    private TransferHeaderAdapter transferHeaderAdapter;

    public TransferFragment() {
        // Required empty public constructor
    }

    public static TransferFragment newInstance() {
        TransferFragment fragment = new TransferFragment();
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
            transfers = (ArrayList<Transfer>) getArguments().getSerializable("TRANSFERS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transfer, container, false);
        lvTransfers = view.findViewById(R.id.transferFrag_lvTransfers);
        loadLvTransfers();

        ibPay = view.findViewById(R.id.transferFrag_ibPay);
        ibPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayDialogFragment dialog = new PayDialogFragment();
                dialog.show(getActivity().getSupportFragmentManager(), "PayDialogFragment");

            }
        });

        ibTransfer = view.findViewById(R.id.transferFrag_ibTransfer);
        ibTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("BANKACCOUNT", bankAccount);

                TransferDialogFragment dialog = new TransferDialogFragment();
                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "TransferDialogFragment");
            }
        });

        tvName = view.findViewById(R.id.transferFrag_tvName);
        tvName.setText(user.getFullName());

        return view;
    }

    private void loadLvTransfers() {
        transferHeaderAdapter = new TransferHeaderAdapter(getContext());
        transferHeaderAdapter.addSectionHeaderItem(transfers.get(0));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(transfers.get(0).getDate());
        int lastMonth = calendar.get(Calendar.MONTH);


        for (Transfer t : transfers) {
            calendar.setTime(t.getDate());
            int thisMonth = calendar.get(Calendar.MONTH);
            if (thisMonth == lastMonth) {
                transferHeaderAdapter.addItem(t);

            } else {
                transferHeaderAdapter.addSectionHeaderItem(t);
                transferHeaderAdapter.addItem(t);
                lastMonth = calendar.get(Calendar.MONTH);
            }
        }

        lvTransfers.setAdapter(transferHeaderAdapter);
    }


    public void notifyTransferAdapter(){
        transferHeaderAdapter.notifyDataSetChanged();
    }
}