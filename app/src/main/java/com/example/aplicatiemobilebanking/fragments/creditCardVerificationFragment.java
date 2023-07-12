package com.example.aplicatiemobilebanking.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.activities.MainActivity;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.User;
import com.mukeshsolanki.OtpView;
import com.vinaygaba.creditcardview.CardType;
import com.vinaygaba.creditcardview.CreditCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class creditCardVerificationFragment extends Fragment {

    private final String SHARED_PREFS_NAME = "com.example.aplicatiemobilebanking";
    private final String FIRST_LOGIN = "FIRST_LOGIN";

    private User user;
    private ArrayList<CreditCard> creditCards;
    private CreditCardView creditCardView;
    private Button btContinue;
    private OtpView otpView;


    public creditCardVerificationFragment() {

    }

    public static creditCardVerificationFragment newInstance() {
        creditCardVerificationFragment fragment = new creditCardVerificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            user = (User)getArguments().getSerializable("USER");
            creditCards = (ArrayList<CreditCard>) getArguments().getSerializable("CREDITCARDS");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit_card_verification, container, false);

        initialiseCreditCardView(view);

        otpView = view.findViewById(R.id.creditCardVerificationFragment_otpView);
        btContinue = view.findViewById(R.id.creditCardVerificationFragment_btContinue);
        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otpView.getText().toString().equals(creditCards.get(0).getCardNumber()
                        .substring(creditCards.get(0).getCardNumber().length() - 5))) {
                    setFirstLoginFalse();
                    // Open Main Activity
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    private void initialiseCreditCardView(View view) {
        creditCardView = view.findViewById(R.id.creditCardVerificationFragment_creditCardView);
        creditCardView.setBackgroundResource(com.vinaygaba.creditcardview.R.drawable.cardbackground_world);
        creditCardView.setCardNumber(creditCards.get(0).getCardNumber().substring(0, 11) + "*****");
        creditCardView.setCardName(creditCards.get(0).getCardholderName());
        creditCardView.setType(creditCards.get(0).getCardType() == 0 ? CardType.VISA : CardType.MASTERCARD);
        creditCardView.setExpiryDate(new SimpleDateFormat("MM/yy").format(creditCards.get(0).getExpirationDate()));
    }

    private void setFirstLoginFalse() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_LOGIN, false).apply();
    }
}