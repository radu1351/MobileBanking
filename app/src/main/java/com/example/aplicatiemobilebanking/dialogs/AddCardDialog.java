package com.example.aplicatiemobilebanking.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.User;
import com.vinaygaba.creditcardview.CardType;
import com.vinaygaba.creditcardview.CreditCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class AddCardDialog extends DialogFragment {

    private User user;
    private BankAccount bankAccount;
    private Spinner spinCardType;
    private TextView tvDelivery;

    private AddCardListener mListener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundDialogSyle);
        builder.setTitle("Add a new credit card");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_card, null);
        builder.setView(view);

        user = (User) getArguments().getSerializable("USER");
        bankAccount = (BankAccount) getArguments().getSerializable("BANKACCOUNT");
        tvDelivery = view.findViewById(R.id.addCardDIag_tvDelivery);
        tvDelivery.setText(getString(R.string.credit_card_delivery_message, user.getAddress()));

        spinCardType = view.findViewById(R.id.addCardDiag_spinCardType);

        CreditCardView creditCardView = view.findViewById(R.id.addCardDiag_card);
        creditCardView.setBackgroundResource(com.vinaygaba.creditcardview.R.drawable.cardbackground_world);
        creditCardView.setCardNumber(generateCreditCardNumber());
        creditCardView.setExpiryDate(generateExpiryDate());
        creditCardView.setCardName(user.getFullName());

        spinCardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("Mastercard"))
                    creditCardView.setType(CardType.MASTERCARD);

                else if (parent.getItemAtPosition(position).toString().equals("Visa"))
                    creditCardView.setType(CardType.VISA);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        builder.setPositiveButton("Add Card", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    CreditCard creditCard = new CreditCard(creditCardView.getCardNumber(),
                            creditCardView.getCardName(),
                            new SimpleDateFormat("MM/yy").parse(creditCardView.getExpiryDate()),
                            CVVGenerator(),
                            creditCardView.getType(), bankAccount.getIban());
                    mListener.onCardAdded(creditCard);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

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

    public static String generateCreditCardNumber() {
        String ccNumber = "4140"; // start with 4140
        int length = 16 - ccNumber.length(); // remaining digits
        Random random = new Random();

        // Generate the remaining 12 digits of the credit card number
        for (int i = 0; i < length - 1; i++) {
            int digit = random.nextInt(10);
            ccNumber += digit;
        }

        // Generate the last digit (the check digit)
        int checkDigit = getCheckDigit(ccNumber);
        ccNumber += checkDigit;

        return ccNumber;
    }

    private static int getCheckDigit(String ccNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = ccNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(ccNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        int checkDigit = (sum % 10);
        if (checkDigit > 0) {
            checkDigit = 10 - checkDigit;
        }
        return checkDigit;
    }

    public static String generateExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
        calendar.add(Calendar.YEAR, 3); // Add 3 years to today's date
        return dateFormat.format(calendar.getTime());
    }

    public int CVVGenerator() {
        Random rand = new Random();
        int firstDigit = 3;
        int secondDigit = rand.nextInt(5) + 5; // Generate a random number between 5 and 9
        int thirdDigit = rand.nextInt(10); // Generate a random number between 0 and 9
        return firstDigit * 100 + secondDigit * 10 + thirdDigit;
    }

    public interface AddCardListener {
        void onCardAdded(CreditCard creditCard);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (AddCardListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddCardListener");
        }
    }

}

