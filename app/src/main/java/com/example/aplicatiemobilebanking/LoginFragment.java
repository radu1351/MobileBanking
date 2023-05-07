package com.example.aplicatiemobilebanking;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class LoginFragment extends Fragment {

    public TextInputEditText tietEmail;
    public TextInputEditText tietPassword;
    public Switch switchRemember;
    public Button btLogin, btRegister;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment getInstance(String param1, String param2) {
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
        View view =  inflater.inflate(R.layout.fragment_login, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view){
        tietEmail = view.findViewById(R.id.loginFrag_tietEmail);
        tietPassword =  view.findViewById(R.id.loginFrag_tietPass);
        switchRemember =  view.findViewById(R.id.loginFrag_switchRem);
        btLogin =  view.findViewById(R.id.loginFrag_btLogin);
        btRegister =  view.findViewById(R.id.loginFrag_btRegister);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Firebase...
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterFragment();
            }
        });

    }

    public void openRegisterFragment(){
        Fragment registerFragment = new RegisterFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.loginAct_fl, registerFragment)
                .commit();
    }

}