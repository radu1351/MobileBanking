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
import com.example.aplicatiemobilebanking.classes.Transfer;

import java.text.SimpleDateFormat;

public class ViewTransferDialog extends DialogFragment {

    private Transfer transfer;
    private TextView tvSenderIban, tvRecipientIban, tvAmount,
            tvCommission, tvDescription, tvDate;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Transfer Details");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_view_transfer, null);
        builder.setView(view);

        transfer = (Transfer) getArguments().getSerializable("TRANSFER");

        tvSenderIban = view.findViewById(R.id.viewTransactionDialog_tvSenderIban);
        tvRecipientIban = view.findViewById(R.id.viewTransactionDialog_tvRecipientIban);
        tvAmount = view.findViewById(R.id.viewTransactionDialog_tvAmount);
        tvCommission = view.findViewById(R.id.viewTransactionDialog_tvCommission);
        tvDescription = view.findViewById(R.id.viewTransactionDialog_tvDescription);
        tvDate = view.findViewById(R.id.viewTransactionDialog_tvDate);

        tvSenderIban.setText(transfer.getBankAccountIban());
        tvRecipientIban.setText(transfer.getRecipientIban());
        tvAmount.setText(String.valueOf(transfer.getAmount()));
        tvCommission.setText(String.valueOf(transfer.getCommission()));
        tvDescription.setText(String.valueOf(transfer.getDescription()));
        tvDate.setText(new SimpleDateFormat("dd MMM yyyy").format(transfer.getDate()));

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
