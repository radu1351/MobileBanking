package com.example.aplicatiemobilebanking.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.aplicatiemobilebanking.list_view_adapters.CreditAdapter;
import com.example.aplicatiemobilebanking.list_view_adapters.CreditCardAdapter;
import com.example.aplicatiemobilebanking.list_view_adapters.DepositAdapter;
import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Credit;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Deposit;
import com.example.aplicatiemobilebanking.database.FirestoreAdminManager;
import com.example.aplicatiemobilebanking.classes.User;

import java.util.ArrayList;

public class ManageAccountDialog extends DialogFragment {

    BankAccount bankAccount;
    User user;
    ArrayList<CreditCard> creditCards = new ArrayList<>(0);
    ArrayList<Credit> credits = new ArrayList<>(0);
    ArrayList<Deposit> deposits = new ArrayList<>(0);

    TextView tvFullName, tvPersonalId, tvIban, tvBalance;
    ListView lvCreditCards, lvDeposits, lvCredits;

    FirestoreAdminManager firestoreAdminManager = new FirestoreAdminManager();
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
        firestoreAdminManager.loadAllFromDatabaseByAccount(bankAccount, new Runnable() {
            @Override
            public void run() {
                loadDataFromFirestoreAdminManager();
                numberOfCreditCards = creditCards.size();
                initTiets(view);
                loadListViews(view);
            }
        });
    }

    private void loadDataFromFirestoreAdminManager(){
        this.creditCards= firestoreAdminManager.getCreditCards();
        this.credits = firestoreAdminManager.getCredits();
        this.deposits = firestoreAdminManager.getDeposits();
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
