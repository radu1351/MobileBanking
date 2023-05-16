package com.example.aplicatiemobilebanking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aplicatiemobilebanking.classes.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    private Context context;
    private int resource;
    private List<Transaction> transactions;
    private LayoutInflater inflater;

    public TransactionAdapter(@NonNull Context context, int resource, @NonNull List<Transaction> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.transactions = objects;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        Transaction transaction = transactions.get(position);

        addMerchant(transaction.getMerchant(), view);
        addAmmount(transaction.getAmount(), view);
        addDate(transaction.getDate(), view);

        return view;
    }

    private void addMerchant(String merchant, View view) {
        TextView textView = view.findViewById(R.id.lvTransactions_merchant);
        String value = merchant;
        textView.setText(value);
    }

    private void addAmmount(float ammount, View view) {
        TextView textView = view.findViewById(R.id.lvTransactions_ammount);
        textView.setText("- " + String.valueOf(ammount) + " RON");
    }

    private void addDate(Date date, View view) {
        TextView textView = view.findViewById(R.id.lvTransactions_date);
        textView.setText(new SimpleDateFormat("dd MMM").format(date));
    }

}
