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
import com.example.aplicatiemobilebanking.classes.Deposit;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class AddDepositDialog extends DialogFragment {
    private static final float INTEREST_RATE = 0.06f; // Rata dobanzii

    private TextInputEditText tietBaseAmount, tietNumberOfMonths, tietInterestRate;
    private TextView tvMaturityRate, tvMaturityEndDate;

    private BankAccount bankAccount;
    private DepositDialogListener depositDialogListener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Make a Deposit");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_deposit, null);
        builder.setView(view);

        initialiseTiets(view);

        bankAccount = (BankAccount) getArguments().getSerializable("BANKACCOUNT");

        builder.setPositiveButton("Deposit", null);
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
                        if (tietBaseAmount.getText().toString().isEmpty()) {
                            tietBaseAmount.setError("No amount provided");
                            hasError = true;
                        }
                        if (Float.parseFloat(tietBaseAmount.getText().toString()) > bankAccount.getBalance()) {
                            tietBaseAmount.setError("Balance too low");
                            hasError = true;
                        }
                        if (tietNumberOfMonths.getText().toString().isEmpty()) {
                            tietBaseAmount.setError("No number of months provided");
                            hasError = true;
                        }
                        if (!hasError) {
                            float baseAmount = Float.parseFloat(tietBaseAmount.getText().toString());
                            int numberOfMonths = Integer.parseInt(tietNumberOfMonths.getText().toString());
                            float maturityRate = calculateMaturityRate(baseAmount, numberOfMonths);

                            Deposit deposit = null;
                            try {
                                deposit = new Deposit(generateId(), baseAmount, INTEREST_RATE, numberOfMonths,
                                        new SimpleDateFormat("dd MMM yyyy").parse(calculateMaturityDate(numberOfMonths)),
                                        maturityRate, bankAccount.getIban());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (depositDialogListener != null) {
                                depositDialogListener.onDepositCreated(deposit);
                            }

                            dismiss();
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

        tvMaturityRate = view.findViewById(R.id.addDepositDialog_tvMaturityRate);
        tvMaturityEndDate = view.findViewById(R.id.addDepositDialog_tvMaturityEndDate);
        tietInterestRate = view.findViewById(R.id.addDepositDialog_tietInterestRate);

        tvMaturityRate.setText(getString(R.string.maturity_rate, String.valueOf(0.0f)));
        tvMaturityEndDate.setText(getString(R.string.maturity_date,
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date())));

        tietInterestRate.setText(getString(R.string.procent, String.valueOf(INTEREST_RATE * 100)));
        tietBaseAmount = view.findViewById(R.id.addDepositDialog_tietBaseAmount);
        tietBaseAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String baseAmountString = s.toString();
                String numberOfMonthsString = tietNumberOfMonths.getText().toString();

                if (baseAmountString.isEmpty()) {
                    tvMaturityRate.setText(getString(R.string.maturity_rate, String.valueOf(0.0f)));
                } else {
                    float baseAmount = Float.parseFloat(baseAmountString);
                    int numberOfMonths = numberOfMonthsString.isEmpty() ? 0 : Integer.parseInt(numberOfMonthsString);
                    float maturityRate = calculateMaturityRate(baseAmount, numberOfMonths);
                    tvMaturityRate.setText(getString(R.string.maturity_rate, String.format("%.2f", maturityRate)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tietNumberOfMonths = view.findViewById(R.id.addDepositDialog_tietNumberOfMonths);
        tietNumberOfMonths.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String baseAmountString = tietBaseAmount.getText().toString();
                String numberOfMonthsString = s.toString();

                if (!baseAmountString.isEmpty() && !numberOfMonthsString.isEmpty()) {
                    float baseAmount = Float.parseFloat(baseAmountString);
                    int numberOfMonths = Integer.parseInt(numberOfMonthsString);
                    float maturityRate = calculateMaturityRate(baseAmount, numberOfMonths);
                    tvMaturityRate.setText(getString(R.string.maturity_rate, String.format("%.2f", maturityRate)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tietNumberOfMonths = view.findViewById(R.id.addDepositDialog_tietNumberOfMonths);
        tietNumberOfMonths.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String numberOfMonthsString = s.toString();

                if (!numberOfMonthsString.isEmpty()) {
                    int numberOfMonths = Integer.parseInt(numberOfMonthsString);
                    tvMaturityEndDate.setText(getString(R.string.maturity_date,
                            calculateMaturityDate(numberOfMonths)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private float calculateMaturityRate(float baseAmount, int numberOfMonths) {
        float monthlyInterestRate = INTEREST_RATE / 12;
        float maturityRate = baseAmount;

        for (int i = 0; i < numberOfMonths; i++) {
            maturityRate += maturityRate * monthlyInterestRate;
        }

        return maturityRate;
    }

    public interface DepositDialogListener {
        void onDepositCreated(Deposit deposit);
    }

    public void setDepositDialogListener(DepositDialogListener listener) {
        this.depositDialogListener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            depositDialogListener = (DepositDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DepositDialogListener");
        }
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
