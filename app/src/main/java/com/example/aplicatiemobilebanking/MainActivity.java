package com.example.aplicatiemobilebanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Transaction;
import com.example.aplicatiemobilebanking.classes.Transfer;
import com.example.aplicatiemobilebanking.classes.User;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.vinaygaba.creditcardview.CardType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements PayDialogFragment.OnTransactionAddedListener, AddCardDialog.AddCardListener, ViewBankAccountDialog.CreditCardListener {

    private FrameLayout fl;
    private Fragment currentFragment;
    private BottomNavigationView bottomNavigationView;

    private User user;
    private BankAccount bankAccount;
    private ArrayList<Transaction> transactions = new ArrayList<>(0);
    private ArrayList<Transfer> transfers = new ArrayList<Transfer>(0);
    private ArrayList<CreditCard> creditCards = new ArrayList<>(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadTransactionsFromDatabase();
        loadTransfersFromDatabase();
        loadCardsFromDatabase();
        loadBankAccountFromDatabase();

        initComponents();

    }


    private void initComponents() {
        user = new User("John","Terry","5010813410083",
                "Aleea Livezilor 74B","072134523144","john@gmail.com","1234");

        openHomeFragment();

        fl = findViewById(R.id.mainAct_fl);

        bottomNavigationView = findViewById(R.id.mainAct_navView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.menu_home) openHomeFragment();
                if (item.getItemId() == R.id.menu_transactions) openTransactionsFragment();
                if (item.getItemId() == R.id.menu_transfer) openTransferFragment();
                if (item.getItemId() == R.id.menu_profile) openProfileFragment();

                return true;
            }
        });

    }

    private void loadBankAccountFromDatabase(){
        bankAccount = new BankAccount("R008BTRLRONCRT0279986", "BTLRON", 6250.50f, "RON");
    }

    private void loadTransactionsFromDatabase() {
        // ********* TEST *********
        try {
            Transaction t1 = new Transaction("Carrefour", "Groceries", 100.0f,
                    new SimpleDateFormat("dd MMM HH:mm").parse("11 Apr 22:40"));
            Transaction t2 = new Transaction("About You", "Shopping", 200.50f,
                    new SimpleDateFormat("dd MMM HH:mm").parse("10 Apr 11:15"));
            Transaction t3 = new Transaction("STB", "Transport", 80.00f,
                    new SimpleDateFormat("dd MMM HH:mm").parse("30 Apr 15:30"));
            Transaction t4 = new Transaction("Mega Image", "Groceries", 230.0f,
                    new SimpleDateFormat("dd MMM HH:mm").parse("20 Apr 15:30"));
            Transaction t5 = new Transaction("Nomad Skybar", "Restaurant", 245.0f,
                    new SimpleDateFormat("dd MMM HH:mm").parse("01 May 12:20"));
            Transaction t6 = new Transaction("Trattoria Il Calcio", "Restaurant", 325.0f,
                    new SimpleDateFormat("dd MMM HH:mm").parse("30 Apr 20:14"));
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

    private void loadTransfersFromDatabase() {
        try {
            Transfer t1 = new Transfer(500, "RO08BTRLRONCRT02799871", "RO08BTRLRONCRT02799871",
                    "Nota restuanrant", new SimpleDateFormat("dd MMM").parse("15 Mar"));
            Transfer t2 = new Transfer(300, "RO08BTRLRONCRT02799871", "RO08BTRLRONCRT02799871",
                    "Nota restuanrant", new SimpleDateFormat("dd MMM").parse("15 Mar"));
            Transfer t3 = new Transfer(100, "RO08BTRLRONCRT02799871", "RO08BTRLRONCRT02799871",
                    "Nota restuanrant", new SimpleDateFormat("dd MMM").parse("12 Mar"));
            Transfer t4 = new Transfer(650, "RO08BTRLRONCRT02799871", "RO08BTRLRONCRT02799871",
                    "Nota restuanrant", new SimpleDateFormat("dd MMM").parse("22 Apr"));
            transfers.add(t1);
            transfers.add(t2);
            transfers.add(t3);
            transfers.add(t4);
            sortTransfersByDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void loadCardsFromDatabase() {
        try {
            CreditCard creditCard1 = new CreditCard("4140498018805562", "John Terry",
                    new SimpleDateFormat("dd MMM yyyy").parse("15 Mar 2025"), 445, CardType.VISA);
            CreditCard creditCard2 = new CreditCard("4090000055421685", "John Terry",
                    new SimpleDateFormat("dd MMM yyyy").parse("22 Aug 2028"), 395, CardType.MASTERCARD);
            creditCards.add(creditCard1);
            //creditCards.add(creditCard2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    private void openHomeFragment() {
        Bundle bundle = new Bundle();
        sortTransactionsByDate();
        bundle.putSerializable("CREDITCARDS", creditCards);
        bundle.putSerializable("USER", user);
        bundle.putSerializable("BANKACCOUNT", bankAccount);
        bundle.putSerializable("TRANSACTIONS", transactions);

        currentFragment = new HomeFragment();
        currentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, currentFragment)
                .commit();

    }

    private void openTransactionsFragment() {
        Bundle bundle = new Bundle();
        sortTransactionsByDate();
        bundle.putSerializable("USER", user);
        bundle.putSerializable("TRANSACTIONS", transactions);

        currentFragment = new TransactionsFragment();
        currentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, currentFragment)
                .commit();
    }

    private void openTransferFragment() {
        Bundle bundle = new Bundle();
        sortTransfersByDate();
        bundle.putSerializable("USER", user);
        bundle.putSerializable("TRANSFERS", transfers);

        currentFragment = new TransferFragment();
        currentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, currentFragment)
                .commit();
    }

    private void openProfileFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("USER", user);
        currentFragment = new ProfileFragment();
        currentFragment.setArguments(bundle);
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

    private void sortTransfersByDate() {
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

    //Adaugare transactie din PayDialogFragment
    @Override
    public void onTransactionAdded(Transaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public void onCardAdded(CreditCard creditCard) {
        creditCards.add(creditCard);
        openHomeFragment();
    }

    public void clickTransactionsMenuItem() {
        BottomNavigationItemView bottomNavigationItemView = findViewById(R.id.menu_transactions);
        bottomNavigationItemView.performClick();
    }

    @Override
    public void onCreditCardsDismissed(ArrayList<CreditCard> creditCards) {
        this.creditCards = creditCards;
        openHomeFragment();
    }
}
