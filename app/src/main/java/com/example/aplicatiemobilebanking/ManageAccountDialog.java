package com.example.aplicatiemobilebanking;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

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

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ManageAccountDialog extends DialogFragment {

    BankAccount bankAccount;
    User user;
    ArrayList<CreditCard> creditCards;
    ArrayList<Credit> credits;
    ArrayList<Deposit> deposits;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView tvFullName, tvPersonalId, tvIban, tvBalance;
    ListView lvCreditCards, lvDeposits, lvCredits;

    private int numberOfCreditCards;
    private CreditCardListener mListener;
    private TerminateAccountListener terminationListener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Account Information");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_manage_account, null);
        builder.setView(view);

        bankAccount = (BankAccount) getArguments().getSerializable("BANKACCOUNT");
        user = (User) getArguments().getSerializable("USER");


        builder.setNegativeButton("Close", null);
        builder.setNeutralButton("Terminate Account", null);
        Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button closeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                closeButton.setTextColor(Color.BLACK);
                closeButton.setTextSize(13f);

                Button terminateAccountButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                terminateAccountButton.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                terminateAccountButton.setTextSize(13f);
                terminateAccountButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (terminationListener != null) {
                            terminationListener.onAccountTerminated(bankAccount);
                        }
                        dismiss();
                    }
                });

            }
        });

        initTiets(view);
        loadAllFromDatabase(view);

        return dialog;
    }

    private void initTiets(View view) {
        tvFullName = view.findViewById(R.id.manageAccountDialog_tvName);
        tvPersonalId = view.findViewById(R.id.manageAccountDialog_tvIdNumber);
        tvIban = view.findViewById(R.id.manageAccountDialog_tvIban);
        tvBalance = view.findViewById(R.id.manageAccountDialog_tvBalance);

        tvFullName.setText(user.getFullName());
        tvPersonalId.setText(user.getIdentificationNumber());
        tvIban.setText(bankAccount.getIban());
        tvBalance.setText(getString(R.string.RON, String.format("%.2f", bankAccount.getBalance())));
    }

    private void loadAllFromDatabase(View view) {

        CompletableFuture<List<CreditCard>> creditCardsFuture = new CompletableFuture<>();
        loadCardsFromDatabase(new OnSuccessListener<List<CreditCard>>() {
            @Override
            public void onSuccess(List<CreditCard> creditCardsFromDatabase) {
                creditCardsFuture.complete(creditCardsFromDatabase);
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

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(depositsFuture, creditsFuture, creditCardsFuture);
        combinedFuture.thenRun(new Runnable() {
            @Override
            public void run() {
                deposits = (ArrayList<Deposit>) depositsFuture.join();
                credits = (ArrayList<Credit>) creditsFuture.join();
                creditCards = (ArrayList<CreditCard>) creditCardsFuture.join();
                numberOfCreditCards = creditCards.size();
                loadListViews(view);
            }
        });
    }

    private void loadListViews(View view) {
        lvCreditCards = view.findViewById(R.id.manageAccountDialog_lvCreditCards);
        CreditCardAdapter creditCardAdapter = new CreditCardAdapter(getContext(), R.layout.lv_cards_row_item,
                creditCards, getLayoutInflater(), true);
        lvCreditCards.setAdapter(creditCardAdapter);

        lvDeposits = view.findViewById(R.id.manageAccountDialog_lvDeposits);
        DepositAdapter depositAdapter = new DepositAdapter(getContext(), R.layout.lv_deposits_short_row_item, deposits, getLayoutInflater());
        lvDeposits.setAdapter(depositAdapter);


        lvCredits = view.findViewById(R.id.manageAccountDialog_lvCredits);
        CreditAdapter creditAdapter = new CreditAdapter(getContext(), R.layout.lv_credit_short_row_item,
                credits, getLayoutInflater());
        lvCredits.setAdapter(creditAdapter);

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

                    //Check for the credit monthly installment if it is the case
                    updateCreditInDatabse(credit);
                }
                callback.onSuccess(credits);
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

    private void updateCreditInDatabse(Credit credit) {
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
                                BankAccount bankAccount = document.toObject(BankAccount.class);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (ManageAccountDialog.CreditCardListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CreditCardListener");
        }
        try {
            terminationListener = (TerminateAccountListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AccountTerminationListener");
        }
    }

    public interface CreditCardListener {
        void onCreditCardsDismissed(ArrayList<CreditCard> creditCards, BankAccount bankAccount);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (numberOfCreditCards != creditCards.size()) //If a card was removed from the account
            mListener.onCreditCardsDismissed(creditCards, bankAccount);
    }


    public interface TerminateAccountListener {
        void onAccountTerminated(BankAccount terminatedAccount);
    }
}
