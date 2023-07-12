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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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

public class FirestoreManager {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User user;
    BankAccount bankAccount;

    private ArrayList<Transaction> transactions = new ArrayList<>(0);
    private ArrayList<Transfer> transfers = new ArrayList<Transfer>(0);
    private ArrayList<CreditCard> creditCards = new ArrayList<>(0);
    private ArrayList<Request> requests = new ArrayList<>(0);
    private ArrayList<Deposit> deposits = new ArrayList<>(0);
    private ArrayList<Credit> credits = new ArrayList<Credit>(0);

    public FirestoreManager() {
    }

    public FirestoreManager(User user) {
        this.user = user;
    }

    public void loadAllFromDatabase(Runnable thenAction) {
        CompletableFuture<BankAccount> bankAccountFuture = new CompletableFuture<>();
        loadBankAccountFromDatabase(new OnSuccessListener<BankAccount>() {
            @Override
            public void onSuccess(BankAccount bankAccountFromDatabase) {
                bankAccountFuture.complete(bankAccountFromDatabase);
            }
        });

        CompletableFuture<Void> allFutures = bankAccountFuture.thenCompose(bankAccount -> {
            this.bankAccount = bankAccount;

            CompletableFuture<List<CreditCard>> creditCardFuture = new CompletableFuture<>();
            this.loadCardsFromDatabase(new OnSuccessListener<List<CreditCard>>() {
                @Override
                public void onSuccess(List<CreditCard> creditCardsFromDatabase) {
                    creditCardFuture.complete(creditCardsFromDatabase);
                }
            });

            CompletableFuture<List<Transaction>> transactionsFuture = new CompletableFuture<>();
            this.loadTransactionsFromDatabase(new OnSuccessListener<List<Transaction>>() {
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

            CompletableFuture<List<Credit>> creditsFuture = new CompletableFuture<>();
            loadCreditsFromDatabase(new OnSuccessListener<List<Credit>>() {
                @Override
                public void onSuccess(List<Credit> creditsFromDatabase) {
                    creditsFuture.complete(creditsFromDatabase);
                }
            });

            // wait for all the futures to complete
            return CompletableFuture.allOf(creditCardFuture, transactionsFuture, transfersFuture, requestsFuture, creditsFuture)
                    .thenAccept(v -> {
                        creditCards = new ArrayList<>(creditCardFuture.join());
                        transactions = new ArrayList<>(transactionsFuture.join());
                        transfers = new ArrayList<>(transfersFuture.join());
                        requests = new ArrayList<>(requestsFuture.join());
                        deposits = new ArrayList<>(depositsFuture.join());
                        credits = new ArrayList<>(creditsFuture.join());
                    });
        });

        allFutures.thenRun(thenAction);
    }

    public void loadBankAccountFromDatabase(OnSuccessListener<BankAccount> callback) {
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

    public void loadCardsFromDatabase(OnSuccessListener<List<CreditCard>> callback) {
        CollectionReference creditCardsCollection = FirebaseFirestore.getInstance().collection("creditCards");
        creditCardsCollection.whereEqualTo("bankAccountIban", bankAccount.getIban()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<CreditCard> creditCards = new ArrayList<>(2);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CreditCard creditCard = document.toObject(CreditCard.class);
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

    public void loadTransactionsFromDatabase(OnSuccessListener<List<Transaction>> callback) {
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

    public void loadTransfersFromDatabase(OnSuccessListener<List<Transfer>> callback) {
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

        Query requesterQuery = requestsRef.whereEqualTo("requesterIban", bankAccount.getIban());
        Query senderQuery = requestsRef.whereEqualTo("senderIban", bankAccount.getIban());
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
                        closeMaturityDepositFromDatabse(bankAccount, deposit);
                    }
                }
                callback.onSuccess(deposits);
            }
        });
    }

    private void loadCreditsFromDatabase(OnSuccessListener<List<Credit>> callback) {
        CollectionReference depositsRef = db.collection("credits");

        Query accountQuery = depositsRef.whereEqualTo("bankAccountIban", bankAccount.getIban());

        accountQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<Credit> credits = new ArrayList<>();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    Credit credit = document.toObject(Credit.class);
                    credits.add(credit);

                    updateCreditInDatabse(bankAccount, credit);
                }
                callback.onSuccess(credits);
            }
        });
    }

    public void closeMaturityDepositFromDatabse(BankAccount bankAccount, Deposit deposit) {
        bankAccount.addBalance(deposit.getMaturityRate());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference bankAccountsCollection = db.collection("bankAccounts");
        DocumentReference bankAccountRef = bankAccountsCollection.document(deposit.getBankAccountIban());
        bankAccountRef.update("balance", FieldValue.increment(deposit.getMaturityRate()));

        CollectionReference depositsCollection = db.collection("deposits");
        DocumentReference depositRef = depositsCollection.document(deposit.getId());
        depositRef.delete();
    }

    public void updateCreditInDatabse(BankAccount bankAccount, Credit credit) {
        Instant currentTime = Instant.now();
        Instant lastPaymentTime = credit.getLastMonthlyPayment().toInstant();
        Duration duration = Duration.between(lastPaymentTime, currentTime);
        long monthsPassed = duration.toDays() / 30;

        if (monthsPassed >= 1) {
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
                                double currentBalance = bankAccountToBeModified.getBalance();
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

    public void reloadAllDataFromDatabase(Runnable thenAction) {

        CompletableFuture<BankAccount> bankAccountFuture = new CompletableFuture<>();
        loadBankAccountFromDatabase(new OnSuccessListener<BankAccount>() {
            @Override
            public void onSuccess(BankAccount bankAccountFromDatabase) {
                bankAccountFuture.complete(bankAccountFromDatabase);
            }
        });

        CompletableFuture<Void> allFutures = bankAccountFuture.thenCompose(bankAccount -> {
            this.bankAccount = bankAccount;

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
            CompletableFuture<List<Credit>> creditsFuture = new CompletableFuture<>();
            loadCreditsFromDatabase(new OnSuccessListener<List<Credit>>() {
                @Override
                public void onSuccess(List<Credit> creditsFromDatabase) {
                    creditsFuture.complete(creditsFromDatabase);
                }
            });

            return CompletableFuture.allOf(creditCardFuture, transactionsFuture, transfersFuture,
                            requestsFuture, depositsFuture, creditsFuture)
                    .thenAccept(v -> {
                        creditCards = new ArrayList<>(creditCardFuture.join());
                        transactions = new ArrayList<>(transactionsFuture.join());
                        transfers = new ArrayList<>(transfersFuture.join());
                        requests = new ArrayList<>(requestsFuture.join());
                        deposits = new ArrayList<>(depositsFuture.join());
                        credits = new ArrayList<>(creditsFuture.join());
                    });
        });

        allFutures.thenRun(thenAction);
    }

    public void addCardToDatabase(CreditCard creditCard) {
        creditCards.add(creditCard);
        db.collection("creditCards").document(creditCard.getCardNumber())
                .set(creditCard)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public void updateCreditCardDatabase(ArrayList<CreditCard> creditCards) {
        this.creditCards = creditCards;
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
                    Log.e(TAG, "Error adding transfer", e);
                });
    }

    public void addRequestToDatabase(Request request) {
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

    public void addDepositToDatabase(Deposit deposit) {
        deposits.add(deposit);

        CollectionReference depositsCollection = db.collection("deposits");

        depositsCollection.document(deposit.getId()).set(deposit)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Deposit added successfully");

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

    public void updateRequestInDatabase(Request request) {
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getId().equals(request.getId())) {
                requests.set(i, request);
                break;
            }
        }
        addRequestToDatabase(request);
    }

    public void removeDepositFromDatabase(Deposit deposit) {
        this.deposits.remove(deposit);
        this.bankAccount.addBalance(deposit.getBaseAmount());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference bankAccountsCollection = db.collection("bankAccounts");
        DocumentReference bankAccountRef = bankAccountsCollection.document(deposit.getBankAccountIban());
        bankAccountRef.update("balance", FieldValue.increment(deposit.getBaseAmount()));

        CollectionReference depositsCollection = db.collection("deposits");
        DocumentReference depositRef = depositsCollection.document(deposit.getId());
        depositRef.delete();
    }


    public void addCreditToDatabase(Credit credit) {
        this.bankAccount.addBalance(credit.getLoanedAmount());
        this.credits.add(credit);

        CollectionReference creditsCollection = db.collection("credits");
        creditsCollection.document(credit.getId()).set(credit)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Credit added successfully");

                    CollectionReference bankAccountsCollection = db.collection("bankAccounts");
                    Query query = bankAccountsCollection.whereEqualTo("iban", credit.getBankAccountIban());

                    query.get().addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            BankAccount bankAccount = document.toObject(BankAccount.class);
                            double currentBalance = bankAccount.getBalance();
                            double newBalance = currentBalance + credit.getLoanedAmount();

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
                    Log.e(TAG, "Error adding Credit", e);
                });
    }

    public void payCreditInDatabase(Credit credit) {
        this.credits.remove(credit);
        this.bankAccount.reduceBalance(credit.getOutstandingBalance());

        DocumentReference creditRef = db.collection("credits").document(credit.getId());
        creditRef.delete()
                .addOnSuccessListener(aVoid -> {
                    DocumentReference bankAccountRef = db.collection("bankAccounts").
                            document(this.bankAccount.getIban());
                    bankAccountRef.set(this.bankAccount)
                            .addOnFailureListener(Throwable::printStackTrace);
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public User getUser() {
        return user;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
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
