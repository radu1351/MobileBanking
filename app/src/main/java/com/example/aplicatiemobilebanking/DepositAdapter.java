package com.example.aplicatiemobilebanking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aplicatiemobilebanking.classes.Deposit;
import com.example.aplicatiemobilebanking.classes.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DepositAdapter extends ArrayAdapter<Deposit> {
    private final Context context;
    private final int resource;
    private final List<Deposit> deposits;
    private final LayoutInflater inflater;

    public DepositAdapter(@NonNull Context context, int resource, @NonNull List<Deposit> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.deposits = objects;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        Deposit deposit = deposits.get(position);

        addMaturityDate(deposit.getNumberOfMonths(), view);
        addMaturityRate(deposit.getMaturityRate(), view);

        return view;
    }

    private void addMaturityDate(int numberOfMonths, View view) {
        TextView textView = view.findViewById(R.id.lvDeposit_tvMaturityDate);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, numberOfMonths);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String endDate = dateFormat.format(calendar.getTime());

        textView.setText(context.getString(R.string.maturity_end_date, endDate));
    }

    private void addMaturityRate(float maturityRate, View view) {
        TextView textView = view.findViewById(R.id.lvDeposit_tvMaturityRate);
        textView.setText(String.format("%.2f", maturityRate) + " RON");
    }


}
