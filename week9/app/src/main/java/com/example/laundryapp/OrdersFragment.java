package com.example.laundryapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orders;
    private DatabaseHelper dbHelper;
    private TextView tvTotalCount, tvActiveCount, tvReadyCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_orders, container, false);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.rvOrders);
        tvTotalCount = view.findViewById(R.id.tvTotalCount);
        tvActiveCount = view.findViewById(R.id.tvActiveCount);
        tvReadyCount = view.findViewById(R.id.tvReadyCount);
        
        ImageView ivTotalIcon = view.findViewById(R.id.ivTotalIcon);
        ImageView ivActiveIcon = view.findViewById(R.id.ivActiveIcon);
        ImageView ivReadyIcon = view.findViewById(R.id.ivReadyIcon);

        if (getContext() != null) {
            Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/3502/3502688.png").into(ivTotalIcon);
            Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/3003/3003984.png").into(ivActiveIcon);
            Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/190/190411.png").into(ivReadyIcon);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orders = new ArrayList<>();
        loadOrders();

        FloatingActionButton fabAddOrder = view.findViewById(R.id.fabAddOrder);
        if (fabAddOrder != null) {
            fabAddOrder.setOnClickListener(v -> showAddOrderDialog());
        }

        return view;
    }

    private void loadOrders() {
        orders = dbHelper.getAllOrders();
        adapter = new OrderAdapter(orders, this);
        recyclerView.setAdapter(adapter);
        updateStats();
    }

    private void updateStats() {
        if (orders == null) return;
        
        int total = orders.size();
        int active = 0;
        int ready = 0;

        for (Order o : orders) {
            if ("Washing".equalsIgnoreCase(o.getOrderStatus())) active++;
            if ("Ready for Pickup".equalsIgnoreCase(o.getOrderStatus())) ready++;
        }

        tvTotalCount.setText(String.valueOf(total));
        tvActiveCount.setText(String.valueOf(active));
        tvReadyCount.setText(String.valueOf(ready));
    }

    @Override
    public void onOrderClick(Order order) {
        showUpdateOrderDialog(order);
    }

    @Override
    public void onDeleteClick(Order order) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Order")
                .setMessage("Are you sure you want to delete this order?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteOrder(order.getId());
                    loadOrders();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showAddOrderDialog() {
        if (getContext() == null) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_order, null);
        EditText etName = dialogView.findViewById(R.id.etCustomerName);
        EditText etPhone = dialogView.findViewById(R.id.etPhoneNumber);
        EditText etService = dialogView.findViewById(R.id.etServiceType);
        EditText etQty = dialogView.findViewById(R.id.etQuantity);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);
        Button btnSave = dialogView.findViewById(R.id.btnSaveOrder);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String service = etService.getText().toString().trim();
            String qtyStr = etQty.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem() != null ? spinnerStatus.getSelectedItem().toString() : "Washing";

            if (!name.isEmpty() && !service.isEmpty() && !qtyStr.isEmpty() && !amountStr.isEmpty()) {
                int qty = Integer.parseInt(qtyStr);
                double amount = Double.parseDouble(amountStr);
                Order order = new Order(0, name, phone, service, qty, amount, status);
                dbHelper.addOrder(order);
                loadOrders();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showUpdateOrderDialog(Order order) {
        if (getContext() == null) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_order, null);
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etName = dialogView.findViewById(R.id.etCustomerName);
        EditText etPhone = dialogView.findViewById(R.id.etPhoneNumber);
        EditText etService = dialogView.findViewById(R.id.etServiceType);
        EditText etQty = dialogView.findViewById(R.id.etQuantity);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);
        Button btnSave = dialogView.findViewById(R.id.btnSaveOrder);

        if (tvTitle != null) tvTitle.setText(getString(R.string.update_order));
        etName.setText(order.getCustomerName());
        etPhone.setText(order.getPhoneNumber());
        etService.setText(order.getServiceType());
        etQty.setText(String.valueOf(order.getQuantity()));
        etAmount.setText(String.valueOf(order.getTotalAmount()));
        btnSave.setText("Update");

        if (spinnerStatus != null) {
            @SuppressWarnings("unchecked")
            ArrayAdapter<CharSequence> arrayAdapter = (ArrayAdapter<CharSequence>) spinnerStatus.getAdapter();
            if (arrayAdapter != null) {
                int position = arrayAdapter.getPosition(order.getOrderStatus());
                spinnerStatus.setSelection(position);
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String service = etService.getText().toString().trim();
            int qty = Integer.parseInt(etQty.getText().toString().trim());
            double amount = Double.parseDouble(etAmount.getText().toString().trim());
            String status = spinnerStatus != null && spinnerStatus.getSelectedItem() != null ? spinnerStatus.getSelectedItem().toString() : order.getOrderStatus();

            order.setCustomerName(name);
            order.setPhoneNumber(phone);
            order.setServiceType(service);
            order.setQuantity(qty);
            order.setTotalAmount(amount);
            order.setOrderStatus(status);
            
            dbHelper.updateOrder(order);
            loadOrders();
            dialog.dismiss();
        });

        dialog.show();
    }
}
