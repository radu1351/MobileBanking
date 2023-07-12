package com.example.aplicatiemobilebanking.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.User;
import com.mukeshsolanki.OtpView;

import java.util.ArrayList;


public class CnpVerificationFragment extends Fragment {

    private Button btContinue;
    private OtpView otpView;

    private ArrayList<CreditCard> creditCards;
    private User user;

    public CnpVerificationFragment() {
    }


    public static CnpVerificationFragment newInstance() {
        CnpVerificationFragment fragment = new CnpVerificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            creditCards = (ArrayList<CreditCard>) getArguments().getSerializable("CREDITCARDS");
            user = (User) getArguments().getSerializable("USER");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cnp_verification, container, false);

        otpView = view.findViewById(R.id.cnpVerificationFragment_otpView);
        btContinue = view.findViewById(R.id.cnpVerificationFragment_btContinue);
        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (otpView.getText().toString().equals(user.getIdentificationNumber().
                        substring(user.getIdentificationNumber().length() - 5))) {

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CREDITCARDS", creditCards);
                    bundle.putSerializable("USER", user);
                    Fragment creditCardVerificationFragment = new creditCardVerificationFragment();
                    creditCardVerificationFragment.setArguments(bundle);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.verificationActivity_fl, creditCardVerificationFragment)
                            .commit();
                }
            }
        });

        return view;
    }
}