package com.example.aplicatiemobilebanking;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicatiemobilebanking.classes.Deposit;

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

        addMaturityDate(deposit.getMaturityDate(), view);
        addMaturityRate(deposit.getMaturityRate(), view);
        loadViewDepositButton(deposit, view);

        return view;
    }

    private void addMaturityDate(Date maturityDate, View view) {
        TextView textView = view.findViewById(R.id.lvDeposit_tvMaturityDate);
        textView.setText(context.getString(R.string.maturity_date,
                new SimpleDateFormat("dd MMM yyyy").format(maturityDate)));
    }

    private void addMaturityRate(float maturityRate, View view) {
        TextView textView = view.findViewById(R.id.lvDeposit_tvMaturityRate);
        textView.setText(String.format("%.2f", maturityRate) + " RON");
    }

    private void loadViewDepositButton(Deposit deposit, View view) {
        Button button = view.findViewById(R.id.lvDeposit_btViewDeposit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("DEPOSIT", deposit);
                ViewDepositDialog dialog = new ViewDepositDialog();
                dialog.setArguments(bundle);
                dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "ViewDepositDialog");
            }
        });
    }
}
