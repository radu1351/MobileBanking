package com.example.aplicatiemobilebanking;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.aplicatiemobilebanking.classes.Transaction;
import com.example.aplicatiemobilebanking.classes.Transfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TransferFragment extends Fragment {

    private ListView lvTransfers;
    private List<Transfer> transfers = new ArrayList<>(0);

    TransferHeaderAdapter transferHeaderAdapter ;

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
            transfers = (ArrayList<Transfer>) getArguments().getSerializable("TRANSFERS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_transfer, container, false);
        lvTransfers = view.findViewById(R.id.transferFrag_lvTransfers);

        loadLvTransfers();

        return view;
    }

    private void loadLvTransfers(){
        transferHeaderAdapter = new TransferHeaderAdapter(getContext());

        Date lastDate = transfers.get(0).getDate();
        transferHeaderAdapter.addSectionHeaderItem(transfers.get(0));

        for (Transfer t : transfers) {
            if (t.getDate().compareTo(lastDate) != 0) {
                transferHeaderAdapter.addSectionHeaderItem(t);
                transferHeaderAdapter.addItem(t);
                lastDate = t.getDate();
            } else {
                transferHeaderAdapter.addItem(t);
            }
        }

        lvTransfers.setAdapter(transferHeaderAdapter);
    }
}