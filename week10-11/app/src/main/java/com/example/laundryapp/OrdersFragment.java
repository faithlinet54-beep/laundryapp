package com.example.laundryapp;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

/**
 * OrdersFragment manages the display and CRUD operations for laundry orders.
 * Enhanced with Search and Notifications.
 */
public class OrdersFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orders;
    private DatabaseHelper dbHelper;
    private TextView tvTotalCount, tvActiveCount, tvReadyCount;
    private TextInputEditText etSearch;
    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_orders, container, false);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.rvOrders);
        tvTotalCount = view.findViewById(R.id.tvTotalCount);
        tvActiveCount = view.findViewById(R.id.tvActiveCount);
        tvReadyCount = view.findViewById(R.id.tvReadyCount);
        etSearch = view.findViewById(R.id.etSearch);
        
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

        setupSearch();
        checkNotificationPermission();

        return view;
    }

    private void setupSearch() {
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (adapter != null) {
                        adapter.getFilter().filter(s);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
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
                    Toast.makeText(getContext(), "Order deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showAddOrderDialog() {
        if (getContext() == null) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_order, null);
        setupOrderDialog(dialogView, null);
    }

    private void showUpdateOrderDialog(Order order) {
        if (getContext() == null) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_order, null);
        setupOrderDialog(dialogView, order);
    }

    private void setupOrderDialog(View dialogView, @Nullable Order orderToUpdate) {
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextInputLayout tilName = dialogView.findViewById(R.id.tilCustomerName);
        TextInputLayout tilPhone = dialogView.findViewById(R.id.tilPhoneNumber);
        TextInputLayout tilService = dialogView.findViewById(R.id.tilServiceType);
        TextInputLayout tilQty = dialogView.findViewById(R.id.tilQuantity);
        TextInputLayout tilAmount = dialogView.findViewById(R.id.tilAmount);

        TextInputEditText etName = dialogView.findViewById(R.id.etCustomerName);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etPhoneNumber);
        TextInputEditText etService = dialogView.findViewById(R.id.etServiceType);
        TextInputEditText etQty = dialogView.findViewById(R.id.etQuantity);
        TextInputEditText etAmount = dialogView.findViewById(R.id.etAmount);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);
        Button btnSave = dialogView.findViewById(R.id.btnSaveOrder);

        if (orderToUpdate != null) {
            if (tvTitle != null) tvTitle.setText(getString(R.string.update_order));
            etName.setText(orderToUpdate.getCustomerName());
            etPhone.setText(orderToUpdate.getPhoneNumber());
            etService.setText(orderToUpdate.getServiceType());
            etQty.setText(String.valueOf(orderToUpdate.getQuantity()));
            etAmount.setText(String.valueOf(orderToUpdate.getTotalAmount()));
            btnSave.setText("Update");

            if (spinnerStatus != null && spinnerStatus.getAdapter() != null) {
                @SuppressWarnings("unchecked")
                ArrayAdapter<CharSequence> arrayAdapter = (ArrayAdapter<CharSequence>) spinnerStatus.getAdapter();
                int position = arrayAdapter.getPosition(orderToUpdate.getOrderStatus());
                spinnerStatus.setSelection(position);
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
            String service = etService.getText() != null ? etService.getText().toString().trim() : "";
            String qtyStr = etQty.getText() != null ? etQty.getText().toString().trim() : "";
            String amountStr = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";
            String status = spinnerStatus.getSelectedItem() != null ? spinnerStatus.getSelectedItem().toString() : "Washing";

            boolean isValid = true;
            if (!InputValidator.isNotEmpty(name)) {
                tilName.setError("Name is required");
                isValid = false;
            } else tilName.setError(null);

            if (!InputValidator.isValidPhone(phone)) {
                tilPhone.setError("Invalid phone number (min 10 digits)");
                isValid = false;
            } else tilPhone.setError(null);

            if (!InputValidator.isNotEmpty(service)) {
                tilService.setError("Service type is required");
                isValid = false;
            } else tilService.setError(null);

            if (!InputValidator.isPositive(qtyStr)) {
                tilQty.setError("Quantity must be positive");
                isValid = false;
            } else tilQty.setError(null);

            if (!InputValidator.isPositive(amountStr)) {
                tilAmount.setError("Amount must be positive");
                isValid = false;
            } else tilAmount.setError(null);

            if (isValid) {
                int qty = Integer.parseInt(qtyStr);
                double amount = Double.parseDouble(amountStr);

                if (orderToUpdate == null) {
                    Order newOrder = new Order(0, name, phone, service, qty, amount, status);
                    dbHelper.addOrder(newOrder);
                    Toast.makeText(getContext(), "Order added successfully", Toast.LENGTH_SHORT).show();
                    NotificationHelper.showOrderNotification(requireContext(), "Order Added", "Laundry Order for " + name + " Saved Successfully.");
                } else {
                    orderToUpdate.setCustomerName(name);
                    orderToUpdate.setPhoneNumber(phone);
                    orderToUpdate.setServiceType(service);
                    orderToUpdate.setQuantity(qty);
                    orderToUpdate.setTotalAmount(amount);
                    orderToUpdate.setOrderStatus(status);
                    dbHelper.updateOrder(orderToUpdate);
                    Toast.makeText(getContext(), "Order updated successfully", Toast.LENGTH_SHORT).show();
                    NotificationHelper.showOrderNotification(requireContext(), "Order Updated", "Laundry Order for " + name + " Updated Successfully.");
                }
                loadOrders();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
