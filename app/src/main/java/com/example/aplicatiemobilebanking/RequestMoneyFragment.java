package com.example.aplicatiemobilebanking;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.BlockedNumberContract;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Request;
import com.example.aplicatiemobilebanking.classes.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RequestMoneyFragment extends Fragment {

    private LinearLayout llSender;
    private TextInputEditText tietPersonalNumber;
    private Button btSearch, btRequest;
    private TextView tvSenderName, tvSenderIban;
    private TextView tvName;
    private ListView lvRequests;
    private User senderUser;
    private BankAccount senderBankAccount;

    private User user;
    private BankAccount bankAccount;

    private ArrayList<Request> requests = new ArrayList<>(0);

    public RequestMoneyFragment() {
    }

    public static RequestMoneyFragment newInstance() {
        RequestMoneyFragment fragment = new RequestMoneyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bankAccount = (BankAccount) getArguments().getSerializable("BANKACCOUNT");
            user = (User) getArguments().getSerializable("USER");
            requests = (ArrayList<Request>) getArguments().getSerializable("REQUESTS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_money, container, false);
        llSender = view.findViewById(R.id.requestMoneyFrag_llSender);
        llSender.setVisibility(View.GONE);
        tvName = view.findViewById(R.id.requestMoneyFrag_tvName);
        tvName.setText(user.getFullName());

        tietPersonalNumber = view.findViewById(R.id.requestMoneyFrag_tietPersonalNumber);
        tvSenderIban = view.findViewById(R.id.requestMoneyFrag_tvSenderIban);
        tvSenderName = view.findViewById(R.id.requestMoneyFrag_tvSenderName);

        lvRequests = view.findViewById(R.id.requestMoneyFrag_lvRequests);
        loadLvRequests();

        btSearch = view.findViewById(R.id.requestMoneyFrag_btSearch);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tietPersonalNumber.getText().toString().equals("")) {
                    tietPersonalNumber.setError("No ID Number entered");
                } else {
                    getAccountFromDatabase(new OnSuccessListener<Pair<User, BankAccount>>() {
                        @Override
                        public void onSuccess(Pair<User, BankAccount> userAccountPair) {
                            if (userAccountPair != null) {
                                senderUser = userAccountPair.first;
                                senderBankAccount = userAccountPair.second;
                                if (!senderUser.getIdentificationNumber().equals(user.getIdentificationNumber())) {
                                    tvSenderName.setText(senderUser.getFullName());
                                    tvSenderIban.setText(senderBankAccount.getIban());
                                    llSender.setVisibility(View.VISIBLE);
                                } else
                                    Toast.makeText(getContext(), "You can't request money from yourself",
                                            Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                                llSender.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        btRequest = view.findViewById(R.id.requestMoneyFrag_btRequest);
        btRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request request = new Request();
                request.setId(generateId());
                request.setRequesterIban(bankAccount.getIban());
                request.setRequesterFullName(user.getFullName());
                request.setSenderFullName(senderUser.getFullName());
                request.setSenderIban(senderBankAccount.getIban());

                Bundle bundle = new Bundle();
                bundle.putSerializable("REQUEST", request);
                bundle.putSerializable("BANKACCOUNT", bankAccount);
                RequestDialog dialog = new RequestDialog();
                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "RequestDialog");
            }
        });

        return view;
    }

    public void loadLvRequests() {
        RequestHeaderAdapter requestHeaderAdapter = new RequestHeaderAdapter(getContext(), bankAccount);
        boolean firstOutgoingAdded = false;
        boolean incomingHeaderAdded = false;
        for (Request request : requests) {
            if (request.getSenderIban().equals(bankAccount.getIban()) && request.getState() != 0)
                continue;
            if (!request.getSenderIban().equals(bankAccount.getIban())) {
                if (!firstOutgoingAdded) {
                    requestHeaderAdapter.addSectionHeaderItem(request);
                    firstOutgoingAdded = true;
                }
                requestHeaderAdapter.addItem(request);
            } else {
                if (!incomingHeaderAdded) {
                    requestHeaderAdapter.addSectionHeaderItem(request);
                    incomingHeaderAdded = true;
                }
                requestHeaderAdapter.addItem(request);
            }
        }
        lvRequests.setAdapter(requestHeaderAdapter);
        diableLvRequestsRefresh();
    }


    public void getAccountFromDatabase(OnSuccessListener<Pair<User, BankAccount>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        CollectionReference bankAccountsRef = db.collection("bankAccounts");

        String personalID = tietPersonalNumber.getText().toString();

        Query userQuery = usersRef.whereEqualTo("identificationNumber", personalID);
        Query accountQuery = bankAccountsRef.whereEqualTo("userPersonalID", personalID);

        userQuery.get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                QuerySnapshot userSnapshot = userTask.getResult();
                if (!userSnapshot.isEmpty()) {
                    DocumentSnapshot userDocument = userSnapshot.getDocuments().get(0);
                    User user = userDocument.toObject(User.class);

                    accountQuery.get().addOnCompleteListener(accountTask -> {
                        if (accountTask.isSuccessful()) {
                            QuerySnapshot accountSnapshot = accountTask.getResult();
                            if (!accountSnapshot.isEmpty()) {
                                DocumentSnapshot accountDocument = accountSnapshot.getDocuments().get(0);
                                BankAccount account = accountDocument.toObject(BankAccount.class);

                                callback.onSuccess(new Pair<>(user, account));
                            } else {
                                callback.onSuccess(null); // Account not found
                            }
                        } else {
                            Log.d(TAG, "Error getting account documents: ", accountTask.getException());
                        }
                    });
                } else {
                    callback.onSuccess(null); // User not found
                }
            } else {
                Log.d(TAG, "Error getting user documents: ", userTask.getException());
            }
        });
    }


    public String generateId() {
        Random rand = new Random();
        long min = 100000000000L;
        long max = 999999999999L;
        long randomNum = min + ((long) (rand.nextDouble() * (max - min)));
        return Long.toString(randomNum);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void diableLvRequestsRefresh() {
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.mainAct_swipeRefreshLayout);
        lvRequests.setOnTouchListener(new View.OnTouchListener() {
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