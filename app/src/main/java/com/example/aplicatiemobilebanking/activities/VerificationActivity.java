package com.example.aplicatiemobilebanking.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Credit;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.User;
import com.example.aplicatiemobilebanking.database.FirestoreManager;
import com.example.aplicatiemobilebanking.fragments.DepositFragment;
import com.example.aplicatiemobilebanking.fragments.SmsVerificationFragment;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class VerificationActivity extends AppCompatActivity {

    private Fragment currentFragment;
    private FirestoreManager firestoreManager;

    private User user;
    private BankAccount bankAccount;
    private ArrayList<CreditCard> creditCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        loadUser();
        firestoreManager = new FirestoreManager(user);
        firestoreManager.loadAllFromDatabase(new Runnable() {
            @Override
            public void run() {
                bankAccount = firestoreManager.getBankAccount();
                creditCards = firestoreManager.getCreditCards();
                openSmsVerificationFragment();
            }
        });


    }

    private void openSmsVerificationFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("USER", user);
        bundle.putSerializable("BANKACCOUNT", bankAccount);
        bundle.putSerializable("CARDS", creditCards);
        currentFragment = new SmsVerificationFragment();
        currentFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.verificationActivity_fl, currentFragment)
                .commit();
    }

    private void loadUser() {
        user = (User) getIntent().getSerializableExtra("USER");
    }
}