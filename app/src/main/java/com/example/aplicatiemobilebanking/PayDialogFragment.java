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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.aplicatiemobilebanking.classes.Transaction;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class PayDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    TextInputEditText tietMerchant, tietAmmount, tietDate;
    Spinner spinCategory;

    private OnTransactionAddedListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Add a new payment");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pay, null);
        builder.setView(view);

        tietMerchant = view.findViewById(R.id.payDiag_tietMerchant);
        spinCategory = view.findViewById(R.id.payDiag_spinCategory);
        tietAmmount = view.findViewById(R.id.payDiag_tietAmmount);
        tietDate = view.findViewById(R.id.payDiag_tietDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String currentDate = now.format(formatter);
        tietDate.setText(currentDate);

        // Set the positive button to save the data
        builder.setPositiveButton("Add payment", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String merchant = tietMerchant.getText().toString();
                    String category = spinCategory.getSelectedItem().toString();
                    Float ammount = Float.parseFloat(tietAmmount.getText().toString());
                    Date date = new SimpleDateFormat("dd MMM yyyy HH:mm").parse(tietDate.getText().toString());
                    Transaction transaction = new Transaction(merchant, category, ammount, date);
                    mListener.onTransactionAdded(transaction);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });

        // Set the negative button to cancel the dialog
        builder.setNegativeButton("Cancel", null);

        Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button saveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                saveButton.setTextColor(Color.BLACK);
                Button cancelButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                cancelButton.setTextColor(Color.BLACK);
            }
        });

        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (OnTransactionAddedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTransactionAddedListener");
        }
    }

    public interface OnTransactionAddedListener {
        void onTransactionAdded(Transaction transaction);
    }
}
