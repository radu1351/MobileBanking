package com.example.aplicatiemobilebanking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Credit;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class AddCreditDialog extends DialogFragment {
    private static final float INTEREST_RATE = 0.06f; // Rata dobanzii

    private TextInputEditText tietLoanedAmount, tietNumberOfMonths;
    private TextView tvTotalCost, tvMonthlyInstalment, tvMaturityDate;

    private BankAccount bankAccount;

    private AddCreditDialogListener listener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Open a Credit");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_credit, null);
        builder.setView(view);

        initialiseTiets(view);

        bankAccount = (BankAccount) getArguments().getSerializable("BANKACCOUNT");

        builder.setPositiveButton("Open Credit", null);
        builder.setNegativeButton("Close", null);

        Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setTextColor(Color.BLACK);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean hasError = false;
                        if (tietLoanedAmount.getText().toString().isEmpty()) {
                            tietLoanedAmount.setError("No Loaned Amount Provided");
                            hasError = true;
                        }
                        if (tietNumberOfMonths.getText().toString().isEmpty()) {
                            tietLoanedAmount.setError("No Number Of Months Provided");
                            hasError = true;
                        }
                        if (!hasError) {
                            try {
                                String id = generateId();
                                float loanedAmount = Float.parseFloat(tietLoanedAmount.getText().toString());
                                int numberOfMonths = Integer.parseInt(tietNumberOfMonths.getText().toString());
                                float totalCost = calculateTotalCreditCost(loanedAmount, numberOfMonths);
                                Date maturityDate = new SimpleDateFormat("dd MMM yyyy").parse(calculateMaturityDate(numberOfMonths));
                                Date lastMonthlyPayment = new Date();
                                float outstandingBalance = totalCost;
                                String bankAccountIban = bankAccount.getIban();

                                Credit credit = new Credit(id, loanedAmount, INTEREST_RATE, totalCost, numberOfMonths,
                                        maturityDate, lastMonthlyPayment, outstandingBalance, bankAccountIban);
                                listener.onCreditAdded(credit);
                                dismiss();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setTextColor(Color.BLACK);
            }
        });

        return dialog;
    }

    private void initialiseTiets(View view) {
        tvTotalCost = view.findViewById(R.id.addCreditDialog_tvTotalCost);
        tvMonthlyInstalment = view.findViewById(R.id.addCreditDialog_tvMonthlyInstalment);
        tvMaturityDate = view.findViewById(R.id.addCreditDialog_tvMaturityDate);

        tietLoanedAmount = view.findViewById(R.id.addCreditDialog_tietLoanedAmount);
        tietNumberOfMonths = view.findViewById(R.id.addCreditDialog_tietNumberOfMonths);

        tvTotalCost.setText(getString(R.string.total_cost,
                String.format("%.0f", 0.0f)));
        tvMonthlyInstalment.setText(getString(R.string.monthly_instalment,
                String.format("%.0f", 0.0f)));
        tvMaturityDate.setText(getString(R.string.maturity_date,
                new SimpleDateFormat("dd MMM yyyy").format(new Date())));

        tietLoanedAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCreditTextViews();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tietNumberOfMonths.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCreditTextViews();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateCreditTextViews() {
        if (tietLoanedAmount.getText().toString().isEmpty() || tietNumberOfMonths.getText().toString().isEmpty()) {
            tvTotalCost.setText(getString(R.string.total_cost,
                    String.format("%.0f", 0.0f)));
            tvMonthlyInstalment.setText(getString(R.string.monthly_instalment,
                    String.format("%.0f", 0.0f)));
            tvMaturityDate.setText(getString(R.string.maturity_date,
                    new SimpleDateFormat("dd MMM yyyy").format(new Date())));
        } else {
            String loanedAmountStr = tietLoanedAmount.getText().toString().trim();
            String numberOfMonthsStr = tietNumberOfMonths.getText().toString().trim();

            float loanedAmount = Float.parseFloat(loanedAmountStr);
            int numberOfMonths = Integer.parseInt(numberOfMonthsStr);

            float totalCost = calculateTotalCreditCost(loanedAmount, numberOfMonths);
            float monthlyInstalment = calculateMonthlyInstalment(totalCost, numberOfMonths);
            String maturityDate = calculateMaturityDate(numberOfMonths);

            tvTotalCost.setText(getString(R.string.total_cost,
                    String.format("%.2f", totalCost)));
            tvMonthlyInstalment.setText(getString(R.string.monthly_instalment,
                    String.format("%.2f", monthlyInstalment)));
            tvMaturityDate.setText(getString(R.string.maturity_date, maturityDate));
        }
    }

    private float calculateTotalCreditCost(float loanedAmount, int numberOfMonths) {
        float monthlyInterestRate = INTEREST_RATE / 12;
        float totalCost = loanedAmount;

        for (int i = 0; i < numberOfMonths; i++) {
            totalCost += totalCost * monthlyInterestRate;
        }
        return totalCost;
    }

    public interface AddCreditDialogListener {
        void onCreditAdded(Credit credit);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddCreditDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddCreditDialogListener");
        }
    }

    private float calculateMonthlyInstalment(float loanedAmount, int numberOfMonths) {
        return calculateTotalCreditCost(loanedAmount, numberOfMonths) / numberOfMonths;
    }

    private String generateId() {
        Random rand = new Random();
        long min = 100000000000L;
        long max = 999999999999L;
        long randomNum = min + ((long) (rand.nextDouble() * (max - min)));
        return Long.toString(randomNum);
    }

    private String calculateMaturityDate(int numberOfMonths) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, numberOfMonths);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String endDate = dateFormat.format(calendar.getTime());
        return endDate;
    }

}
