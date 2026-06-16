package com.example.laundryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class EarningsFragment extends Fragment {

    private LinearLayout containerTransactionsList;

    private static class TransactionData {
        String id, customer, time, amount;

        TransactionData(String id, String customer, String time, String amount) {
            this.id = id;
            this.customer = customer;
            this.time = time;
            this.amount = amount;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_earnings, container, false);

        containerTransactionsList = view.findViewById(R.id.containerTransactionsList);
        TextView tvTotalEarningsValue = view.findViewById(R.id.tvTotalEarningsValue);
        TextView tvTodayEarningsValue = view.findViewById(R.id.tvTodayEarningsValue);
        ImageView ivCashIcon = view.findViewById(R.id.ivCashIcon);
        ImageView ivRecentIcon = view.findViewById(R.id.ivRecentIcon);

        // Load Header Icons
        Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/2488/2488749.png").into(ivCashIcon);
        Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/3502/3502688.png").into(ivRecentIcon);

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<Order> orders = dbHelper.getAllOrders();
        
        double total = 0;
        List<TransactionData> transactions = new ArrayList<>();
        for (Order o : orders) {
            total += o.getAmountCharged();
            transactions.add(new TransactionData("Order #" + o.getId(), o.getCustomerName(), "Completed", "Ksh." + o.getAmountCharged()));
        }

        tvTotalEarningsValue.setText("Ksh. " + (int)total);
        tvTodayEarningsValue.setText("Ksh. " + (int)total);

        populateTransactions(transactions);

        return view;
    }

    private void populateTransactions(List<TransactionData> transactions) {
        if (containerTransactionsList == null) return;
        containerTransactionsList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (TransactionData transaction : transactions) {
            View row = inflater.inflate(R.layout.item_transaction_row, containerTransactionsList, false);

            ((TextView) row.findViewById(R.id.tvOrderNumber)).setText(transaction.id);
            ((TextView) row.findViewById(R.id.tvCustomerName)).setText(transaction.customer);
            ((TextView) row.findViewById(R.id.tvTime)).setText(transaction.time);
            ((TextView) row.findViewById(R.id.tvAmount)).setText(transaction.amount);
            
            ImageView ivStatusCheck = row.findViewById(R.id.ivStatusCheck);
            Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/190/190411.png").into(ivStatusCheck);

            containerTransactionsList.addView(row);
        }
    }
}
