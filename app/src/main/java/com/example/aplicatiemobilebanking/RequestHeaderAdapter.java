package com.example.aplicatiemobilebanking;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Request;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeSet;

public class RequestHeaderAdapter extends BaseAdapter {

    private static final int TYPE_ITEM_INCOMING = 0;
    private static final int TYPE_ITEM_OUTGOING = 1;
    private static final int TYPE_SEPARATOR = 2;

    private ArrayList<Request> mData = new ArrayList<>();
    private TreeSet<Integer> sectionHeader = new TreeSet<>();

    private LayoutInflater mInflater;

    BankAccount bankAccount;
    Context context;

    public TreeSet<Integer> getSectionHeader() {
        return sectionHeader;
    }

    public RequestHeaderAdapter(Context context, BankAccount bankAccount) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.bankAccount = bankAccount;
    }

    public void addItem(final Request item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final Request item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (sectionHeader.contains(position)) {
            return TYPE_SEPARATOR;
        } else {
            Request request = mData.get(position);
            return request.getSenderIban().equals(bankAccount.getIban()) ? TYPE_ITEM_INCOMING : TYPE_ITEM_OUTGOING;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Request getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (rowType) {
                case TYPE_ITEM_INCOMING:
                    convertView = inflater.inflate(R.layout.lv_incoming_request_row_item, parent, false);
                    holder.textView1 = convertView.findViewById(R.id.lvIncomingReq_tvRequesterName);
                    holder.textView2 = convertView.findViewById(R.id.lvIncomingReq_tvRequesterIban);
                    holder.btView = convertView.findViewById(R.id.lvIncomingReq_btView);
                    holder.btView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("REQUEST", mData.get(position));
                            bundle.putSerializable("BANKACCOUNT", bankAccount);
                            ViewRequestDialog viewRequestDialog = new ViewRequestDialog();
                            viewRequestDialog.setArguments(bundle);
                            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                            viewRequestDialog.show(fragmentManager, "View Transfer Dialog");
                        }
                    });
                    break;
                case TYPE_ITEM_OUTGOING:
                    convertView = inflater.inflate(R.layout.lv_outgoing_request_row_item, parent, false);
                    holder.textView1 = convertView.findViewById(R.id.lvOutgoingReq_tvSenderName);
                    holder.textView2 = convertView.findViewById(R.id.lvOutgoingReq_tvSenderIban);
                    holder.textView3 = convertView.findViewById(R.id.lvOutgoingReq_tvAmount);
                    holder.textView4 = convertView.findViewById(R.id.lvOutgoingReq_tvStatus);
                    break;
                case TYPE_SEPARATOR:
                    convertView = inflater.inflate(R.layout.lv_header, parent, false);
                    holder.textView1 = convertView.findViewById(R.id.textSeparator);
                    break;
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Request request = mData.get(position);
        switch (rowType) {
            case TYPE_ITEM_INCOMING:
                holder.textView1.setText(request.getRequesterFullName());
                holder.textView2.setText(request.getRequesterIban());
                break;
            case TYPE_ITEM_OUTGOING:
                holder.textView1.setText(request.getSenderFullName());
                holder.textView2.setText(request.getSenderIban());
                holder.textView3.setText(Float.toString(request.getAmount()) + " RON");
                holder.textView4.setText(request.getState() == 0 ? "In Progress" : request.getState() == 1 ? "Accepted" : "Declined");
                int stateColor = request.getState() == 0 ? R.color.black : request.getState() == 1 ? R.color.dark_green : R.color.red;
                holder.textView4.setTextColor(ContextCompat.getColor(convertView.getContext(), stateColor));
                break;
            case TYPE_SEPARATOR:
                holder.textView1.setText(request.getSenderIban().equals(bankAccount.getIban()) ? "Incoming Requests" : "Outgoing Requests");
                break;
        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
        public TextView textView4;
        public Button btView;
    }
}
