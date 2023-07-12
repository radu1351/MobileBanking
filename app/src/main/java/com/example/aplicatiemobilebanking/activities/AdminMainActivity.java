package com.example.aplicatiemobilebanking.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.aplicatiemobilebanking.database.FirestoreAdminManager;
import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Credit;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Deposit;
import com.example.aplicatiemobilebanking.classes.User;
import com.example.aplicatiemobilebanking.dialogs.ManageAccountDialog;
import com.example.aplicatiemobilebanking.dialogs.ViewCreditDialog;
import com.example.aplicatiemobilebanking.dialogs.ViewDepositDialog;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.aplicatiemobilebanking.list_view_adapters.AccountsAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminMainActivity extends AppCompatActivity implements ViewCreditDialog.OnPayCreditListener,
        ViewDepositDialog.OnTerminateDepositListener, ManageAccountDialog.CreditCardListener,
        ManageAccountDialog.TerminateAccountListener {

    private ArrayList<BankAccount> bankAccounts = new ArrayList<>(0);
    private ArrayList<User> users = new ArrayList<User>(0);

    private Button btLogout;
    private ListView lvAccounts;
    private TextInputEditText tietSearch;

    FirestoreAdminManager firestoreAdminManager;
    private final String SHARED_PREFS_NAME = "com.example.aplicatiemobilebanking.admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        loadAllFromDatabase();
    }

    private void loadAllFromDatabase() {
        firestoreAdminManager = new FirestoreAdminManager();

        firestoreAdminManager.loadAllAccountsFromDatabase(new Runnable() {
            @Override
            public void run() {
                loadDataFromFirestoreAdminManager();
                initComponents();
            }
        });
    }

    private void loadDataFromFirestoreAdminManager() {
        this.bankAccounts = firestoreAdminManager.getBankAccounts();
        this.users = firestoreAdminManager.getUsers();
    }

    private void initComponents() {
        btLogout = findViewById(R.id.adminMainActivity_btLogout);
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        tietSearch = findViewById(R.id.adminMainActivity_tietSearch);
        tietSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                filterUsers(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
        loadAccountsListView(this, bankAccounts);
    }

    private void filterUsers(String text) {
        ArrayList<BankAccount> temp = new ArrayList();
        ArrayList<User> tempUsers = new ArrayList();
        for (User user : users) {
            if (user.getFullName().toLowerCase().contains(text.toLowerCase())) {
                tempUsers.add(user);
                for (BankAccount acc : bankAccounts) {
                    if (acc.getUserPersonalID().equals(user.getIdentificationNumber())) {
                        temp.add(acc);
                        break;
                    }
                }
            }
        }
        loadAccountsListView(this, temp);
    }

    private void loadAccountsListView(AppCompatActivity activity, ArrayList<BankAccount> bankAccounts) {
        lvAccounts = findViewById(R.id.adminMainActivity_lvAccounts);
        AccountsAdapter accountsAdapter = new AccountsAdapter(activity, R.layout.lv_accounts_row_item,
                bankAccounts, users, getLayoutInflater());
        lvAccounts.setAdapter(accountsAdapter);
    }


    @Override
    public void onPayCredit(Credit credit) {
        firestoreAdminManager.payCreditInDatabase(credit);
        ((AccountsAdapter) lvAccounts.getAdapter()).openLastAccountClicked();
    }

    @Override
    public void onTerminateDeposit(Deposit deposit) {
        firestoreAdminManager.terminateDepositInDatabase(deposit);
        ((AccountsAdapter) lvAccounts.getAdapter()).openLastAccountClicked();
    }

    @Override
    public void onCreditCardsDismissed(ArrayList<CreditCard> creditCards, BankAccount bankAccount) {
        firestoreAdminManager.updateCreditCardDatabase(creditCards, bankAccount);
    }

    @Override
    public void onAccountTerminated(BankAccount terminatedAccount) {
        Log.d("TERMINATED ACCOUNT", terminatedAccount.toString());
        firestoreAdminManager.deleteBankAccountFromDatabase(terminatedAccount);
        loadDataFromFirestoreAdminManager();
        ((AccountsAdapter) lvAccounts.getAdapter()).notifyDataSetChanged();
    }
}