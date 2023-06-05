package com.example.aplicatiemobilebanking.list_view_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.CreditCard;
import com.example.aplicatiemobilebanking.classes.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreditCardAdapter extends ArrayAdapter<CreditCard> {

    private Context context;
    private int resource;
    private ArrayList<CreditCard> creditCards;
    private LayoutInflater inflater;
    private boolean shortText;

    public CreditCardAdapter(@NonNull Context context, int resource,
                             @NonNull ArrayList<CreditCard> objects, LayoutInflater inflater, boolean shortText) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.creditCards = objects;
        this.inflater = inflater;
        this.shortText = shortText;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        CreditCard creditCard = creditCards.get(position);

        addDescription(creditCard.getCardNumber(), creditCard.getCardType(), view);

        Button btRemove = view.findViewById(R.id.lvCards_btRemove);
        btRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creditCards.remove(position);
                notifyDataSetChanged();
            }
        });
        return view;
    }

    private void addDescription(String cardNumber, int cardType, View view) {
        TextView textView = view.findViewById(R.id.lvCards_tvCard);
        String value;
        if (!shortText) {
            value = context.getString(R.string.card_description, cardType == 0 ? "Visa" : "Mastercard",
                    cardNumber.substring(12, 16));
        }
        else{
            value = context.getString(R.string.card_description, cardType == 0 ? "Visa" : "M.C.",
                    cardNumber.substring(12, 16));
        }
            textView.setText(value);
    }


}