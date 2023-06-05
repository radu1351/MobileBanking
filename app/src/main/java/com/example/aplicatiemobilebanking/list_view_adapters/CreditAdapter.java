package com.example.aplicatiemobilebanking.list_view_adapters;

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

import com.example.aplicatiemobilebanking.R;
import com.example.aplicatiemobilebanking.classes.Credit;
import com.example.aplicatiemobilebanking.dialogs.ViewCreditDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CreditAdapter extends ArrayAdapter<Credit> {
    private final Context context;
    private final int resource;
    private final List<Credit> credits;
    private final LayoutInflater inflater;

    public CreditAdapter(@NonNull Context context, int resource, @NonNull List<Credit> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.credits = objects;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        Credit credit = credits.get(position);

        addLoanedAmount(credit.getLoanedAmount(), view);
        addMaturityDate(credit.getMaturityDate(), view);
        loadViewCreditButton(credit, view);

        return view;
    }


    private void addLoanedAmount(Float loanedAmount, View view) {
        TextView tvLoanedAmount = view.findViewById(R.id.lvCredit_tvLoanedAmount);
        tvLoanedAmount.setText(context.getString(R.string.loaned_amount, String.valueOf(loanedAmount)));
    }

    private void addMaturityDate(Date maturityDate, View view) {
        TextView tvLoanedAmount = view.findViewById(R.id.lvCredit_tvMaturityDate);
        tvLoanedAmount.setText(context.getString(R.string.maturity_date,
                new SimpleDateFormat("dd MMM yyyy").format(maturityDate)));
    }

    private void loadViewCreditButton(Credit credit, View view) {
        Button button = view.findViewById(R.id.lvCredit_btViewCredit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("CREDIT", credit);
                ViewCreditDialog dialog = new ViewCreditDialog();
                dialog.setArguments(bundle);
                dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "ViewCreditDialog");
            }
        });
    }
}
