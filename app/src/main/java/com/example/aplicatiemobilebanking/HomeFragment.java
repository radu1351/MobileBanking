package com.example.aplicatiemobilebanking;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Credit;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Deposit;
import com.example.aplicatiemobilebanking.classes.Transaction;
import com.example.aplicatiemobilebanking.classes.User;
import com.vinaygaba.creditcardview.CardType;
import com.vinaygaba.creditcardview.CreditCardView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private User user;
    private BankAccount bankAccount;
    private ArrayList<CreditCard> creditCards = new ArrayList<CreditCard>();
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private ArrayList<Deposit> deposits = new ArrayList<>();
    private ArrayList<Credit> credits = new ArrayList<Credit>();

    private ListView lvTransactions;
    private TextView tvName, tvBalance;
    private HorizontalScrollView hsvCards;
    private LinearLayout llCards;
    private Button btAddCard, btViewBankAccount, btDeposits, btCredits;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("USER");
            bankAccount = (BankAccount) getArguments().get("BANKACCOUNT");
            creditCards = (ArrayList<CreditCard>) getArguments().getSerializable("CREDITCARDS");
            transactions = (ArrayList<Transaction>) getArguments().getSerializable("TRANSACTIONS");
            deposits = (ArrayList<Deposit>) getArguments().getSerializable("DEPOSITS");
            credits = (ArrayList<Credit>) getArguments().getSerializable("CREDIS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvName = view.findViewById(R.id.homeFrag_tvName);
        tvName.setText(user.getFullName());
        tvBalance = view.findViewById(R.id.homeFrag_tvBalance);
        tvBalance.setText(String.format("%.2f", bankAccount.getBalance()) + " " + bankAccount.getCurrency());
        hsvCards = view.findViewById(R.id.homeFrag_hsvCards);
        llCards = view.findViewById(R.id.homeFrag_llCards);

        loadCreditCardView(view);

        btAddCard = view.findViewById(R.id.homeFrag_btAddCard);
        btDeposits = view.findViewById(R.id.homeFrag_btDeposits);
        btViewBankAccount = view.findViewById(R.id.homeFrag_btViewAccount);

        btAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (creditCards.size() < 2) {
                    AddCardDialog dialog = new AddCardDialog();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("USER", user);
                    bundle.putSerializable("BANKACCOUNT", bankAccount);
                    dialog.setArguments(bundle);
                    dialog.show(getActivity().getSupportFragmentManager(), "AddCardDialogFragment");
                } else
                    Toast.makeText(getContext(), "You can have a maximum of two credit cards", Toast.LENGTH_SHORT).show();
            }
        });

        btDeposits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).openDepositFragment();
            }
        });

        btCredits = view.findViewById(R.id.homeFrag_btCredits);
        btCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).openCreditFragment();
            }
        });

        btViewBankAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewBankAccountDialog dialog = new ViewBankAccountDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("BANKACCOUNT", bankAccount);
                bundle.putSerializable("CREDITCARDS", creditCards);
                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "AddCardDialogFragment");
            }
        });

        return view;
    }


    private void loadCreditCardView(View view) {

        if (creditCards.size() == 2) {
            CreditCardView card1 = view.findViewById(R.id.card1);
            card1.setBackgroundResource(com.vinaygaba.creditcardview.R.drawable.cardbackground_world);
            card1.setCardNumber(creditCards.get(0).getCardNumber());
            card1.setCardName(creditCards.get(0).getCardholderName());
            card1.setType(creditCards.get(0).getCardType() == 0 ? CardType.VISA : CardType.MASTERCARD);
            card1.setExpiryDate(new SimpleDateFormat("MM/yy").format(creditCards.get(0).getExpirationDate()));

            CreditCardView card2 = view.findViewById(R.id.card2);
            card2.setBackgroundResource(com.vinaygaba.creditcardview.R.drawable.cardbackground_world);
            card2.setCardNumber(creditCards.get(1).getCardNumber());
            card2.setCardName(creditCards.get(1).getCardholderName());
            card2.setType(creditCards.get(1).getCardType() == 0 ? CardType.VISA : CardType.MASTERCARD);
            card2.setExpiryDate(new SimpleDateFormat("MM/yy").format(creditCards.get(1).getExpirationDate()));

        } else if (creditCards.size() == 1) {
            CreditCardView card1 = view.findViewById(R.id.card1);
            card1.setBackgroundResource(com.vinaygaba.creditcardview.R.drawable.cardbackground_world);
            card1.setCardNumber(creditCards.get(0).getCardNumber());
            card1.setCardName(creditCards.get(0).getCardholderName());
            card1.setType(creditCards.get(0).getCardType() == 0 ? CardType.VISA : CardType.MASTERCARD);
            card1.setExpiryDate(new SimpleDateFormat("MM/yy").format(creditCards.get(0).getExpirationDate()));

            CreditCardView card2 = view.findViewById(R.id.card2);
            llCards.removeView(card2);

        } else if (creditCards.size() == 0) {
            CreditCardView card1 = view.findViewById(R.id.card1);
            CreditCardView card2 = view.findViewById(R.id.card2);
            llCards.removeView(card1);
            llCards.removeView(card2);
        }
        diableHsvCardsRefresh();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void diableHsvCardsRefresh() {
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.mainAct_swipeRefreshLayout);
        hsvCards.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disable refreshing when the user starts touching the LinearLayout
                        swipeRefreshLayout.setEnabled(false);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Enable refreshing when the user stops touching the LinearLayout
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });
    }

}