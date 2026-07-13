package com.example.laundryapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.textfield.TextInputLayout;

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

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orders = new ArrayList<>();
        loadOrders();

        FloatingActionButton fabAddOrder = view.findViewById(R.id.fabAddOrder);
        if (fabAddOrder != null) {
            fabAddOrder.setOnClickListener(v -> showOrderDialog(null));
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
        int activeCount = 0;
        int readyCount = 0;
        for (Order o : orders) {
            if ("Washing".equalsIgnoreCase(o.getOrderStatus())) {
                activeCount++;
            }
            if ("Ready for Pickup".equalsIgnoreCase(o.getOrderStatus())) {
                readyCount++;
            }
        }
        tvTotalCount.setText(String.valueOf(total));
        tvActiveCount.setText(String.valueOf(activeCount));
        tvReadyCount.setText(String.valueOf(readyCount));
    }

    @Override
    public void onOrderClick(Order order) {
        showOrderDialog(order);
    }

    @Override
    public void onDeleteClick(Order order) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete the order for " + order.getCustomerName() + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes, Delete", (dialog, which) -> {
                    dbHelper.deleteOrder(order.getId());
                    loadOrders();
                    Toast.makeText(getContext(), "Order deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showOrderDialog(@Nullable final Order existingOrder) {
        if (getContext() == null) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_order, null);
        
        final TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        final TextInputLayout tilName = dialogView.findViewById(R.id.tilCustomerName);
        final TextInputLayout tilPhone = dialogView.findViewById(R.id.tilPhoneNumber);
        final TextInputLayout tilService = dialogView.findViewById(R.id.tilServiceType);
        final TextInputLayout tilQty = dialogView.findViewById(R.id.tilQuantity);
        final TextInputLayout tilAmount = dialogView.findViewById(R.id.tilAmount);
        
        final EditText etName = dialogView.findViewById(R.id.etCustomerName);
        final EditText etPhone = dialogView.findViewById(R.id.etPhoneNumber);
        final EditText etService = dialogView.findViewById(R.id.etServiceType);
        final EditText etQty = dialogView.findViewById(R.id.etQuantity);
        final EditText etAmount = dialogView.findViewById(R.id.etAmount);
        final Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);
        final Button btnSave = dialogView.findViewById(R.id.btnSaveOrder);

        if (existingOrder != null) {
            tvTitle.setText("Edit Laundry Order");
            etName.setText(existingOrder.getCustomerName());
            etPhone.setText(existingOrder.getPhoneNumber());
            etService.setText(existingOrder.getServiceType());
            etQty.setText(String.valueOf(existingOrder.getQuantity()));
            etAmount.setText(String.valueOf(existingOrder.getTotalAmount()));
            btnSave.setText("Update Order");

            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerStatus.getAdapter();
            if (adapter != null) {
                int pos = adapter.getPosition(existingOrder.getOrderStatus());
                spinnerStatus.setSelection(pos);
            }
        }

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String service = etService.getText().toString().trim();
            String qtyStr = etQty.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem().toString();

            // Reset errors
            tilName.setError(null);
            tilPhone.setError(null);
            tilService.setError(null);
            tilQty.setError(null);
            tilAmount.setError(null);

            boolean isValid = true;

            if (TextUtils.isEmpty(name)) {
                tilName.setError("Customer name is required");
                isValid = false;
            }
            if (TextUtils.isEmpty(phone) || !TextUtils.isDigitsOnly(phone)) {
                tilPhone.setError("Valid phone number is required");
                isValid = false;
            }
            if (TextUtils.isEmpty(service)) {
                tilService.setError("Service type is required");
                isValid = false;
            }
            
            int qty = 0;
            if (TextUtils.isEmpty(qtyStr)) {
                tilQty.setError("Quantity is required");
                isValid = false;
            } else {
                qty = Integer.parseInt(qtyStr);
                if (qty <= 0) {
                    tilQty.setError("Quantity must be positive");
                    isValid = false;
                }
            }

            double amount = 0;
            if (TextUtils.isEmpty(amountStr)) {
                tilAmount.setError("Amount is required");
                isValid = false;
            } else {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    tilAmount.setError("Price must be positive");
                    isValid = false;
                }
            }

            if (isValid) {
                if (existingOrder == null) {
                    Order newOrder = new Order(0, name, phone, service, qty, amount, status);
                    dbHelper.addOrder(newOrder);
                    Toast.makeText(getContext(), "Order added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    existingOrder.setCustomerName(name);
                    existingOrder.setPhoneNumber(phone);
                    existingOrder.setServiceType(service);
                    existingOrder.setQuantity(qty);
                    existingOrder.setTotalAmount(amount);
                    existingOrder.setOrderStatus(status);
                    dbHelper.updateOrder(existingOrder);
                    Toast.makeText(getContext(), "Order updated successfully", Toast.LENGTH_SHORT).show();
                }
                loadOrders();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
