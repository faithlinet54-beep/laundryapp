package com.example.laundryapp;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class DashboardFragment extends Fragment {

    private LinearLayout containerOrderRows;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_dashboard, container, false);

        containerOrderRows = view.findViewById(R.id.containerOrderRows);
        TextView tvViewAll = view.findViewById(R.id.tvViewAll);
        TextView tvWelcome = view.findViewById(R.id.tvWelcome);
        TextView tvEarningsValue = view.findViewById(R.id.tvEarningsValue);
        TextView tvOrdersValue = view.findViewById(R.id.tvOrdersValue);
        
        ImageView ivEarningsIcon = view.findViewById(R.id.ivEarningsIcon);
        ImageView ivOrdersIcon = view.findViewById(R.id.ivOrdersIcon);
        ImageView ivLiveIndicator = view.findViewById(R.id.ivLiveIndicator);

        // Load Icons from internet (Figma-style)
        Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/2488/2488749.png").into(ivEarningsIcon);
        Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/679/679821.png").into(ivOrdersIcon);
        Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/595/595067.png").into(ivLiveIndicator);

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<Order> orders = dbHelper.getAllOrders();
        
        tvOrdersValue.setText(String.valueOf(orders.size()));
        double total = 0;
        for (Order o : orders) total += o.getAmountCharged();
        tvEarningsValue.setText("Sh." + (int)total);

        // Styling the welcome text: "Welcome back, Owner!"
        String welcomeText = "Welcome back, Owner!";
        SpannableString spannableString = new SpannableString(welcomeText);
        // "Owner!" starts at index 14 and ends at 20
        spannableString.setSpan(new ForegroundColorSpan(0xFF7C3AED), 14, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvWelcome.setText(spannableString);

        tvViewAll.setOnClickListener(v -> {
            if (getActivity() != null) {
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.nav_orders);
                }
            }
        });

        populateOrderTable();

        return view;
    }

    private void populateOrderTable() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<Order> orders = dbHelper.getAllOrders();

        if (containerOrderRows != null) {
            containerOrderRows.removeAllViews();
            
            // Limit to top 5 recent orders for dashboard
            int count = Math.min(orders.size(), 5);
            for (int i = 0; i < count; i++) {
                Order order = orders.get(i);
                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(0, 24, 0, 24);
                row.setGravity(android.view.Gravity.CENTER_VERTICAL);

                TextView tvId = new TextView(getContext());
                tvId.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                tvId.setText("#" + order.getId());
                tvId.setTextColor(0xFF111827);
                tvId.setTextSize(13f);

                TextView tvCustomer = new TextView(getContext());
                tvCustomer.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.3f));
                tvCustomer.setText(order.getCustomerName());
                tvCustomer.setTextColor(0xFF111827);
                tvCustomer.setTextSize(13f);
                tvCustomer.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);

                TextView tvService = new TextView(getContext());
                tvService.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f));
                tvService.setText(order.getServiceType());
                tvService.setTextColor(0xFF111827);
                tvService.setTextSize(13f);

                TextView tvStatus = new TextView(getContext());
                tvStatus.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                tvStatus.setText(order.getOrderStatus());
                
                int statusColor = 0xFF2563EB; // Washing
                if ("ready".equalsIgnoreCase(order.getOrderStatus())) {
                    statusColor = 0xFF7C3AED; // Ready
                }
                tvStatus.setTextColor(statusColor);
                tvStatus.setGravity(Gravity.END);
                tvStatus.setTextSize(13f);
                tvStatus.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);

                row.addView(tvId);
                row.addView(tvCustomer);
                row.addView(tvService);
                row.addView(tvStatus);

                View divider = new View(getContext());
                divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                divider.setBackgroundColor(0xFFD1D5DB);

                containerOrderRows.addView(row);
                containerOrderRows.addView(divider);
            }
        }
    }
}
