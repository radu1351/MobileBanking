package com.example.aplicatiemobilebanking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public FrameLayout flLogin;
    public Fragment currentFragment ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponents();


    }


    private void initComponents(){
        flLogin = findViewById(R.id.loginAct_fl);
        openLoginFragment();
    }


    public void openLoginFragment(){
        if( ! (currentFragment instanceof LoginFragment) ) {
            currentFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.loginAct_fl, currentFragment)
                    .commit();
        }
    }

}