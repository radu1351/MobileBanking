package com.example.aplicatiemobilebanking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.vinaygaba.creditcardview.CreditCardView;

public class MainActivity extends AppCompatActivity {

    FrameLayout fl;
    public Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        openHomeFragment();


    }

    private void initComponents() {
        fl = findViewById(R.id.mainAct_fl);
        openHomeFragment();
    }

    private void openHomeFragment() {
        if (!(currentFragment instanceof HomeFragment)) {
            currentFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainAct_fl, currentFragment)
                    .commit();
        }
    }
}
