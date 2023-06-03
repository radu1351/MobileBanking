package com.example.aplicatiemobilebanking;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Deposit;
import com.example.aplicatiemobilebanking.classes.Request;
import com.example.aplicatiemobilebanking.classes.Transaction;
import com.example.aplicatiemobilebanking.classes.Transfer;
import com.example.aplicatiemobilebanking.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.bouncycastle.cert.ocsp.Req;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity implements PayDialog.OnTransactionAddedListener,
        AddCardDialog.AddCardListener, ViewBankAccountDialog.CreditCardListener,
        TransferDialog.TransferDialogListener, RequestDialog.RequestListener,
        ViewRequestDialog.RequestDialogListener, MobileTransferDialog.MobileTransferDialogListener,
        AddDepositDialog.DepositDialogListener, ViewDepositDialog.OnTerminateDepositListener {

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

            CompletableFuture<List<Request>> requestsFuture = new CompletableFuture<>();
            loadRequestsFromDatabase(new OnSuccessListener<List<Request>>() {
                @Override
                public void onSuccess(List<Request> requestsFromDatabase) {
                    requestsFuture.complete(requestsFromDatabase);
                }
            });

            CompletableFuture<List<Deposit>> depositsFuture = new CompletableFuture<>();
            loadDepositsFromDatabase(new OnSuccessListener<List<Deposit>>() {
                @Override
                public void onSuccess(List<Deposit> depositsFromDatabse) {
                    depositsFuture.complete(depositsFromDatabse);
                }
            });
            // wait for all the futures to complete
            return CompletableFuture.allOf(creditCardFuture, transactionsFuture, transfersFuture, requestsFuture)
                    .thenAccept(v -> {
                        creditCards = new ArrayList<>(creditCardFuture.join());
                        transactions = new ArrayList<>(transactionsFuture.join());
                        transfers = new ArrayList<>(transfersFuture.join());
                        requests = new ArrayList<>(requestsFuture.join());
                        deposits = new ArrayList<>(depositsFuture.join());
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

        Query recipientQuery = transfersRef.whereEqualTo("recipientIban", bankAccount.getIban());

        Query bankAccountQuery = transfersRef.whereEqualTo("bankAccountIban", bankAccount.getIban());

        Tasks.whenAllSuccess(recipientQuery.get(), bankAccountQuery.get())
                .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> results) {
                        List<Transfer> transfers = new ArrayList<>();
                        for (Object result : results) {
                            QuerySnapshot querySnapshot = (QuerySnapshot) result;
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Transfer transfer = document.toObject(Transfer.class);
                                transfers.add(transfer);
                            }
                        }
                        callback.onSuccess(transfers);
                    }
                });
    }

    private void loadRequestsFromDatabase(OnSuccessListener<List<Request>> callback) {
        CollectionReference requestsRef = db.collection("requests");

        // Query for requets where requesterIban = bankAccount.getIban()
        Query requesterQuery = requestsRef.whereEqualTo("requesterIban", bankAccount.getIban());

        // Query for requests where senderIban = bankAccount.getIban()
        Query senderQuery = requestsRef.whereEqualTo("senderIban", bankAccount.getIban());

        // Combine the results of the two queries into a single list
        Tasks.whenAllSuccess(requesterQuery.get(), senderQuery.get())
                .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> results) {
                        List<Request> requests = new ArrayList<>();
                        for (Object result : results) {
                            QuerySnapshot querySnapshot = (QuerySnapshot) result;
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Request request = document.toObject(Request.class);
                                requests.add(request);
                            }
                        }
                        callback.onSuccess(requests);
                    }
                });
    }

    private void loadDepositsFromDatabase(OnSuccessListener<List<Deposit>> callback) {
        CollectionReference depositsRef = db.collection("deposits");

        Query accountQuery = depositsRef.whereEqualTo("bankAccountIban", bankAccount.getIban());

        accountQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<Deposit> deposits = new ArrayList<>();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    Deposit deposit = document.toObject(Deposit.class);
                    deposits.add(deposit);

                    //Check if the deposit has reached its maturity date.
                    // If so, update the balance and close the deposit
                    if (deposit.getMaturityDate().before(new Date())) {
                        closeMaturityDepositFromDatabse(deposit);
                    }
                }
                callback.onSuccess(deposits);
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

        swipeRefreshLayout = findViewById(R.id.mainAct_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadAllDataFromDatabase();
            }
        });

    }

    private void reloadAllDataFromDatabase() {
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

            CompletableFuture<List<Request>> requestsFuture = new CompletableFuture<>();
            loadRequestsFromDatabase(new OnSuccessListener<List<Request>>() {
                @Override
                public void onSuccess(List<Request> requestsFromDatabase) {
                    requestsFuture.complete(requestsFromDatabase);
                }
            });

            CompletableFuture<List<Deposit>> depositsFuture = new CompletableFuture<>();
            loadDepositsFromDatabase(new OnSuccessListener<List<Deposit>>() {
                @Override
                public void onSuccess(List<Deposit> depositsFromDatabse) {
                    depositsFuture.complete(depositsFromDatabse);
                }
            });

            // wait for all the futures to complete
            return CompletableFuture.allOf(creditCardFuture, transactionsFuture, transfersFuture, requestsFuture, depositsFuture)
                    .thenAccept(v -> {
                        creditCards = new ArrayList<>(creditCardFuture.join());
                        transactions = new ArrayList<>(transactionsFuture.join());
                        transfers = new ArrayList<>(transfersFuture.join());
                        requests = new ArrayList<>(requestsFuture.join());
                        deposits = new ArrayList<>(depositsFuture.join());
                    });
        });

        // Reopen the current fragment
        allFutures.thenRun(new Runnable() {
            @Override
            public void run() {
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
                swipeRefreshLayout.setRefreshing(false);
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
        CollectionReference creditCardsCollection = db.collection("creditCards");

        Query query = creditCardsCollection.whereEqualTo("bankAccountIban", bankAccount.getIban());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    batch.delete(document.getReference());
                }
                batch.commit().addOnSuccessListener(aVoid -> {
                    for (CreditCard card : creditCards) {
                        creditCardsCollection.document(card.getCardNumber()).set(card);
                    }
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting old credit cards", e);
                });
            } else {
                Log.e(TAG, "Error querying old credit cards", task.getException());
            }
        });
    }

    public void addTransactionToDatabase(Transaction transaction) {
        //Update the local variables
        transactions.add(transaction);
        bankAccount.reduceBalance(transaction.getAmount());

        CollectionReference transactionsCollection = db.collection("transactions");
        transactionsCollection.document(transaction.getId()).set(transaction)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Transaction added successfully");

                    CollectionReference bankAccountsCollection = db.collection("bankAccounts");

                    bankAccountsCollection.document(bankAccount.getIban())
                            .update("balance", FieldValue.increment(-transaction.getAmount()))
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
        //Update the local variables
        transfers.add(transfer);
        bankAccount.reduceBalance(transfer.getAmount() + transfer.getCommission());

        CollectionReference transactionsCollection = db.collection("transfers");
        transactionsCollection.document(transfer.getId()).set(transfer)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    Log.d(TAG, "Transfer added successfully");
                    CollectionReference bankAccountsCollection = db.collection("bankAccounts");

                    bankAccountsCollection.document(transfer.getBankAccountIban())
                            .update("balance", FieldValue.increment(-(transfer.getAmount() + transfer.getCommission())))
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d(TAG, "Sender balance updated successfully");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error updating sender balance", e);
                            });

                    bankAccountsCollection.document(transfer.getRecipientIban())
                            .update("balance", FieldValue.increment(transfer.getAmount()))
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d(TAG, "Recipient balance updated successfully");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error updating recipient balance", e);
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Log.e(TAG, "Error adding transfer", e);
                });
    }

    private void addRequestToDatabase(Request request) {
        //Update the local variables
        requests.add(request);

        CollectionReference requestsCollection = db.collection("requests");

        requestsCollection.document(request.getId()).set(request)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Request added successfully");

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding Request", e);
                });
    }

    private void addDepositToDatabase(Deposit deposit) {
        deposits.add(deposit);

        CollectionReference depositsCollection = db.collection("deposits");

        depositsCollection.document(deposit.getId()).set(deposit)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Deposit added successfully");

                    // Update bank account balance
                    bankAccount.reduceBalance(deposit.getBaseAmount());

                    CollectionReference bankAccountsCollection = db.collection("bankAccounts");
                    Query query = bankAccountsCollection.whereEqualTo("iban", deposit.getBankAccountIban());

                    query.get().addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            BankAccount bankAccount = document.toObject(BankAccount.class);
                            double currentBalance = bankAccount.getBalance();
                            double newBalance = currentBalance - deposit.getBaseAmount();

                            document.getReference().update("balance", newBalance)
                                    .addOnSuccessListener(aVoid1 -> {
                                        Log.d(TAG, "Bank account balance updated successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating bank account balance", e);
                                    });
                        }
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error searching for bank account", e);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding Deposit", e);
                });
    }

    private void updateRequestInDatabase(Request request) {
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getId().equals(request.getId())) {
                requests.set(i, request);
                break;
            }
        }
        addRequestToDatabase(request);
    }

    private void removeDepositFromDatabase(Deposit deposit) {
        // Delete the local variables
        this.deposits.remove(deposit);
        this.bankAccount.addBalance(deposit.getBaseAmount());

        // Update the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference bankAccountsCollection = db.collection("bankAccounts");
        DocumentReference bankAccountRef = bankAccountsCollection.document(deposit.getBankAccountIban());
        bankAccountRef.update("balance", FieldValue.increment(deposit.getBaseAmount()));

        CollectionReference depositsCollection = db.collection("deposits");
        DocumentReference depositRef = depositsCollection.document(deposit.getId());
        depositRef.delete();
    }

    private void closeMaturityDepositFromDatabse(Deposit deposit) {
        // Delete the local variables
        this.deposits.remove(deposit);
        this.bankAccount.addBalance(deposit.getMaturityRate());

        // Update the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference bankAccountsCollection = db.collection("bankAccounts");
        DocumentReference bankAccountRef = bankAccountsCollection.document(deposit.getBankAccountIban());
        bankAccountRef.update("balance", FieldValue.increment(deposit.getMaturityRate()));

        CollectionReference depositsCollection = db.collection("deposits");
        DocumentReference depositRef = depositsCollection.document(deposit.getId());
        depositRef.delete();
    }

    private void openHomeFragment() {
        Bundle bundle = new Bundle();
        sortTransactionsByDate();
        bundle.putSerializable("CREDITCARDS", creditCards);
        bundle.putSerializable("USER", user);
        bundle.putSerializable("BANKACCOUNT", bankAccount);
        bundle.putSerializable("TRANSACTIONS", transactions);
        bundle.putSerializable("DEPOSITS", deposits);

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

    private void openTransferFragment() {
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
        addTransactionToDatabase(transaction);
    }

    @Override
    public void onCardAdded(CreditCard creditCard) {
        creditCards.add(creditCard);
        addCardToDatabase(creditCard);
        openHomeFragment();
    }


    @Override
    public void onCreditCardsDismissed(ArrayList<CreditCard> creditCards) {
        this.creditCards = creditCards;
        updateCreditCardDatabase(creditCards);
        openHomeFragment();
    }

    @Override
    public void onTransferCreated(Transfer transfer) {
        addTransferToDatabase(transfer);
        openTransferFragment();
    }

    @Override
    public void onMobileTransferCreated(Transfer transfer) {
        addTransferToDatabase(transfer);
        openTransferFragment();
    }

    @Override
    public void onRequestCreated(Request request) {
        addRequestToDatabase(request);
        openRequestMoneyFragment();
    }

    @Override
    public void onDepositCreated(Deposit deposit) {
        addDepositToDatabase(deposit);
        openDepositFragment();
    }

    @Override
    public void onAcceptRequest(Request request) {
        Log.d("ACCEPTED REQ", request.toString());
        if (request.getState() == 1) { // If accepted, the request becomes a transfer
            Transfer transfer = new Transfer(generateId(), request.getRequesterIban(), request.getAmount(),
                    2.5f, request.getDescription(), new Date(), request.getSenderIban());
            addTransferToDatabase(transfer);
            updateRequestInDatabase(request);
        } else if (request.getState() == 2) { // If declined, the request is only updated
            updateRequestInDatabase(request);
        }
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
        removeDepositFromDatabase(deposit);
        openDepositFragment();
    }
}