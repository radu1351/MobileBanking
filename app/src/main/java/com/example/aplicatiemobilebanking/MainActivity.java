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

import com.example.aplicatiemobilebanking.classes.Transaction;
import com.example.aplicatiemobilebanking.classes.Transfer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.vinaygaba.creditcardview.CreditCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private FrameLayout fl;
    private Fragment currentFragment;
    private BottomNavigationView bottomNavigationView;

    private ArrayList<Transaction> transactions = new ArrayList<>(0);
    private ArrayList<Transfer> transfers = new ArrayList<Transfer>(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        loadTransactionsFromDatabase();
        loadTransfersFromDatabase();
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

    private void loadTransactionsFromDatabase() {
        // ********* TEST *********
        try {
            Transaction t1 = new Transaction(1, "Carrefour", "Groceries", 100.0f,
                    new SimpleDateFormat("dd MMM").parse("24 Apr"));
            Transaction t2 = new Transaction(2, "About You", "Shopping", 200.50f,
                    new SimpleDateFormat("dd MMM").parse("23 Apr"));
            Transaction t3 = new Transaction(3, "STB", "Transport", 80.00f,
                    new SimpleDateFormat("dd MMM").parse("30 Apr"));
            Transaction t4 = new Transaction(4, "Mega Image", "Groceries", 230.0f,
                    new SimpleDateFormat("dd MMM").parse("15 Mar"));
            Transaction t5 = new Transaction(5, "Nomad Skybar", "Restaurant", 245.0f,
                    new SimpleDateFormat("dd MMM").parse("29 Mar"));
            Transaction t6 = new Transaction(6, "Trattoria Il Calcio", "Restaurant", 325.0f,
                    new SimpleDateFormat("dd MMM").parse("30 Apr"));
            transactions.add(t1);
            transactions.add(t2);
            transactions.add(t3);
            transactions.add(t4);
            transactions.add(t5);
            transactions.add(t6);
            sortTransactionsByDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // ***********************
    }

    private void loadTransfersFromDatabase(){
        try {
            Transfer t1 = new Transfer(1,500,"RO08BTRLRONCRT02799871","RO08BTRLRONCRT02799871",
                    "Nota restuanrant", new SimpleDateFormat("dd MMM").parse("15 Mar"));
            Transfer t2 = new Transfer(2,300,"RO08BTRLRONCRT02799871","RO08BTRLRONCRT02799871",
                    "Nota restuanrant", new SimpleDateFormat("dd MMM").parse("15 Mar"));
            Transfer t3= new Transfer(2,100,"RO08BTRLRONCRT02799871","RO08BTRLRONCRT02799871",
                    "Nota restuanrant", new SimpleDateFormat("dd MMM").parse("12 Mar"));
            transfers.add(t1);
            transfers.add(t2);
            transfers.add(t3);
            sortTransfersByDate();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void openHomeFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("TRANSACTIONS", transactions);
        bundle.putSerializable("NAME", "John Terry");

        currentFragment = new HomeFragment();
        currentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, currentFragment)
                .commit();

    }

    private void openStatisticsFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("TRANSACTIONS", transactions);

        currentFragment = new StatisticsFragment();
        currentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, currentFragment)
                .commit();
    }

    private void openTransferFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("TRANSFERS", transfers);

        currentFragment = new TransferFragment();
        currentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, currentFragment)
                .commit();
    }

    private void openStocksFragment() {
        currentFragment = new StocksFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, currentFragment)
                .commit();
    }

    private void sortTransactionsByDate() {
        transactions.sort(new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                if (t1.getDate().compareTo(t2.getDate()) < 0)
                    return 1;
                else if (t1.getDate().compareTo(t2.getDate()) > 0)
                    return -1;
                else return 0;
            }
        });
    }

    private void sortTransfersByDate(){
        transfers.sort(new Comparator<Transfer>() {
            @Override
            public int compare(Transfer t1, Transfer t2) {
                if (t1.getDate().compareTo(t2.getDate()) < 0)
                    return 1;
                else if (t1.getDate().compareTo(t2.getDate()) > 0)
                    return -1;
                else return 0;
            }
        });
    }

    private void hideBottomNavigation() {
        View decorView = getWindow().getDecorView();
        WindowInsetsController insetsController = decorView.getWindowInsetsController();
        insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        insetsController.hide(WindowInsets.Type.navigationBars());
    }


}
