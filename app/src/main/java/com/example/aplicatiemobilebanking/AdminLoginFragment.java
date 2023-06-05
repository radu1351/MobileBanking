package com.example.aplicatiemobilebanking;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class AdminLoginFragment extends Fragment {

    public TextInputEditText tietId;
    public TextInputEditText tietPassword;
    public Button btLogin, btUserLogin;

    private final String SHARED_PREFS_NAME = "com.example.aplicatiemobilebanking.admin";
    private final String PREF_ID = "PREF_ID";
    private final String PREF_PASSWORD = "PREF_PASSWORD";

    private SharedPreferences sharedPreferences;
    private Switch switchRemember;

    public AdminLoginFragment() {
    }

    public static AdminLoginFragment newInstance() {
        AdminLoginFragment fragment = new AdminLoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_admin_login, container, false);

        btUserLogin = view.findViewById(R.id.loginAdminFrag_btUserLogin);
        btUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginFragment();
            }
        });

        tietId = view.findViewById(R.id.loginAdminFrag_tietId);
        tietPassword = view.findViewById(R.id.loginAdminFrag_tietPass);
        btLogin = view.findViewById(R.id.loginAdminFrag_btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Id = tietId.getText().toString();
                String password = tietPassword.getText().toString();
                logInUser(Id, password);
            }
        });

        switchRemember = view.findViewById(R.id.loginAdminFrag_switchRem);
        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String savedId = prefs.getString(PREF_ID, "");
        String savedPassword = prefs.getString(PREF_PASSWORD, "");
        if (!savedId.isEmpty() && !savedPassword.isEmpty()) {
            tietId.setText(savedId);
            tietPassword.setText(savedPassword);
            switchRemember.setChecked(true);
            logInUser(savedId, savedPassword);
        }

        return view;
    }

    private void logInUser(String id, String password) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference adminCollection = firestore.collection("admins");
        adminCollection
                .whereEqualTo("id", id)
                .whereEqualTo("password", password)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).edit();
                            if (switchRemember.isChecked()) {
                                //Save the id and password for the lext logIn
                                editor.putString(PREF_ID, id);
                                editor.putString(PREF_PASSWORD, password);
                            } else {
                                // Clear id email and password
                                editor.clear();
                            }
                            editor.apply();

                            QuerySnapshot snapshot = task.getResult();
                            if (snapshot != null && !snapshot.isEmpty()) {
                                openAdminMainActivity();
                            }
                        } else {
                            Exception exception = task.getException();
                            exception.printStackTrace();
                        }
                    }
                });
    }

    private void openLoginFragment() {
        Fragment registerFragment = new LoginFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.loginAct_fl, registerFragment)
                .commit();
    }

    private void openAdminMainActivity(){
        Intent intent = new Intent(getContext(), AdminMainActivity.class);
        startActivity(intent);
    }
}