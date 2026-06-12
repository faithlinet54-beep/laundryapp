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

public class DashboardFragment extends Fragment {

    private LinearLayout containerOrderRows;

    private static class OrderItem {
        String id, customer, service, status;
        int statusColor;

        OrderItem(String id, String customer, String service, String status, int statusColor) {
            this.id = id;
            this.customer = customer;
            this.service = service;
            this.status = status;
            this.statusColor = statusColor;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_dashboard, container, false);

        containerOrderRows = view.findViewById(R.id.containerOrderRows);
        TextView tvViewAll = view.findViewById(R.id.tvViewAll);
        TextView tvWelcome = view.findViewById(R.id.tvWelcome);

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
        List<OrderItem> orders = new ArrayList<>();
        // John Doe - Washing (Blue status color)
        orders.add(new OrderItem("#1024", "John Doe", "Washing", "Washing", 0xFF2563EB));
        // Mary M - Ready (Purple status color)
        orders.add(new OrderItem("#1023", "Mary M", "Dry clean", "Ready", 0xFF7C3AED));

        if (containerOrderRows != null) {
            containerOrderRows.removeAllViews();
            for (OrderItem order : orders) {
                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(0, 24, 0, 24);
                row.setGravity(android.view.Gravity.CENTER_VERTICAL);

                TextView tvId = new TextView(getContext());
                tvId.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                tvId.setText(order.id);
                tvId.setTextColor(0xFF111827);
                tvId.setTextSize(13f);

                TextView tvCustomer = new TextView(getContext());
                tvCustomer.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.3f));
                tvCustomer.setText(order.customer);
                tvCustomer.setTextColor(0xFF111827);
                tvCustomer.setTextSize(13f);
                tvCustomer.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);

                TextView tvService = new TextView(getContext());
                tvService.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f));
                tvService.setText(order.service);
                tvService.setTextColor(0xFF111827);
                tvService.setTextSize(13f);

                TextView tvStatus = new TextView(getContext());
                tvStatus.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                tvStatus.setText(order.status);
                tvStatus.setTextColor(order.statusColor);
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
