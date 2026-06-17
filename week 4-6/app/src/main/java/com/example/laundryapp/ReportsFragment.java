package com.example.laundryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.List;

public class ReportsFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private TextView tvTotalOrders, tvCompletedOrders, tvPendingOrders, tvTotalRevenue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_reports, container, false);

        dbHelper = new DatabaseHelper(getContext());
        tvTotalOrders = view.findViewById(R.id.tvReportTotalOrders);
        tvCompletedOrders = view.findViewById(R.id.tvReportCompletedOrders);
        tvPendingOrders = view.findViewById(R.id.tvReportPendingOrders);
        tvTotalRevenue = view.findViewById(R.id.tvReportTotalRevenue);

        loadReportData();

        return view;
    }

    // 8. Reports Screen - Retrieve all report data from SQLite
    private void loadReportData() {
        List<Order> orders = dbHelper.getAllOrders();
        
        int totalOrders = orders.size();
        int completedOrders = 0;
        int pendingOrders = 0;
        double totalRevenue = 0;

        for (Order o : orders) {
            totalRevenue += o.getTotalAmount();
            if ("Completed".equalsIgnoreCase(o.getOrderStatus())) {
                completedOrders++;
            } else if ("Pending".equalsIgnoreCase(o.getOrderStatus())) {
                pendingOrders++;
            }
        }

        tvTotalOrders.setText(String.valueOf(totalOrders));
        tvCompletedOrders.setText(String.valueOf(completedOrders));
        tvPendingOrders.setText(String.valueOf(pendingOrders));
        tvTotalRevenue.setText("Sh. " + (int)totalRevenue);
    }
}
