package com.example.aplicatiemobilebanking.dialogs;

import static android.content.ContentValues.TAG;

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

import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Transfer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class TransferDialog extends DialogFragment {

    private BankAccount bankAccount;

    private TextInputEditText tietRecName, tietRecIban, tietAmount, tietDescription;
    private RadioGroup rgTransferType;
    private RadioButton rbNormal, rbInstant;
    private TextView tvTotalTransfered, tvTotalCost;
    private Dialog dialog;

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
        tietAmount = view.findViewById(R.id.transferDiag_tietAmount);
        tietDescription = view.findViewById(R.id.transferDiag_tietDescription);
        tvTotalTransfered = view.findViewById(R.id.transferDiag_tvTotalTransfered);
        tvTotalCost = view.findViewById(R.id.transferDiag_tvTotalCost);

        tietAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    totalTransfered = Float.parseFloat(s.toString());
                    tvTotalTransfered.setText(getString(R.string.to_be_transfered,
                            String.valueOf(totalTransfered)));

                    totalCost = s.toString().isEmpty() ? 0.0f : Float.parseFloat(s.toString()) + commision;
                    tvTotalCost.setText(getString(R.string.total_transfer_cost,
                            String.valueOf(totalCost)));
                } else {
                    totalTransfered = 0.0f;
                    tvTotalTransfered.setText(getString(R.string.to_be_transfered,
                            String.valueOf(totalTransfered)));

                    totalCost = 0.0f;
                    tvTotalCost.setText(getString(R.string.total_transfer_cost,
                            String.valueOf(totalCost)));
                }
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

                float Amount = tietAmount.getText().toString().isEmpty()
                        ? 0.0f : Float.parseFloat(tietAmount.getText().toString());

                totalTransfered = Amount;
                tvTotalTransfered.setText(getString(R.string.to_be_transfered,
                        String.valueOf(Amount)));

                if (Amount != 0)
                    totalCost = Amount + commision;
                tvTotalCost.setText(getString(R.string.total_transfer_cost,
                        String.valueOf(totalCost)));
            }
        });

        builder.setPositiveButton("Transfer", null);
        builder.setNegativeButton("Close", null);

        dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button closeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                Button transferButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                transferButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean hasError = false;
                        if (tietRecIban.getText().toString().isEmpty() || tietRecIban.getText().toString().length() != 24) {
                            tietRecIban.setError("Wrong IBAN provided");
                            hasError = true;
                        }
                        if (tietAmount.getText().toString().isEmpty()) {
                            tietAmount.setError("No amount provided");
                            hasError = true;
                        }
                        if (Float.parseFloat(tietAmount.getText().toString()) > bankAccount.getBalance()) {
                            tietAmount.setError("Balance too low");
                            hasError = true;
                        }
                        if (tietRecName.getText().toString().isEmpty()) {
                            tietRecName.setError("No recipient name provided");
                            hasError = true;
                        }
                        if (tietDescription.getText().toString().isEmpty()) {
                            tietDescription.setError("No description provided");
                            hasError = true;
                        }
                        if (!hasError) {

                            String senderIban = bankAccount.getIban();
                            String recipientIban = tietRecIban.getText().toString();
                            float amount = totalTransfered;
                            String description = tietDescription.getText().toString();

                            LocalDateTime localDateTime = LocalDateTime.now(); // Get the current local date and time
                            Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
                            Date date = Date.from(instant);

                            Transfer transfer = new Transfer(generateId(),
                                    recipientIban, amount,
                                    commision, description, date, bankAccount.getIban());

                            processTransfer(transfer);
                        }
                    }

                });
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


    public String generateId() {
        Random rand = new Random();
        long min = 100000000000L;
        long max = 999999999999L;
        long randomNum = min + ((long) (rand.nextDouble() * (max - min)));
        return Long.toString(randomNum);
    }

    public void processTransfer(Transfer transfer) {
        CompletableFuture<Boolean> checkRecipientFuture = new CompletableFuture<>();
        checkAccountInDatabase(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean recipientExists) {
                checkRecipientFuture.complete(recipientExists);
            }
        });

        checkRecipientFuture.thenRun(() -> {
            Boolean recipientExists = checkRecipientFuture.join();

            if (recipientExists) {
                if (!transfer.getRecipientIban().equals(bankAccount.getIban())) {
                    if (mListener != null) {
                        mListener.onTransferCreated(transfer);
                    }
                    dialog.dismiss();
                } else {
                    tietRecIban.setError("Recipient IBAN is the same as yours");
                }
            } else {
                tietRecIban.setError("IBAN not in database");
            }
        });
    }

    public void checkAccountInDatabase(OnSuccessListener<Boolean> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference bankAccountsRef = db.collection("bankAccounts");
        Query query = bankAccountsRef.whereEqualTo("iban", tietRecIban.getText().toString());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (!snapshot.isEmpty()) {
                    callback.onSuccess(true); // RecipientIban exists in the database
                } else {
                    callback.onSuccess(false); // RecipientIban doesn't exists in the database
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

    }

}

