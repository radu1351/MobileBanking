package com.example.aplicatiemobilebanking;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Transaction;
import com.example.aplicatiemobilebanking.classes.Transfer;
import com.example.aplicatiemobilebanking.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity implements PayDialog.OnTransactionAddedListener,
        AddCardDialog.AddCardListener, ViewBankAccountDialog.CreditCardListener,
        TransferDialog.TransferDialogListener {

    private FrameLayout fl;
    private Fragment currentFragment;
    private BottomNavigationView bottomNavigationView;

    private User user;
    private BankAccount bankAccount;
    private ArrayList<Transaction> transactions = new ArrayList<>(0);
    private ArrayList<Transfer> transfers = new ArrayList<Transfer>(0);
    private ArrayList<CreditCard> creditCards = new ArrayList<>(0);

    private boolean bankAccountLoaded = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadAllFromDatabase();
    }


    private void loadAllFromDatabase() {
        loadUser();

        CompletableFuture<BankAccount> bankAccountFuture = new CompletableFuture<>();
        loadBankAccountFromDatabase(new OnSuccessListener<BankAccount>() {
            @Override
            public void onSuccess(BankAccount bankAccountFromDatabase) {
                bankAccountFuture.complete(bankAccountFromDatabase);
            }
        });

        CompletableFuture<Void> allFutures = bankAccountFuture.thenCompose(bankAccount -> {
            // store the bank account
            this.bankAccount = bankAccount;

            // load the rest of the data
            CompletableFuture<List<CreditCard>> creditCardFuture = new CompletableFuture<>();
            loadCardsFromDatabase(new OnSuccessListener<List<CreditCard>>() {
                @Override
                public void onSuccess(List<CreditCard> creditCardsFromDatabase) {
                    creditCardFuture.complete(creditCardsFromDatabase);
                }
            });

            CompletableFuture<List<Transaction>> transactionsFuture = new CompletableFuture<>();
            loadTransactionsFromDatabase(new OnSuccessListener<List<Transaction>>() {
                @Override
                public void onSuccess(List<Transaction> transactionsFromDatabase) {
                    transactionsFuture.complete(transactionsFromDatabase);
                }
            });

            CompletableFuture<List<Transfer>> transfersFuture = new CompletableFuture<>();
            loadTransfersFromDatabase(new OnSuccessListener<List<Transfer>>() {
                @Override
                public void onSuccess(List<Transfer> transfersFromDatabase) {
                    transfersFuture.complete(transfersFromDatabase);
                }
            });

            // wait for all the futures to complete
            return CompletableFuture.allOf(creditCardFuture, transactionsFuture, transfersFuture)
                    .thenAccept(v -> {
                        creditCards = new ArrayList<>(creditCardFuture.join());
                        transactions = new ArrayList<>(transactionsFuture.join());
                        transfers = new ArrayList<>(transfersFuture.join());
                    });
        });

        // initialize the components after all the futures complete
        allFutures.thenRun(this::initComponents);
    }



    private void loadUser() {
        user = (User) getIntent().getSerializableExtra("USER");
    }

    private void loadBankAccountFromDatabase(OnSuccessListener<BankAccount> callback) {
        db.collection("bankAccounts")
                .whereEqualTo("userPersonalID", user.getIdentificationNumber())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BankAccount bankAccount = document.toObject(BankAccount.class);
                                Log.d(TAG, "BANKACCOUNT:" + bankAccount.toString());
                                callback.onSuccess(bankAccount);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void loadCardsFromDatabase(OnSuccessListener<List<CreditCard>> callback) {
        CollectionReference creditCardsCollection = FirebaseFirestore.getInstance().collection("creditCards");

        creditCardsCollection.whereEqualTo("bankAccountIban", bankAccount.getIban()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<CreditCard> creditCards = new ArrayList<>(2);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CreditCard creditCard = document.toObject(CreditCard.class);
                                // Add credit card to list, up to max size of 2
                                if (creditCards.size() < 2) {
                                    creditCards.add(creditCard);
                                }
                            }
                            Log.d("CREDIT CARDS _____ ", creditCards.toString());
                            callback.onSuccess(creditCards);
                        } else {
                            Log.d(TAG, "Error getting credit cards: ", task.getException());
                        }
                    }
                });
    }


    private void loadTransactionsFromDatabase(OnSuccessListener<List<Transaction>> callback) {
        db.collection("transactions")
                .whereEqualTo("bankAccountIban", bankAccount.getIban())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Transaction> transactions = new ArrayList<>(0);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Transaction transaction = document.toObject(Transaction.class);
                                transactions.add(transaction);
                            }
                            callback.onSuccess(transactions);
                        } else {
                            Log.w(TAG, "Error getting transactions", task.getException());
                        }
                    }
                });
    }


    private void loadTransfersFromDatabase(OnSuccessListener<List<Transfer>> callback) {
        CollectionReference transfersRef = db.collection("transfers");

        Query query = transfersRef.whereEqualTo("bankAccountIban", bankAccount.getIban());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Transfer> transfers = new ArrayList<>(0);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Convert the document to a Transfer object and add it to the list
                        Transfer transfer = document.toObject(Transfer.class);
                        transfers.add(transfer);
                    }
                    callback.onSuccess(transfers);
                } else {
                    Log.w(TAG, "Error getting transfers", task.getException());
                }
            }
        });
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

    }

    private void addCardToDatabase(CreditCard creditCard) {
        db.collection("creditCards").document(creditCard.getCardNumber())
                .set(creditCard)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Credit card added successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle error adding credit card
                    }
                });
    }

    private void updateCreditCardDatabase(ArrayList<CreditCard> creditCards) {
        // Get a reference to the Firestore collection for credit cards
        CollectionReference creditCardsCollection = db.collection("creditCards");

        // Delete the old credit cards that match the bank account IBAN
        Query query = creditCardsCollection.whereEqualTo("bankAccountIban", bankAccount.getIban());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    batch.delete(document.getReference());
                }
                batch.commit().addOnSuccessListener(aVoid -> {
                    // Add the new credit cards to the collection
                    for (CreditCard card : creditCards) {
                        creditCardsCollection.document(card.getCardNumber()).set(card);
                    }
                }).addOnFailureListener(e -> {
                    // Handle error if deleting old credit cards fails
                    Log.e(TAG, "Error deleting old credit cards", e);
                });
            } else {
                // Handle error if querying old credit cards fails
                Log.e(TAG, "Error querying old credit cards", task.getException());
            }
        });
    }


    public void addTransactionToDatabase(Transaction transaction) {
        CollectionReference transactionsCollection = db.collection("transactions");

        transactionsCollection.document(transaction.getId()).set(transaction)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Transaction added successfully");

                    CollectionReference bankAccountsCollection = db.collection("bankAccounts");

                    bankAccountsCollection.document(bankAccount.getIban())
                            .update("balance", FieldValue.increment(-transaction.getAmmount()))
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d(TAG, "Balance updated successfully");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error updating balance", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding transaction", e);
                });
    }


    public void addTransferToDatabase(Transfer transfer) {
        CollectionReference transactionsCollection = db.collection("transfers");
        transactionsCollection.document(transfer.getId()).set(transfer)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    Log.d(TAG, "Transfer added successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Log.e(TAG, "Error adding transfer", e);
                });
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
        bundle.putSerializable("CREDITCARDS", creditCards);

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
        bundle.putSerializable("CREDITCARDS", creditCards);
        bundle.putSerializable("BANKACCOUNT", bankAccount);

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

    //Adaugare transactie din PayDialogFragment
    @Override
    public void onTransactionAdded(Transaction transaction) {
        transactions.add(transaction);
        bankAccount.reduceBalance(transaction.getAmmount());
        addTransactionToDatabase(transaction);
    }

    @Override
    public void onCardAdded(CreditCard creditCard) {
        creditCards.add(creditCard);
        addCardToDatabase(creditCard);
        openHomeFragment();
    }

    public void clickTransactionsMenuItem() {
        BottomNavigationItemView bottomNavigationItemView = findViewById(R.id.menu_transactions);
        bottomNavigationItemView.performClick();
    }

    @Override
    public void onCreditCardsDismissed(ArrayList<CreditCard> creditCards) {
        this.creditCards = creditCards;
        updateCreditCardDatabase(creditCards);
        openHomeFragment();
    }

    @Override
    public void onTransferCreated(Transfer transfer) {
        transfers.add(transfer);
        addTransferToDatabase(transfer);
        openTransferFragment();
    }
}