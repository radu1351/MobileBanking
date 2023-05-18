package com.example.aplicatiemobilebanking;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Request;
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
    private ArrayList<Transfer> transfers = new ArrayList<>(0);
    private ArrayList<CreditCard> creditCards = new ArrayList<>(0);
    private ArrayList<Request> requests = new ArrayList<Request>(0);
    private ImageButton ibPay, ibTransfer, ibRequest, ibPhoneTransfer;
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
            creditCards = (ArrayList<CreditCard>) getArguments().getSerializable("CREDITCARDS");
            transfers = (ArrayList<Transfer>) getArguments().getSerializable("TRANSFERS");
            requests = (ArrayList<Request>) getArguments().getSerializable("REQUESTS");
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
                Bundle bundle = new Bundle();
                bundle.putSerializable("CREDITCARDS", creditCards);
                bundle.putSerializable("BANKACCOUNT", bankAccount);
                PayDialog dialog = new PayDialog();
                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "PayDialogFragment");

            }
        });

        ibRequest = view.findViewById(R.id.transferFrag_ibRequest);
        ibRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRequestMoneyFragment();
            }
        });

        ibTransfer = view.findViewById(R.id.transferFrag_ibTransfer);
        ibTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("BANKACCOUNT", bankAccount);
                TransferDialog dialog = new TransferDialog();
                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "TransferDialogFragment");
            }
        });

        tvName = view.findViewById(R.id.transferFrag_tvName);
        tvName.setText(user.getFullName());

        lvTransfers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int actualPosition = position;
                int headerCount = 0;
                for (int headerPosition : transferHeaderAdapter.getSectionHeader()) {
                    if (headerPosition < position) {
                        headerCount++;
                    } else {
                        break;
                    }
                }
                actualPosition -= headerCount;

                Bundle bundle = new Bundle();
                bundle.putSerializable("TRANSFER", transfers.get(actualPosition));
                ViewTransferDialog viewTransferDialog = new ViewTransferDialog();
                viewTransferDialog.setArguments(bundle);
                viewTransferDialog.show(getActivity().getSupportFragmentManager(), "ViewTransfersDialog");
            }
        });
        return view;
    }

    private void loadLvTransfers() {
        if (!transfers.isEmpty()) {

            transferHeaderAdapter = new TransferHeaderAdapter(getContext(), bankAccount);
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

    }


    public void openRequestMoneyFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("USER", user);
        bundle.putSerializable("BANKACCOUNT", bankAccount);
        bundle.putSerializable("REQUESTS", requests);

        RequestMoneyFragment requestMoneyFragment = new RequestMoneyFragment();
        requestMoneyFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, requestMoneyFragment)
                .commit();
    }
}