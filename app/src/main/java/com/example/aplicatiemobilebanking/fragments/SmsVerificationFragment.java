package com.example.aplicatiemobilebanking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukeshsolanki.OtpView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class SmsVerificationFragment extends Fragment {

    private User user;
    private BankAccount bankAccount;
    private ArrayList<CreditCard> creditCards;
    private TextView tvEnterCode, tvResendCode;

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private String verificationId;
    private String inputVerificationId;
    private OtpView otpView;
    private Button btContinue;

    public SmsVerificationFragment() {
    }

    public static SmsVerificationFragment newInstance() {
        SmsVerificationFragment fragment = new SmsVerificationFragment();
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
            creditCards = (ArrayList<CreditCard>) getArguments().getSerializable("CARDS");
        }
        setFirebaseAuth();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sms_verification, container, false);

        tvEnterCode = view.findViewById(R.id.smsVerificationFragment_tvEnterCode);
        tvEnterCode.setText(getString(R.string.code_sent, user.getPhoneNumber()));


        tvResendCode = view.findViewById(R.id.smsVerificationFragment_tvResendCode);
        tvResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpView.setText("");
                requestOtpVerification(user.getPhoneNumber());
            }
        });

        otpView = view.findViewById(R.id.smsVerificationFragment_otpView);
        btContinue = view.findViewById(R.id.smsVerificationFragment_btContinue);
        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputVerificationId = otpView.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, inputVerificationId);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("USER", user);
                                    bundle.putSerializable("CREDITCARDS", creditCards);
                                    Fragment cnpVerificationFragment = new CnpVerificationFragment();
                                    cnpVerificationFragment.setArguments(bundle);
                                    requireActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.verificationActivity_fl, cnpVerificationFragment)
                                            .commit();
                                } else {
                                }
                            }
                        });
            }
        });
        return view;
    }

    private void setFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
            }

            @Override
            public void onCodeSent(String sentVerificationId, PhoneAuthProvider.ForceResendingToken token) {
                verificationId = sentVerificationId;
            }
        };
        requestOtpVerification(user.getPhoneNumber());
    }

    private void requestOtpVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber) // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
                        .setActivity(getActivity()) // Activity (for callback binding)
                        .setCallbacks(verificationCallbacks) // Verification callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

}
