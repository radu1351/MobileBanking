package com.example.aplicatiemobilebanking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Request;
import com.example.aplicatiemobilebanking.classes.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

public class RequestDialog extends DialogFragment {

    private BankAccount senderBankAccount;
    private User senderUser;
    private Request request;
    private TextInputEditText tietAmount, tietDescription;

    private RequestListener requestListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Request Money");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_request, null);
        builder.setView(view);

        tietAmount = view.findViewById(R.id.requestMoneyFrag_tietAmount);

        senderBankAccount = (BankAccount) getArguments().getSerializable("BANKACCOUNT");
        senderUser = (User) getArguments().getSerializable("USER");
        request = (Request) getArguments().getSerializable("REQUEST");

        builder.setPositiveButton("Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean hasError = false;

                if (tietDescription.getText().toString().isEmpty()) {
                    tietDescription.setError("No description provided for the sender");
                    hasError = true;
                }

                if (tietAmount.getText().toString().isEmpty()) {
                    tietAmount.setError("No amount provided");
                    hasError = true;
                }

                if (!hasError) {
                    request.setAmount(Float.parseFloat(tietAmount.getText().toString()));
                    request.setDate(new Date());
                    request.setDescription(tietDescription.getText().toString());
                    request.setState(0); // In progress (not accepted/declined)

                    if (requestListener != null) {
                        requestListener.onRequestCreated(request);
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button requestButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                requestButton.setTextColor(Color.BLACK);
                Button cancelButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                cancelButton.setTextColor(Color.BLACK);
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            requestListener = (RequestDialog.RequestListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RequestListener");
        }
    }

    public interface RequestListener {
        void onRequestCreated(Request request);
    }
}
