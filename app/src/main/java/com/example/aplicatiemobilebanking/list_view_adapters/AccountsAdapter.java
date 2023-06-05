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
import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.User;
import com.example.aplicatiemobilebanking.dialogs.ManageAccountDialog;

import java.util.List;

public class AccountsAdapter extends ArrayAdapter<BankAccount> {
    private final Context context;
    private final int resource;
    private final List<BankAccount> bankAccounts;
    private final List<User> users;
    private final LayoutInflater inflater;
    private int lastClickedPosition = -1;
    private Button btManageAccount;
    ManageAccountDialog dialog;

    public AccountsAdapter(@NonNull Context context, int resource, @NonNull List<BankAccount> bankAccounts,
                           List<User> users, LayoutInflater inflater) {
        super(context, resource, bankAccounts);
        this.context = context;
        this.resource = resource;
        this.bankAccounts = bankAccounts;
        this.users = users;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        BankAccount bankAccount = bankAccounts.get(position);

        User associatedUser = new User();
        for (User user : users) {
            if (user.getIdentificationNumber().equals(bankAccount.getUserPersonalID())) {
                associatedUser = user;
                break;
            }
        }

        addUserFullName(bankAccount, associatedUser, view);
        addBankAccountIban(bankAccount, view);
        loadManageAccountButton(bankAccount, associatedUser, view, position);

        return view;
    }


    private void addUserFullName(BankAccount bankAccount, User user, View view) {
        TextView tvUserFullName = view.findViewById(R.id.lvAccounts_tvUser);
        tvUserFullName.setText(user.getFullName());
    }

    private void addBankAccountIban(BankAccount bankAccount, View view) {
        TextView tvIban = view.findViewById(R.id.lvAccounts_tvIban);
        tvIban.setText(bankAccount.getIban());
    }

    public void openLastAccountClicked() {
        super.notifyDataSetChanged();
        dialog.dismiss();
        if (lastClickedPosition != -1) {
            View lastClickedView = getView(lastClickedPosition, null, null);
            if (lastClickedView != null) {
                btManageAccount = lastClickedView.findViewById(R.id.lvAccounts_btManageAccount);
                btManageAccount.performClick();
            }
        }
    }

    private void loadManageAccountButton(BankAccount bankAccount, User user, View view, int position) {
        btManageAccount = view.findViewById(R.id.lvAccounts_btManageAccount);
        btManageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastClickedPosition = position;
                Bundle bundle = new Bundle();
                bundle.putSerializable("BANKACCOUNT", bankAccount);
                bundle.putSerializable("USER", user);
                dialog = new ManageAccountDialog();
                dialog.setArguments(bundle);
                dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "ManageAccountDialog");
            }
        });
    }
}
