package com.example.aplicatiemobilebanking;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.aplicatiemobilebanking.classes.User;

public class ProfileFragment extends Fragment {
    private TextView tvFirstName, tvLastName, tvIdentificationNumber,
            tvAdress,tvPhone,tvEmail;
    private Button btLogOut;
    private User user;

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User)getArguments().getSerializable("USER");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        tvFirstName = view.findViewById(R.id.profileFrag_tvFirstName);
        tvLastName = view.findViewById(R.id.profileFrag_tvLastName);
        tvIdentificationNumber = view.findViewById(R.id.profileFrag_tvIdentificationNumber);
        tvAdress = view.findViewById(R.id.profileFrag_tvAddress);
        tvPhone = view.findViewById(R.id.profileFrag_tvPhoneNumber);
        tvEmail = view.findViewById(R.id.profileFrag_tvEmail);

        tvFirstName.setText(user.getFirstName());
        tvLastName.setText(user.getLastName());
        tvIdentificationNumber.setText(user.getIdentificationNumber());
        tvAdress.setText(user.getAdress());
        tvPhone.setText(user.getPhoneNumber());
        tvEmail.setText(user.getEmail());

        btLogOut = view.findViewById(R.id.profileFrag_btLogOut);
        btLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}