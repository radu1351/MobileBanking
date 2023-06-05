package com.example.aplicatiemobilebanking;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Credit;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Deposit;
import com.example.aplicatiemobilebanking.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AdminMainActivity extends AppCompatActivity implements ViewCreditDialog.OnPayCreditListener,
        ViewDepositDialog.OnTerminateDepositListener, ManageAccountDialog.CreditCardListener,
        ManageAccountDialog.TerminateAccountListener {

    ArrayList<BankAccount> bankAccounts = new ArrayList<>(0);
    ArrayList<User> users = new ArrayList<User>();

    Button btLogout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListView lvAccounts;

    FirestoreManager firestoreManager;
    private final String SHARED_PREFS_NAME = "com.example.aplicatiemobilebanking.admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        initComponents();

        loadAllFromDatabase();
    }

    private void loadAllFromDatabase() {
        CompletableFuture<List<BankAccount>> bankAccountFuture = new CompletableFuture<>();
        loadBankAccountsFromDatabase(new OnSuccessListener<List<BankAccount>>() {
            @Override
            public void onSuccess(List<BankAccount> bankAccountsFromDatabase) {
                bankAccountFuture.complete(bankAccountsFromDatabase);
            }
        });

        CompletableFuture<List<User>> usersFuture = new CompletableFuture<>();
        loadUsersfromDatabase(new OnSuccessListener<List<User>>() {
            @Override
            public void onSuccess(List<User> usersFromDatabase) {
                usersFuture.complete(usersFromDatabase);
            }
        });

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(bankAccountFuture, usersFuture);
        combinedFuture.thenRun(new Runnable() {
            @Override
            public void run() {
                bankAccounts = (ArrayList<BankAccount>) bankAccountFuture.join();
                users = (ArrayList<User>) usersFuture.join();
                initComponents();
            }
        });
    }

    private void initComponents() {
        firestoreManager = new FirestoreManager();

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

        loadAccountsListView(this, bankAccounts);
    }

    private void loadAccountsListView(AppCompatActivity activity, ArrayList<BankAccount> bankAccounts) {
        lvAccounts = findViewById(R.id.adminMainActivity_lvAccounts);
        AccountsAdapter accountsAdapter = new AccountsAdapter(activity, R.layout.lv_accounts_row_item,
                bankAccounts, users, getLayoutInflater());
        lvAccounts.setAdapter(accountsAdapter);
    }

    private void loadBankAccountsFromDatabase(OnSuccessListener<List<BankAccount>> callback) {
        db.collection("bankAccounts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<BankAccount> bankAccounts = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BankAccount bankAccount = document.toObject(BankAccount.class);
                                bankAccounts.add(bankAccount);
                            }
                            callback.onSuccess(bankAccounts);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void loadUsersfromDatabase(OnSuccessListener<List<User>> callback) {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> users = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                users.add(user);
                            }
                            callback.onSuccess(users);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void deleteBankAccountFromDatabase(BankAccount bankAccount) {
        this.bankAccounts.remove(bankAccount);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference bankAccountRef = db.collection("bankAccounts").document(bankAccount.getIban());
        bankAccountRef.delete();

        db.collection("creditCards")
                .whereEqualTo("bankAccountIban", bankAccount.getIban())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        db.collection("creditCards").document(document.getId()).delete();
                    }
                })
                .addOnFailureListener(e -> {
                });

        db.collection("deposits")
                .whereEqualTo("bankAccountIban", bankAccount.getIban())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        db.collection("deposits").document(document.getId()).delete();
                    }
                })
                .addOnFailureListener(e -> {
                });

        db.collection("credits")
                .whereEqualTo("bankAccountIban", bankAccount.getIban())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        db.collection("credits").document(document.getId()).delete();
                    }
                })
                .addOnFailureListener(e -> {
                });
    }

    private void terminateDepositInDatabase(Deposit deposit) {
        DocumentReference depositRef = db.collection("deposits").document(deposit.getId());
        depositRef.delete()
                .addOnSuccessListener(aVoid -> {
                    DocumentReference bankAccountRef = db.collection("bankAccounts").
                            document(deposit.getId());
                    bankAccountRef.update("balance", FieldValue.increment(deposit.getBaseAmount()));
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private void payCreditInDatabase(Credit credit) {
        DocumentReference creditRef = db.collection("credits").document(credit.getId());
        creditRef.delete()
                .addOnSuccessListener(aVoid -> {
                    DocumentReference bankAccountRef = db.collection("bankAccounts").
                            document(credit.getBankAccountIban());
                    bankAccountRef.update("balance", FieldValue.increment(-credit.getOutstandingBalance()));
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private void updateCreditCardDatabase(ArrayList<CreditCard> creditCards, BankAccount bankAccount) {
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

    @Override
    public void onPayCredit(Credit credit) {
        payCreditInDatabase(credit);
        ((AccountsAdapter) lvAccounts.getAdapter()).openLastAccountClicked();
    }

    @Override
    public void onTerminateDeposit(Deposit deposit) {
        terminateDepositInDatabase(deposit);
        ((AccountsAdapter) lvAccounts.getAdapter()).openLastAccountClicked();
    }

    @Override
    public void onCreditCardsDismissed(ArrayList<CreditCard> creditCards, BankAccount bankAccount) {
        updateCreditCardDatabase(creditCards, bankAccount);
    }

    @Override
    public void onAccountTerminated(BankAccount terminatedAccount) {
        Log.d("TERMINATED ACCOUNT", terminatedAccount.toString());
        deleteBankAccountFromDatabase(terminatedAccount);
        ((AccountsAdapter) lvAccounts.getAdapter()).notifyDataSetChanged();
    }
}