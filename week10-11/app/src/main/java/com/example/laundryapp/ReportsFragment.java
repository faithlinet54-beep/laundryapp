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
    private TextView tvTotalRevenue, tvTotalOrders, tvCompletedOrders, tvPendingOrders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_reports, container, false);

        dbHelper = new DatabaseHelper(getContext());
        tvTotalRevenue = view.findViewById(R.id.tvReportTotalRevenue);
        tvTotalOrders = view.findViewById(R.id.tvReportTotalOrders);
        tvCompletedOrders = view.findViewById(R.id.tvReportCompletedOrders);
        tvPendingOrders = view.findViewById(R.id.tvReportPendingOrders);

        loadReportData();

        return view;
    }

    private void loadReportData() {
        List<Order> orders = dbHelper.getAllOrders();

        double totalRevenue = 0;
        int totalOrders = orders.size();
        int completedOrdersCount = 0;
        int pendingOrdersCount = 0;

        for (Order o : orders) {
            totalRevenue += o.getTotalAmount();
            if ("Completed".equalsIgnoreCase(o.getOrderStatus())) {
                completedOrdersCount++;
            } else {
                pendingOrdersCount++;
            }
        }

        if (getContext() != null) {
            tvTotalRevenue.setText(getString(R.string.total_revenue_format, (int) totalRevenue));
            tvTotalOrders.setText(String.valueOf(totalOrders));
            tvCompletedOrders.setText(String.valueOf(completedOrdersCount));
            tvPendingOrders.setText(String.valueOf(pendingOrdersCount));
        }
    }
}
