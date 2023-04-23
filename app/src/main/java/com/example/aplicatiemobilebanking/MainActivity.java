package com.example.aplicatiemobilebanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.vinaygaba.creditcardview.CreditCardView;

public class MainActivity extends AppCompatActivity {

    private FrameLayout fl;
    private Fragment homeFragment, statisticsFragment, transferFragment, stocksFragment;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

    }


    private void initComponents() {
        openHomeFragment();

        fl = findViewById(R.id.mainAct_fl);

        bottomNavigationView = findViewById(R.id.mainAct_navView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.menu_home) openHomeFragment();
                if (item.getItemId() == R.id.menu_statistics) openStatisticsFragment();
                if (item.getItemId() == R.id.menu_transfer) openTransferFragment();
                if (item.getItemId() == R.id.menu_stocks) openStocksFragment();

                return true;
            }

        });

    }

    private void openHomeFragment() {
        if (homeFragment == null)
            homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, homeFragment)
                .commit();

    }

    private void openStatisticsFragment() {
        if (statisticsFragment == null)
            statisticsFragment = new StatisticsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, statisticsFragment)
                .commit();
    }

    private void openTransferFragment() {
        if (transferFragment == null)
            transferFragment = new TransferFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, transferFragment)
                .commit();
    }

    private void openStocksFragment() {
        if (stocksFragment == null)
            stocksFragment = new StocksFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, stocksFragment)
                .commit();
    }

    private void hideBottomNavigation() {
        View decorView = getWindow().getDecorView();
        WindowInsetsController insetsController = decorView.getWindowInsetsController();
        insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        insetsController.hide(WindowInsets.Type.navigationBars());
    }
}
