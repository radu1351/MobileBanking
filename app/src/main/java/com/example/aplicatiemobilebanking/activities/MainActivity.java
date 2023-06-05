package com.example.aplicatiemobilebanking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.aplicatiemobilebanking.database.FirestoreManager;
import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Credit;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Deposit;
import com.example.aplicatiemobilebanking.classes.Request;
import com.example.aplicatiemobilebanking.classes.Transaction;
import com.example.aplicatiemobilebanking.classes.Transfer;
import com.example.aplicatiemobilebanking.classes.User;
import com.example.aplicatiemobilebanking.dialogs.AddCardDialog;
import com.example.aplicatiemobilebanking.dialogs.AddCreditDialog;
import com.example.aplicatiemobilebanking.dialogs.AddDepositDialog;
import com.example.aplicatiemobilebanking.dialogs.MobileTransferDialog;
import com.example.aplicatiemobilebanking.dialogs.PayDialog;
import com.example.aplicatiemobilebanking.dialogs.RequestDialog;
import com.example.aplicatiemobilebanking.dialogs.TransferDialog;
import com.example.aplicatiemobilebanking.dialogs.ViewBankAccountDialog;
import com.example.aplicatiemobilebanking.dialogs.ViewCreditDialog;
import com.example.aplicatiemobilebanking.dialogs.ViewDepositDialog;
import com.example.aplicatiemobilebanking.dialogs.ViewRequestDialog;
import com.example.aplicatiemobilebanking.fragments.CreditFragment;
import com.example.aplicatiemobilebanking.fragments.DepositFragment;
import com.example.aplicatiemobilebanking.fragments.HomeFragment;
import com.example.aplicatiemobilebanking.fragments.NoTransactionsFragment;
import com.example.aplicatiemobilebanking.fragments.ProfileFragment;
import com.example.aplicatiemobilebanking.fragments.RequestMoneyFragment;
import com.example.aplicatiemobilebanking.fragments.TransactionsFragment;
import com.example.aplicatiemobilebanking.fragments.TransferFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements PayDialog.OnTransactionAddedListener,
        AddCardDialog.AddCardListener, ViewBankAccountDialog.CreditCardListener,
        TransferDialog.TransferDialogListener, RequestDialog.RequestListener,
        ViewRequestDialog.RequestDialogListener, MobileTransferDialog.MobileTransferDialogListener,
        AddDepositDialog.DepositDialogListener, ViewDepositDialog.OnTerminateDepositListener,
        AddCreditDialog.AddCreditDialogListener, ViewCreditDialog.OnPayCreditListener {

    private FrameLayout fl;
    private Fragment currentFragment;
    private BottomNavigationView bottomNavigationView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private User user;
    private BankAccount bankAccount;
    private ArrayList<Transaction> transactions = new ArrayList<>(0);
    private ArrayList<Transfer> transfers = new ArrayList<Transfer>(0);
    private ArrayList<CreditCard> creditCards = new ArrayList<>(0);
    private ArrayList<Request> requests = new ArrayList<>(0);
    private ArrayList<Deposit> deposits = new ArrayList<>(0);
    private ArrayList<Credit> credits = new ArrayList<Credit>(0);

    FirestoreManager firestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadAllFromDatabase();
    }

    private void loadAllFromDatabase() {
        loadUser();

        firestoreManager = new FirestoreManager(user);
        firestoreManager.loadAllFromDatabase(new Runnable() {
            @Override
            public void run() {
                loadDataFromFirestoreManager();
                initComponents();
            }
        });
    }

    private void loadDataFromFirestoreManager() {
        this.bankAccount = firestoreManager.getBankAccount();
        this.transactions = firestoreManager.getTransactions();
        this.creditCards = firestoreManager.getCreditCards();
        this.transfers = firestoreManager.getTransfers();
        this.requests = firestoreManager.getRequests();
        this.deposits = firestoreManager.getDeposits();
        this.credits = firestoreManager.getCredits();
    }

    private void loadUser() {
        user = (User) getIntent().getSerializableExtra("USER");
    }


    private void initComponents() {
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

        swipeRefreshLayout = findViewById(R.id.mainAct_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                firestoreManager.reloadAllDataFromDatabase(new Runnable() {
                    @Override
                    public void run() {
                        loadDataFromFirestoreManager();
                        reopenCurrentFragment();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void reopenCurrentFragment() {
        if (currentFragment instanceof HomeFragment)
            openHomeFragment();
        else if (currentFragment instanceof TransactionsFragment)
            openTransactionsFragment();
        else if (currentFragment instanceof TransferFragment)
            openTransferFragment();
        else if (currentFragment instanceof ProfileFragment)
            openProfileFragment();
        else if (currentFragment instanceof RequestMoneyFragment)
            openRequestMoneyFragment();
        else if (currentFragment instanceof DepositFragment)
            openDepositFragment();
    }

    public void openHomeFragment() {
        Bundle bundle = new Bundle();
        sortTransactionsByDate();
        bundle.putSerializable("CREDITCARDS", creditCards);
        bundle.putSerializable("USER", user);
        bundle.putSerializable("BANKACCOUNT", bankAccount);
        bundle.putSerializable("TRANSACTIONS", transactions);
        bundle.putSerializable("DEPOSITS", deposits);
        bundle.putSerializable("CREDITS", credits);

        currentFragment = new HomeFragment();
        currentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, currentFragment)
                .commit();

    }

    private void openTransactionsFragment() {
        if (!transactions.isEmpty()) {
            Bundle bundle = new Bundle();
            sortTransactionsByDate();
            bundle.putSerializable("USER", user);
            bundle.putSerializable("BANKACCOUNT", bankAccount);
            bundle.putSerializable("TRANSACTIONS", transactions);
            bundle.putSerializable("CREDITCARDS", creditCards);

            currentFragment = new TransactionsFragment();
            currentFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainAct_fl, currentFragment)
                    .commit();
        } else {
            currentFragment = new NoTransactionsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainAct_fl, currentFragment)
                    .commit();
        }
    }

    public void openDepositFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("BANKACCOUNT", bankAccount);
        bundle.putSerializable("USER", user);
        bundle.putSerializable("DEPOSITS", deposits);
        currentFragment = new DepositFragment();
        currentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, currentFragment)
                .commit();
    }

    public void openCreditFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("BANKACCOUNT", bankAccount);
        bundle.putSerializable("USER", user);
        bundle.putSerializable("CREDITS", credits);
        currentFragment = new CreditFragment();
        currentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, currentFragment)
                .commit();
    }

   public void openTransferFragment() {
        Bundle bundle = new Bundle();
        sortTransfersByDate();
        sortRequests();
        bundle.putSerializable("USER", user);
        bundle.putSerializable("TRANSFERS", transfers);
        bundle.putSerializable("CREDITCARDS", creditCards);
        bundle.putSerializable("BANKACCOUNT", bankAccount);
        bundle.putSerializable("REQUESTS", requests);

        currentFragment = new TransferFragment();
        currentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainAct_fl, currentFragment)
                .commit();
    }

    public void openRequestMoneyFragment() {
        Bundle bundle = new Bundle();
        sortRequests();
        bundle.putSerializable("USER", user);
        bundle.putSerializable("BANKACCOUNT", bankAccount);
        bundle.putSerializable("REQUESTS", requests);

        currentFragment = new RequestMoneyFragment();
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

    private void sortRequests() {
        requests.sort(new Comparator<Request>() {
            @Override
            public int compare(Request r1, Request r2) {
                boolean r1IsSender = r1.getSenderIban().equals(bankAccount.getIban());
                boolean r2IsSender = r2.getSenderIban().equals(bankAccount.getIban());
                boolean r1IsRequester = r1.getRequesterIban().equals(bankAccount.getIban());
                boolean r2IsRequester = r2.getRequesterIban().equals(bankAccount.getIban());

                if (r1IsSender && r2IsSender) {
                    return r1.getDate().compareTo(r2.getDate());
                } else if (r1IsSender) {
                    return -1;
                } else if (r2IsSender) {
                    return 1;
                } else if (r1IsRequester && r2IsRequester) {
                    return compareByStateAndDate(r1, r2);
                } else if (r1IsRequester) {
                    return -1;
                } else if (r2IsRequester) {
                    return 1;
                }
                return 0;
            }

            private int compareByStateAndDate(Request r1, Request r2) {
                // In progress -> Accepted -> Declined
                if (r1.getState() != r2.getState()) {
                    return r1.getState() - r2.getState();
                } else {
                    return r1.getDate().compareTo(r2.getDate());
                }
            }
        });
    }

    @Override
    public void onTransactionAdded(Transaction transaction) {
        firestoreManager.addTransactionToDatabase(transaction);
        loadDataFromFirestoreManager();
        openTransferFragment();
    }

    @Override
    public void onCardAdded(CreditCard creditCard) {
        firestoreManager.addCardToDatabase(creditCard);
        loadDataFromFirestoreManager();
        openHomeFragment();
    }

    @Override
    public void onCreditCardsDismissed(ArrayList<CreditCard> creditCards) {
        firestoreManager.updateCreditCardDatabase(creditCards);
        loadDataFromFirestoreManager();
        openHomeFragment();
    }

    @Override
    public void onTransferCreated(Transfer transfer) {
        firestoreManager.addTransferToDatabase(transfer);
        loadDataFromFirestoreManager();
        openTransferFragment();
    }

    @Override
    public void onMobileTransferCreated(Transfer transfer) {
        firestoreManager.addTransferToDatabase(transfer);
        loadDataFromFirestoreManager();
        openTransferFragment();
    }

    @Override
    public void onRequestCreated(Request request) {
        firestoreManager.addRequestToDatabase(request);
        loadDataFromFirestoreManager();
        openRequestMoneyFragment();
    }

    @Override
    public void onDepositCreated(Deposit deposit) {
        firestoreManager.addDepositToDatabase(deposit);
        loadDataFromFirestoreManager();
        openDepositFragment();
    }

    @Override
    public void onAcceptRequest(Request request) {
        Log.d("ACCEPTED REQ", request.toString());
        if (request.getState() == 1) { // If accepted, the request becomes a transfer
            Transfer transfer = new Transfer(generateId(), request.getRequesterIban(), request.getAmount(),
                    2.5f, request.getDescription(), new Date(), request.getSenderIban());
            firestoreManager.addTransferToDatabase(transfer);
            firestoreManager.updateRequestInDatabase(request);
        } else if (request.getState() == 2) { // If declined, the request is only updated
            firestoreManager.updateRequestInDatabase(request);
        }
        loadDataFromFirestoreManager();
        openRequestMoneyFragment();
    }

    public String generateId() {
        Random rand = new Random();
        long min = 100000000000L;
        long max = 999999999999L;
        long randomNum = min + ((long) (rand.nextDouble() * (max - min)));
        return Long.toString(randomNum);
    }

    @Override
    public void onTerminateDeposit(Deposit deposit) {
        firestoreManager.removeDepositFromDatabase(deposit);
        loadDataFromFirestoreManager();
        openDepositFragment();
    }

    @Override
    public void onCreditAdded(Credit credit) {
        firestoreManager.addCreditToDatabase(credit);
        loadDataFromFirestoreManager();
        openCreditFragment();
    }

    @Override
    public void onPayCredit(Credit credit) {
        firestoreManager.payCreditInDatabase(credit);
        loadDataFromFirestoreManager();
        openCreditFragment();
    }
}