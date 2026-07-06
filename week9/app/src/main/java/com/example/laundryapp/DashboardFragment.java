package com.example.laundryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class DashboardFragment extends Fragment {

    private LinearLayout containerOrderRows;
    private DatabaseHelper dbHelper;
    private TextView tvEarningsValue;
    private TextView tvOrdersValue;
    private TextView tvSubtitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_dashboard, container, false);

        dbHelper = new DatabaseHelper(getContext());
        containerOrderRows = view.findViewById(R.id.containerOrderRows);
        TextView tvViewAll = view.findViewById(R.id.tvViewAll);
        TextView tvWelcome = view.findViewById(R.id.tvWelcome);
        tvEarningsValue = view.findViewById(R.id.tvEarningsValue);
        tvOrdersValue = view.findViewById(R.id.tvOrdersValue);
        tvSubtitle = view.findViewById(R.id.tvSubtitle);
        ImageButton btnLogout = view.findViewById(R.id.btnLogout);

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    SharedPreferences prefs = activity.getSharedPreferences("LaundryPrefs", Context.MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    Toast.makeText(getContext(), getString(R.string.logout_success), Toast.LENGTH_SHORT).show();

                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new LoginFragment())
                            .commit();
                    
                    View bottomNav = activity.findViewById(R.id.bottom_navigation);
                    if (bottomNav != null) bottomNav.setVisibility(View.GONE);
                }
            });
        }
        
        ImageView ivEarningsIcon = view.findViewById(R.id.ivEarningsIcon);
        ImageView ivOrdersIcon = view.findViewById(R.id.ivOrdersIcon);
        ImageView ivLiveIndicator = view.findViewById(R.id.ivLiveIndicator);

        if (getContext() != null) {
            Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/2488/2488749.png").into(ivEarningsIcon);
            Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/679/679821.png").into(ivOrdersIcon);
            Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/595/595067.png").into(ivLiveIndicator);
        }

        FragmentActivity activity = getActivity();
        if (activity != null) {
            SharedPreferences prefs = activity.getSharedPreferences("LaundryPrefs", Context.MODE_PRIVATE);
            String username = prefs.getString("username", "Owner");

            String welcomeText = getString(R.string.welcome_back, username);
            SpannableString spannableString = new SpannableString(welcomeText);
            int start = welcomeText.indexOf(username);
            if (start >= 0) {
                int end = start + username.length();
                spannableString.setSpan(new ForegroundColorSpan(0xFF7C3AED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tvWelcome.setText(spannableString);
        }

        updateDashboardStats();

        if (tvViewAll != null) {
            tvViewAll.setOnClickListener(v -> {
                FragmentActivity act = getActivity();
                if (act != null) {
                    BottomNavigationView bottomNav = act.findViewById(R.id.bottom_navigation);
                    if (bottomNav != null) {
                        bottomNav.setSelectedItemId(R.id.nav_orders);
                    }
                }
            });
        }

        populateOrderTable();

        return view;
    }

    private void updateDashboardStats() {
        List<Order> orders = dbHelper.getAllOrders();
        
        double revenue = 0;
        int activeOrders = 0;
        for (Order o : orders) {
            revenue += o.getTotalAmount();
            if (!"Completed".equalsIgnoreCase(o.getOrderStatus())) {
                activeOrders++;
            }
        }
        
        tvOrdersValue.setText(String.valueOf(activeOrders));
        tvEarningsValue.setText(getString(R.string.total_revenue_format, (int) revenue));
        tvSubtitle.setText(getString(R.string.total_orders_subtitle, orders.size()));
    }

    private void populateOrderTable() {
        List<Order> orders = dbHelper.getAllOrders();

        if (containerOrderRows != null && getContext() != null) {
            containerOrderRows.removeAllViews();
            
            int count = Math.min(orders.size(), 5);
            for (int i = 0; i < count; i++) {
                Order order = orders.get(i);
                View row = createOrderRow(order);
                containerOrderRows.addView(row);

                View divider = new View(getContext());
                divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                divider.setBackgroundColor(0xFFD1D5DB);
                containerOrderRows.addView(divider);
            }
        }
    }

    private View createOrderRow(Order order) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 24, 0, 24);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvId = createCell("#" + order.getId(), 1f, false);
        TextView tvCustomer = createCell(order.getCustomerName(), 1.3f, true);
        TextView tvService = createCell(order.getServiceType(), 1.2f, false);
        TextView tvStatus = createStatusCell(order.getOrderStatus());

        row.addView(tvId);
        row.addView(tvCustomer);
        row.addView(tvService);
        row.addView(tvStatus);

        return row;
    }

    private TextView createCell(String text, float weight, boolean bold) {
        TextView tv = new TextView(getContext());
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight));
        tv.setText(text);
        tv.setTextColor(0xFF111827);
        tv.setTextSize(13f);
        if (bold) tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }

    private TextView createStatusCell(String status) {
        TextView tvStatus = new TextView(getContext());
        tvStatus.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tvStatus.setText(status);
        
        int statusColor;
        if ("Ready for Pickup".equalsIgnoreCase(status)) {
            statusColor = 0xFF7C3AED;
        } else if ("Completed".equalsIgnoreCase(status)) {
            statusColor = 0xFF16A34A;
        } else if ("Washing".equalsIgnoreCase(status)) {
            statusColor = 0xFF2563EB;
        } else {
            statusColor = 0xFFEA580C;
        }

        tvStatus.setTextColor(statusColor);
        tvStatus.setGravity(Gravity.END);
        tvStatus.setTextSize(13f);
        tvStatus.setTypeface(null, Typeface.BOLD);
        return tvStatus;
    }
}
