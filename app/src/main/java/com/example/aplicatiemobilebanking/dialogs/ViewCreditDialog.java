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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Credit;
import com.example.aplicatiemobilebanking.classes.Deposit;

import java.text.SimpleDateFormat;


public class ViewCreditDialog extends DialogFragment {

    private BankAccount bankAccount;
    private Credit credit;
    private TextView tvLoanedAmount, tvInterestRate, tvTotalCost, tvNumberOfMonths,
            tvMaturityDate, tvLastMonthlyPayment, tvOutstandingBalance;

    private OnPayCreditListener onPayCreditListener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Credit Information");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_view_credit, null);
        builder.setView(view);

        bankAccount = (BankAccount) getArguments().getSerializable("BANKACCOUNT");
        credit = (Credit) getArguments().getSerializable("CREDIT");

        tvLoanedAmount = view.findViewById(R.id.viewCreditDiag_tvLoanedAmount);
        tvInterestRate = view.findViewById(R.id.viewCreditDiag_tvInterestRate);
        tvTotalCost = view.findViewById(R.id.viewCreditDiag_tvTotalCost);
        tvNumberOfMonths = view.findViewById(R.id.viewCreditDiag_tvNumberOfMonths);
        tvMaturityDate = view.findViewById(R.id.viewCreditDiag_tvMaturityDate);
        tvLastMonthlyPayment = view.findViewById(R.id.viewCreditDiag_tvLastMonthlyPayment);
        tvOutstandingBalance = view.findViewById(R.id.viewCreditDiag_tvOutstandingBalance);

        tvLoanedAmount.setText(getString(R.string.RON, String.format("%.2f", credit.getLoanedAmount())));
        tvInterestRate.setText(getString(R.string.procent, String.valueOf(credit.getInterestRate() * 100)));
        tvTotalCost.setText(getString(R.string.RON,String.format("%.2f", credit.getTotalCost())));
        tvNumberOfMonths.setText(String.valueOf(credit.getNumberOfMonths()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        tvMaturityDate.setText(dateFormat.format(credit.getMaturityDate()));
        tvLastMonthlyPayment.setText(dateFormat.format(credit.getLastMonthlyPayment()));
        tvOutstandingBalance.setText(getString(R.string.RON,
                String.format("%.2f", credit.getOutstandingBalance())));

        builder.setNegativeButton("Close", null);
        builder.setNeutralButton("Pay Credit", null);
        Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button closeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                closeButton.setTextColor(Color.BLACK);

                Button TerminateDepositButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                TerminateDepositButton.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_green));
                TerminateDepositButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onPayCreditListener != null) {
                            onPayCreditListener.onPayCredit(credit);
                        }
                        dismiss();
                    }
                });

            }
        });
        return dialog;
    }

    public interface OnPayCreditListener {
        void onPayCredit(Credit credit);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onPayCreditListener = (OnPayCreditListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnPayCreditListener");
        }
    }


}
