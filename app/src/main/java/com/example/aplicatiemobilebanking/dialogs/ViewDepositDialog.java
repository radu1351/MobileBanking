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
import androidx.fragment.app.DialogFragment;

import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Deposit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ViewDepositDialog extends DialogFragment {

    private BankAccount bankAccount;
    private Deposit deposit;

    private TextView tvBaseAmount, tvInterestRate,
            tvNumberOfMonths, tvMaturityRate, tvMaturityDate;

    private OnTerminateDepositListener terminateDepositListener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Deposit Information");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_view_deposit, null);
        builder.setView(view);

        bankAccount = (BankAccount) getArguments().getSerializable("BANKACCOUNT");
        deposit = (Deposit) getArguments().getSerializable("DEPOSIT");

        tvBaseAmount = view.findViewById(R.id.viewDepositDialog_tvBaseAmount);
        tvInterestRate = view.findViewById(R.id.viewDepositDialog_tvInterestRate);
        tvNumberOfMonths = view.findViewById(R.id.viewDepositDialog_tvNumberOfMonths);
        tvMaturityRate = view.findViewById(R.id.viewDepositDialog_tvMaturityRate);
        tvMaturityDate = view.findViewById(R.id.viewDepositDialog_tvMaturityDate);

        tvBaseAmount.setText(deposit.getBaseAmount() + " RON");
        tvInterestRate.setText(getString(R.string.procent, String.valueOf(deposit.getInterestRate() * 100)));
        tvNumberOfMonths.setText(String.valueOf(deposit.getNumberOfMonths()));
        tvMaturityRate.setText(String.format("%.2f", deposit.getMaturityRate()));

        tvMaturityDate.setText(new SimpleDateFormat("dd MMM yyyy").format(deposit.getMaturityDate()));

        builder.setNegativeButton("Close", null);
        builder.setNeutralButton("Terminate Deposit", null);
        Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button closeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                closeButton.setTextColor(Color.BLACK);

                Button TerminateDepositButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                TerminateDepositButton.setTextColor(Color.RED);
                TerminateDepositButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (terminateDepositListener != null) {
                            terminateDepositListener.onTerminateDeposit(deposit);
                        }
                        dismiss();
                    }
                });

            }
        });
        return dialog;
    }

    public void setTerminateDepositListener(OnTerminateDepositListener listener) {
        terminateDepositListener = listener;
    }

    public interface OnTerminateDepositListener {
        void onTerminateDeposit(Deposit deposit);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            terminateDepositListener = (OnTerminateDepositListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnTerminateDepositListener");
        }
    }
}
