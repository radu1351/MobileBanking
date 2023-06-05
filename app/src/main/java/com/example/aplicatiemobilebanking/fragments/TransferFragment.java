package com.example.aplicatiemobilebanking.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aplicatiemobilebanking.activities.MainActivity;
import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.list_view_adapters.TransferHeaderAdapter;
import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Request;
import com.example.aplicatiemobilebanking.classes.Transfer;
import com.example.aplicatiemobilebanking.classes.User;
import com.example.aplicatiemobilebanking.dialogs.MobileTransferDialog;
import com.example.aplicatiemobilebanking.dialogs.PayDialog;
import com.example.aplicatiemobilebanking.dialogs.TransferDialog;
import com.example.aplicatiemobilebanking.dialogs.ViewTransferDialog;

import java.util.ArrayList;
import java.util.Calendar;


public class TransferFragment extends Fragment {

    private User user;
    private BankAccount bankAccount;
    private ListView lvTransfers;
    private ArrayList<Transfer> transfers = new ArrayList<>(0);
    private ArrayList<CreditCard> creditCards = new ArrayList<>(0);
    private ArrayList<Request> requests = new ArrayList<>(0);
    private Button btPay, btTransfer, btRequest, btMobileTransfer;
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

        btPay = view.findViewById(R.id.transferFrag_btPay);
        btPay.setOnClickListener(new View.OnClickListener() {
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

        btRequest = view.findViewById(R.id.transferFrag_btRequest);
        btRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).openRequestMoneyFragment();
            }
        });

        btTransfer = view.findViewById(R.id.transferFrag_btTransfer);
        btTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("BANKACCOUNT", bankAccount);
                TransferDialog dialog = new TransferDialog();
                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "TransferDialogFragment");
            }
        });

        btMobileTransfer = view.findViewById(R.id.transferFrag_btMobile);
        btMobileTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("BANKACCOUNT", bankAccount);
                bundle.putSerializable("USER", user);
                MobileTransferDialog dialog = new MobileTransferDialog();
                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "MobileTransferDialog");
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
        diableLvTransfersRefresh();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void diableLvTransfersRefresh() {
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.mainAct_swipeRefreshLayout);
        lvTransfers.setOnTouchListener(new View.OnTouchListener() {
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