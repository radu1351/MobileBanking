package com.example.aplicatiemobilebanking.database;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Credit;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Deposit;
import com.example.aplicatiemobilebanking.classes.Request;
import com.example.aplicatiemobilebanking.classes.Transaction;
import com.example.aplicatiemobilebanking.classes.Transfer;
import com.example.aplicatiemobilebanking.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FirestoreAdminManager {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<User> users = new ArrayList<>(0);
    private ArrayList<BankAccount> bankAccounts = new ArrayList<>(0);
    private ArrayList<Transaction> transactions = new ArrayList<>(0);
    private ArrayList<Transfer> transfers = new ArrayList<Transfer>(0);
    private ArrayList<CreditCard> creditCards = new ArrayList<>(0);
    private ArrayList<Request> requests = new ArrayList<>(0);
    private ArrayList<Deposit> deposits = new ArrayList<>(0);
    private ArrayList<Credit> credits = new ArrayList<Credit>(0);


    public FirestoreAdminManager() {
    }

    public void loadAllAccountsFromDatabase(Runnable thenAction) {
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
                thenAction.run();
            }
        });
    }

    public void loadAllFromDatabaseByAccount(BankAccount bankAccount, Runnable thenAction){
        CompletableFuture<List<CreditCard>> creditCardsFuture = new CompletableFuture<>();
        loadCardsFromDatabase(bankAccount,new OnSuccessListener<List<CreditCard>>() {
            @Override
            public void onSuccess(List<CreditCard> creditCardsFromDatabase) {
                creditCardsFuture.complete(creditCardsFromDatabase);
            }
        });

        CompletableFuture<List<Deposit>> depositsFuture = new CompletableFuture<>();
        loadDepositsFromDatabase(bankAccount,new OnSuccessListener<List<Deposit>>() {
            @Override
            public void onSuccess(List<Deposit> depositsFromDatabse) {
                depositsFuture.complete(depositsFromDatabse);
            }
        });

        CompletableFuture<List<Credit>> creditsFuture = new CompletableFuture<>();
        loadCreditsFromDatabase(bankAccount,new OnSuccessListener<List<Credit>>() {
            @Override
            public void onSuccess(List<Credit> creditsFromDatabase) {
                creditsFuture.complete(creditsFromDatabase);
            }
        });

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(depositsFuture, creditsFuture, creditCardsFuture);
        combinedFuture.thenRun(new Runnable() {
            @Override
            public void run() {
                deposits = (ArrayList<Deposit>) depositsFuture.join();
                credits = (ArrayList<Credit>) creditsFuture.join();
                creditCards = (ArrayList<CreditCard>) creditCardsFuture.join();
                thenAction.run();
            }
        });
    }

    public void loadBankAccountsFromDatabase(OnSuccessListener<List<BankAccount>> callback) {
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

    public void loadUsersfromDatabase(OnSuccessListener<List<User>> callback) {
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

    public void deleteBankAccountFromDatabase(BankAccount bankAccount) {
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

    private void loadDepositsFromDatabase(BankAccount bankAccount, OnSuccessListener<List<Deposit>> callback) {
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
                        closeMaturityDepositFromDatabse(bankAccount, deposit);
                    }
                }
                callback.onSuccess(deposits);
            }
        });
    }

    private void closeMaturityDepositFromDatabse(BankAccount bankAccount, Deposit deposit) {
        // Delete the local variables
        this.deposits.remove(deposit);
        bankAccount.addBalance(deposit.getMaturityRate());

        // Update the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference bankAccountsCollection = db.collection("bankAccounts");
        DocumentReference bankAccountRef = bankAccountsCollection.document(deposit.getBankAccountIban());
        bankAccountRef.update("balance", FieldValue.increment(deposit.getMaturityRate()));

        CollectionReference depositsCollection = db.collection("deposits");
        DocumentReference depositRef = depositsCollection.document(deposit.getId());
        depositRef.delete();
    }

    private void loadCreditsFromDatabase(BankAccount bankAccount, OnSuccessListener<List<Credit>> callback) {
        CollectionReference depositsRef = db.collection("credits");

        Query accountQuery = depositsRef.whereEqualTo("bankAccountIban", bankAccount.getIban());

        accountQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<Credit> credits = new ArrayList<>();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    Credit credit = document.toObject(Credit.class);
                    credits.add(credit);

                    //Check for the credit monthly installment if it is the case
                    updateCreditInDatabse(bankAccount, credit);
                }
                callback.onSuccess(credits);
            }
        });
    }

    private void loadCardsFromDatabase(BankAccount bankAccount, OnSuccessListener<List<CreditCard>> callback) {
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

    private void updateCreditInDatabse(BankAccount bankAccount, Credit credit) {
        Instant currentTime = Instant.now();
        Instant lastPaymentTime = credit.getLastMonthlyPayment().toInstant();
        Duration duration = Duration.between(lastPaymentTime, currentTime);
        long monthsPassed = duration.toDays() / 30;

        if (monthsPassed >= 1) {
            // Update the local last monthly payment date to today
            credit.setLastMonthlyPayment(new Date());
            credit.setOutstandingBalance(credit.getOutstandingBalance() - monthsPassed * credit.getMonthlyInstalment());
            bankAccount.reduceBalance(monthsPassed * credit.getMonthlyInstalment());

            CollectionReference creditsCollection = db.collection("credits");
            creditsCollection.document(credit.getId()).set(credit)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Credit updated successfully");

                        // Reduce the bank account balance by the monthly installment
                        CollectionReference bankAccountsCollection = db.collection("bankAccounts");
                        Query query = bankAccountsCollection.whereEqualTo("iban", credit.getBankAccountIban());

                        query.get().addOnSuccessListener(querySnapshot -> {
                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                BankAccount bankAccountToBeModified = document.toObject(BankAccount.class);
                                double currentBalance = bankAccount.getBalance();
                                double newBalance = currentBalance - monthsPassed * credit.getMonthlyInstalment();

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
                        Log.e(TAG, "Error updating Credit", e);
                    });
        }
    }

    public void terminateDepositInDatabase(Deposit deposit) {
        DocumentReference depositRef = db.collection("deposits").document(deposit.getId());
        depositRef.delete()
                .addOnSuccessListener(aVoid -> {
                    DocumentReference bankAccountRef = db.collection("bankAccounts").
                            document(deposit.getId());
                    bankAccountRef.update("balance", FieldValue.increment(deposit.getBaseAmount()));
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public void payCreditInDatabase(Credit credit) {
        DocumentReference creditRef = db.collection("credits").document(credit.getId());
        creditRef.delete()
                .addOnSuccessListener(aVoid -> {
                    DocumentReference bankAccountRef = db.collection("bankAccounts").
                            document(credit.getBankAccountIban());
                    bankAccountRef.update("balance", FieldValue.increment(-credit.getOutstandingBalance()));
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public void updateCreditCardDatabase(ArrayList<CreditCard> creditCards, BankAccount bankAccount) {
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

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public ArrayList<Transfer> getTransfers() {
        return transfers;
    }

    public ArrayList<CreditCard> getCreditCards() {
        return creditCards;
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public ArrayList<Deposit> getDeposits() {
        return deposits;
    }

    public ArrayList<Credit> getCredits() {
        return credits;
    }
}
