package com.example.aplicatiemobilebanking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Transfer;
import com.google.android.material.textfield.TextInputEditText;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class TransferDialogFragment extends DialogFragment {

    private BankAccount bankAccount;

    private TextInputEditText tietRecName, tietRecIban, tietAmmount, tietDescription;
    private RadioGroup rgTransferType;
    private RadioButton rbNormal, rbInstant;
    private TextView tvTotalTransfered, tvTotalCost;

    private float totalTransfered = 0.0f;
    private float totalCost = 0.0f;
    private float commision = 2.5f;

    private TransferDialogListener mListener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Make a new transfer");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_transfer, null);
        builder.setView(view);

        bankAccount = (BankAccount) getArguments().getSerializable("BANKACCOUNT");

        rgTransferType = view.findViewById(R.id.transferDiag_rgTransferType);

        tietRecName = view.findViewById(R.id.transferDiag_tietRecName);
        tietRecIban = view.findViewById(R.id.transferDiag_tietRecIban);
        tietAmmount = view.findViewById(R.id.transferDiag_tietAmmount);
        tietDescription = view.findViewById(R.id.transferDiag_tietDescription);
        tvTotalTransfered = view.findViewById(R.id.transferDiag_tvTotalTransfered);
        tvTotalCost = view.findViewById(R.id.transferDiag_tvTotalCost);

        tietAmmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                totalTransfered = Float.parseFloat(s.toString());
                tvTotalTransfered.setText(getString(R.string.to_be_transfered,
                        String.valueOf(totalTransfered)));

                totalCost = s.toString().isEmpty() ? 0.0f : Float.parseFloat(s.toString()) + commision;
                tvTotalCost.setText(getString(R.string.total_transfer_cost,
                        String.valueOf(totalCost)));
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        rbNormal = view.findViewById(R.id.transferDiag_rbNormal);
        rbInstant = view.findViewById(R.id.transferDiag_rbInstant);
        rgTransferType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rbNormal.getId()) {
                    commision = 2.5f;

                } else if (checkedId == rbInstant.getId()) {
                    commision = 5.0f;
                }

                float ammount = tietAmmount.getText().toString().isEmpty()
                        ? 0.0f : Float.parseFloat(tietAmmount.getText().toString());

                totalTransfered = ammount;
                tvTotalTransfered.setText(getString(R.string.to_be_transfered,
                        String.valueOf(ammount)));

                if (ammount != 0)
                    totalCost = ammount + commision;
                tvTotalCost.setText(getString(R.string.total_transfer_cost,
                        String.valueOf(totalCost)));
            }
        });

        builder.setPositiveButton("Transfer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String senderIban = bankAccount.getIban();
                String recipientIban = tietRecIban.getText().toString();
                float ammount = totalTransfered;
                String description = tietDescription.getText().toString();

                LocalDateTime localDateTime = LocalDateTime.now(); // Get the current local date and time
                Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
                Date date = Date.from(instant);

                Transfer transfer = new Transfer(senderIban, recipientIban, ammount,
                        commision, description, date);

                if (mListener != null) {
                    mListener.onTransferCreated(transfer);
                }
            }
        });

        builder.setNegativeButton("Close", null);

        Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button closeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                Button transferButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                closeButton.setTextColor(Color.BLACK);
                transferButton.setTextColor(Color.BLACK);
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (TransferDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement TransferDialogListener");
        }
    }

    public interface TransferDialogListener {
        void onTransferCreated(Transfer transfer);
    }

}
