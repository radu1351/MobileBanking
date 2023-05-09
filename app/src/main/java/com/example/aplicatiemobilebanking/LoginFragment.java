package com.example.aplicatiemobilebanking;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Transaction;
import com.example.aplicatiemobilebanking.classes.Transfer;
import com.example.aplicatiemobilebanking.classes.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class LoginFragment extends Fragment {

    public TextInputEditText tietEmail;
    public TextInputEditText tietPassword;
    public Switch switchRemember;
    public Button btLogin, btRegister;

    //User and Account information
    User user;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment getInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        tietEmail = view.findViewById(R.id.loginFrag_tietEmail);
        tietPassword = view.findViewById(R.id.loginFrag_tietPass);
        switchRemember = view.findViewById(R.id.loginFrag_switchRem);
        btLogin = view.findViewById(R.id.loginFrag_btLogin);
        btRegister = view.findViewById(R.id.loginFrag_btRegister);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tietEmail.getText().toString();
                String password = tietPassword.getText().toString();
                logInUser(email, password);
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterFragment();
            }
        });

    }

    private void logInUser(String email, String password) {
        // Create a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the "users" collection for the given email and password
        Query query = db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password);

        // Execute the query asynchronously
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Check if there is a user with the given email and password
                if (!task.getResult().isEmpty()) {
                    // User found, log them in
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    user = task.getResult().getDocuments().get(0).toObject(User.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                } else {
                    // No user found
                }
            } else {
                // Query failed, show error message
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }


    private void openRegisterFragment() {
        Fragment registerFragment = new RegisterFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.loginAct_fl, registerFragment)
                .commit();
    }

}