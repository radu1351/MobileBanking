package com.example.aplicatiemobilebanking.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ViewTransactionDialog extends DialogFragment {

    private Transaction transaction;
    private TextView tvMerchant, tvCategory,
            tvAmmount, tvDate, tvCreditCard;

    ArrayList<CreditCard> creditCards = new ArrayList<>(0);

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Transaction Details");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_view_transaction, null);
        builder.setView(view);

        transaction = (Transaction) getArguments().getSerializable("TRANSACTION");
        creditCards = (ArrayList<CreditCard>) getArguments().getSerializable("CREDITCARDS");

        tvMerchant = view.findViewById(R.id.viewTransactionDialog_tvMerchant);
        tvCategory = view.findViewById(R.id.viewTransactionDialog_tvCategory);
        tvAmmount = view.findViewById(R.id.viewTransactionDialog_tvAmount);
        tvDate = view.findViewById(R.id.viewTransactionDialog_tvDate);
        tvCreditCard = view.findViewById(R.id.viewTransactionDialog_tvCreditCard);

        tvMerchant.setText(transaction.getMerchant());
        tvCategory.setText(transaction.getCategory());
        tvAmmount.setText(String.valueOf(transaction.getAmount()) + " RON");
        tvDate.setText(new SimpleDateFormat("dd MMM yyyy HH:mm").format(transaction.getDate()));

        CreditCard creditCard = creditCards.stream().filter(c -> c.getCardNumber().equals(transaction.getCreditCardNumber()))
                .findAny().orElse(null);

        if (creditCard != null) {
            tvCreditCard.setText(getString(R.string.card_description,
                    creditCard.getCardType() == 0 ? "Visa" : "Mastercard",
                    creditCard.getCardNumber().substring(12, 16)));
        } else {
            //The card was removed from the account
            tvCreditCard.setText(getString(R.string.card_description,
                    "Removed card", transaction.getCreditCardNumber().substring(12, 16)));
        }

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
}
