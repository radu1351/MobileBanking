package com.example.aplicatiemobilebanking;

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

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ViewTransactionDialog extends DialogFragment {

    private Transaction transaction;
    private TextView tvMerchant, tvCategory,
            tvAmmount, tvDate, tvCreditCard;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Transaction Details");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_view_transaction, null);
        builder.setView(view);

        transaction = (Transaction) getArguments().getSerializable("TRANSACTION");

        tvMerchant = view.findViewById(R.id.viewTransactionDialog_tvMerchant);
        tvCategory = view.findViewById(R.id.viewTransactionDialog_tvCategory);
        tvAmmount = view.findViewById(R.id.viewTransactionDialog_tvAmount);
        tvDate = view.findViewById(R.id.viewTransactionDialog_tvDate);
        tvCreditCard = view.findViewById(R.id.viewTransactionDialog_tvCreditCard);

        tvMerchant.setText(transaction.getMerchant());
        tvCategory.setText(transaction.getCategory());
        tvAmmount.setText(String.valueOf(transaction.getAmmount()) + " RON");
        tvDate.setText(new SimpleDateFormat("dd MMM yyyy HH:mm").format(transaction.getDate()));
        tvCreditCard.setText(getString(R.string.card_description,
                transaction.getCreditCard().getCardType() == 0 ? "Visa" : "Mastercard",
                transaction.getCreditCard().getCardNumber().substring(12,16)));

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
