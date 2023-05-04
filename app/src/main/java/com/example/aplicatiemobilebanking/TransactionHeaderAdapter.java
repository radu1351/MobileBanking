package com.example.aplicatiemobilebanking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeSet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.aplicatiemobilebanking.classes.Transaction;

class TransactionHeaderAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<Transaction> mData = new ArrayList<Transaction>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;

    public TransactionHeaderAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final Transaction item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final Transaction item) {
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
    public Transaction getItem(int position) {
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
                    convertView = mInflater.inflate(R.layout.lv_transactions_row_item, null);
                    holder.textView1 = (TextView) convertView.findViewById(R.id.lvTransactions_merchant);
                    holder.textView2 = (TextView) convertView.findViewById(R.id.lvTransactions_date);
                    holder.textView3 = (TextView) convertView.findViewById(R.id.lvTransactions_ammount);
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
                holder.textView1.setText(mData.get(position).getMerchant());
                holder.textView2.setText((new SimpleDateFormat("HH:mm").format(mData.get(position).getDate())));
                holder.textView3.setText(String.valueOf("- " + mData.get(position).getAmmount()) + " RON");
                break;
            case TYPE_SEPARATOR:
                holder.textView1.setText(new SimpleDateFormat("dd MMM").format(mData.get(position).getDate()));
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
