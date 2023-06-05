package com.example.aplicatiemobilebanking.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Request;
import com.example.aplicatiemobilebanking.classes.Transfer;

import java.text.SimpleDateFormat;

public class ViewRequestDialog extends DialogFragment {

    private Request request;
    private BankAccount bankAccount;

    private RequestDialogListener requestDialogListener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Review Request");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_view_request, null);
        builder.setView(view);

        request = (Request) getArguments().getSerializable("REQUEST");
        bankAccount = (BankAccount) getArguments().getSerializable("BANKACCOUNT");

        TextView tvRequesterName = view.findViewById(R.id.viewRequestDialog_tvRequesterName);
        TextView tvRequesterIban = view.findViewById(R.id.viewRequestDialog_tvRequesterIban);
        TextView tvAmount = view.findViewById(R.id.viewRequestDialog_tvAmount);
        TextView tvDescription = view.findViewById(R.id.viewRequestDialog_tvDescription);

        tvRequesterName.setText(request.getRequesterFullName());
        tvRequesterIban.setText(request.getRequesterIban());
        tvAmount.setText(String.valueOf(request.getAmount()));
        tvDescription.setText(request.getDescription());

        builder.setPositiveButton("Accept", null);
        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.setState(2);
                sendRequestResult();
            }
        });
        builder.setNeutralButton("Cancel", null);
        Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button acceptButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                acceptButton.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_green));
                acceptButton.setTextSize(14f);
                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bankAccount.getBalance() < request.getAmount()) {
                            Toast.makeText(getContext(), "Balance too low", Toast.LENGTH_SHORT).show();
                            tvAmount.setError("Balance too low");
                        } else {
                            request.setState(1);
                            sendRequestResult();
                            dialog.dismiss(); // Manually dismiss the dialog when the conditions are met
                        }
                    }
                });

                Button cancelButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                cancelButton.setTextColor(Color.DKGRAY);
                cancelButton.setTextSize(14f);

                Button declineButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                declineButton.setTextColor(Color.RED);
                declineButton.setTextSize(14f);
            }
        });

        return dialog;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof RequestDialogListener) {
            requestDialogListener = (RequestDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RequestDialogListener");
        }
    }

    public interface RequestDialogListener {
        void onAcceptRequest(Request request);
    }

    private void sendRequestResult() {
        if (requestDialogListener != null) {
            requestDialogListener.onAcceptRequest(request);
        }
    }
}