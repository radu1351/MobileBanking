package com.example.aplicatiemobilebanking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.aplicatiemobilebanking.classes.BankAccount;
import com.example.aplicatiemobilebanking.classes.Transfer;

class TransferHeaderAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<Transfer> mData = new ArrayList<Transfer>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;

    BankAccount bankAccount;

    public TreeSet<Integer> getSectionHeader() {
        return sectionHeader;
    }

    public TransferHeaderAdapter(Context context, BankAccount bankAccount) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bankAccount = bankAccount;
    }

    public void addItem(final Transfer item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final Transfer item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Transfer getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.lv_transfers_row_item, null);
                    holder.textView1 = (TextView) convertView.findViewById(R.id.lvTransfers_tvProcessed);
                    holder.textView2 = (TextView) convertView.findViewById(R.id.lvTransfers_tvAmmount);
                    holder.textView3 = (TextView) convertView.findViewById(R.id.lvTransfers_tvDate);
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.lv_header, null);
                    holder.textView1 = (TextView) convertView.findViewById(R.id.textSeparator);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (rowType) {
            case TYPE_ITEM:
                holder.textView1.setText(R.string.processed);

                if (mData.get(position).getRecipientIban().equals(bankAccount.getIban())) {
                    // This account recieved money, so the sum is positive.
                    holder.textView2.setText(String.valueOf(" + " + mData.get(position).getAmount()) + " RON");
                    holder.textView2.setTextColor(Color.GREEN);
                } else { // This account sent money, so the sum is negative
                    holder.textView2.setText(String.valueOf("- " + mData.get(position).getAmount()) + " RON");
                    holder.textView2.setTextColor(Color.RED);
                }
                holder.textView3.setText((new SimpleDateFormat("dd MMM yyyy").format(mData.get(position).getDate())));
                break;
            case TYPE_SEPARATOR:
                holder.textView1.setText(new SimpleDateFormat("MMMM").format(mData.get(position).getDate()));
                break;
        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
    }

}
