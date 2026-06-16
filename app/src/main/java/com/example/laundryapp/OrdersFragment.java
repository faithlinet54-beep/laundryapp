package com.example.laundryapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class OrdersFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Order> allOrders;
    private TextView tvTotalCount, tvActiveCount, tvReadyCount;
    private EditText etSearch;
    private ImageView ivTotalIcon, ivActiveIcon, ivReadyIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_orders, container, false);

        dbHelper = new DatabaseHelper(getContext());
        rvOrders = view.findViewById(R.id.rvOrders);
        tvTotalCount = view.findViewById(R.id.tvTotalCount);
        tvActiveCount = view.findViewById(R.id.tvActiveCount);
        tvReadyCount = view.findViewById(R.id.tvReadyCount);
        etSearch = view.findViewById(R.id.etSearch);
        
        ivTotalIcon = view.findViewById(R.id.ivTotalIcon);
        ivActiveIcon = view.findViewById(R.id.ivActiveIcon);
        ivReadyIcon = view.findViewById(R.id.ivReadyIcon);

        // Load Metric Icons from internet (Figma-style)
        Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/3502/3502688.png").into(ivTotalIcon); // Ledger
        Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/109/109613.png").into(ivActiveIcon); // Clock
        Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/190/190411.png").into(ivReadyIcon); // Check

        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        allOrders = dbHelper.getAllOrders();
        adapter = new OrderAdapter(allOrders, this);
        rvOrders.setAdapter(adapter);

        updateMetrics();

        FloatingActionButton fabAddOrder = view.findViewById(R.id.fabAddOrder);
        fabAddOrder.setOnClickListener(v -> showOrderDialog(null));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchOrders(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void searchOrders(String query) {
        if (query.isEmpty()) {
            allOrders = dbHelper.getAllOrders();
        } else {
            allOrders = dbHelper.searchOrders(query);
        }
        adapter.setOrders(allOrders);
    }

    private void updateMetrics() {
        List<Order> orders = dbHelper.getAllOrders();
        tvTotalCount.setText(String.valueOf(orders.size()));
        
        int active = 0;
        int ready = 0;
        for (Order o : orders) {
            if ("washing".equalsIgnoreCase(o.getOrderStatus())) active++;
            if ("ready".equalsIgnoreCase(o.getOrderStatus())) ready++;
        }
        tvActiveCount.setText(String.valueOf(active));
        tvReadyCount.setText(String.valueOf(ready));
    }

    private void showOrderDialog(@Nullable Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_order, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextInputEditText etName = dialogView.findViewById(R.id.etCustomerName);
        TextInputEditText etService = dialogView.findViewById(R.id.etServiceType);
        TextInputEditText etAmount = dialogView.findViewById(R.id.etAmount);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);

        if (order != null) {
            tvTitle.setText("Update Order");
            etName.setText(order.getCustomerName());
            etService.setText(order.getServiceType());
            etAmount.setText(String.valueOf(order.getAmountCharged()));
            
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerStatus.getAdapter();
            int pos = adapter.getPosition(order.getOrderStatus());
            spinnerStatus.setSelection(pos);

            builder.setNeutralButton("Delete", (dialog, which) -> {
                dbHelper.deleteOrder(order.getId());
                refreshData();
                Toast.makeText(getContext(), "Order Deleted", Toast.LENGTH_SHORT).show();
            });
        }

        builder.setPositiveButton(order == null ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String service = etService.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem().toString();

            if (name.isEmpty() || service.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);

            if (order == null) {
                dbHelper.addOrder(name, service, amount, status);
                Toast.makeText(getContext(), "Order Added", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.updateOrder(order.getId(), name, service, amount, status);
                Toast.makeText(getContext(), "Order Updated", Toast.LENGTH_SHORT).show();
            }
            refreshData();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void refreshData() {
        allOrders = dbHelper.getAllOrders();
        adapter.setOrders(allOrders);
        updateMetrics();
    }

    @Override
    public void onOrderClick(Order order) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new OrdersDetailsFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
}
