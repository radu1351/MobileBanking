package com.example.aplicatiemobilebanking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.User;
import com.vinaygaba.creditcardview.CardType;
import com.vinaygaba.creditcardview.CreditCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ViewBankAccountDialog extends DialogFragment {

    private BankAccount bankAccount;
    private CreditCardAdapter creditCardAdapter;
    private ListView lvCreditCards;
    private ArrayList<CreditCard> creditCards;
    private int numberOfCreditCards;
    private CreditCardListener mListener;
    private TextView tvIban, tvSwitft, tvBalance, tvCurrency;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Bank Account Information");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_view_bank_account, null);
        builder.setView(view);

        bankAccount = (BankAccount) getArguments().getSerializable("BANKACCOUNT");
        creditCards = (ArrayList<CreditCard>) getArguments().getSerializable("CREDITCARDS");
        numberOfCreditCards = creditCards.size();
        creditCardAdapter = new CreditCardAdapter(getContext(), R.layout.lv_cards_row_item,
                creditCards, getLayoutInflater(),false);
        lvCreditCards = view.findViewById(R.id.viewAccountDialog_lvCreditCards);
        lvCreditCards.setAdapter(creditCardAdapter);

        tvIban = view.findViewById(R.id.viewAccountDialog_tvIban);
        tvSwitft = view.findViewById(R.id.viewAccountDialog_tvSwift);
        tvBalance = view.findViewById(R.id.viewAccountDialog_tvBalance);
        tvCurrency = view.findViewById(R.id.viewAccountDialog_tvCurrency);

        tvIban.setText(bankAccount.getIban());
        tvSwitft.setText(bankAccount.getSwift());
        tvBalance.setText(String.valueOf(bankAccount.getBalance()) + " " + bankAccount.getCurrency());
        tvCurrency.setText(bankAccount.getCurrency());

        builder.setNegativeButton("Close", null);

        Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button closeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                closeButton.setTextColor(Color.BLACK);
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (CreditCardListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CreditCardListener");
        }
    }

    public interface CreditCardListener {
        void onCreditCardsDismissed(ArrayList<CreditCard> creditCards);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (numberOfCreditCards != creditCards.size()) //If a card was removed from the account
            mListener.onCreditCardsDismissed(creditCards);
    }
}
