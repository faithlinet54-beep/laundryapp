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

        Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/3502/3502688.png").into(ivTotalIcon); 
        Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/109/109613.png").into(ivActiveIcon); 
        Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/190/190411.png").into(ivReadyIcon); 

        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshData();

        // 8. Touch Gestures using reusable GestureHandler class
        GestureHandler.attachToRecyclerView(getContext(), rvOrders, new GestureHandler.OnGestureListener() {
            @Override
            public void onSingleTap(int position) {
                Order order = allOrders.get(position);
                EventLogger.showSnackbar(view, "Selected order for: " + order.getCustomerName());
                showOrderDialog(order);
            }

            @Override
            public void onDoubleTap(int position) {
                Order order = allOrders.get(position);
                order.setFavorite(!order.isFavorite());
                adapter.notifyItemChanged(position);
                String status = order.isFavorite() ? "Marked as Favorite" : "Unmarked as Favorite";
                EventLogger.showToast(getContext(), status);
            }

            @Override
            public void onLongPress(int position) {
                Order order = allOrders.get(position);
                showContextMenu(order);
            }

            @Override
            public void onSwipeLeft(int position) {
                onDeleteClick(allOrders.get(position));
            }

            @Override
            public void onSwipeRight(int position) {
                Order order = allOrders.get(position);
                order.setOrderStatus("Completed");
                dbHelper.updateOrder(order);
                refreshData();
                EventLogger.showToast(getContext(), "Order marked as COMPLETED");
            }
        });

        FloatingActionButton fabAddOrder = view.findViewById(R.id.fabAddOrder);
        fabAddOrder.setOnClickListener(v -> showOrderDialog(null));

        // 7. Search Functionality + Keyboard Handling
        KeyboardHandler.setOnActionDoneListener(etSearch, () -> {
            String query = etSearch.getText().toString().trim();
            if (InputValidator.isNotEmpty(query)) {
                searchOrders(query);
            } else {
                etSearch.setError("Please enter search text");
            }
        });

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
            // Search by Customer Name or Phone Number (Implemented in DatabaseHelper)
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
            if ("Washing".equalsIgnoreCase(o.getOrderStatus())) active++;
            if ("Ready for Pickup".equalsIgnoreCase(o.getOrderStatus())) ready++;
        }
        tvActiveCount.setText(String.valueOf(active));
        tvReadyCount.setText(String.valueOf(ready));
    }

    // 2. Save Orders & 4. Update Orders
    private void showOrderDialog(@Nullable Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_order, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextInputEditText etName = dialogView.findViewById(R.id.etCustomerName);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etPhoneNumber);
        TextInputEditText etService = dialogView.findViewById(R.id.etServiceType);
        TextInputEditText etQty = dialogView.findViewById(R.id.etQuantity);
        TextInputEditText etAmount = dialogView.findViewById(R.id.etAmount);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);

        if (order != null) {
            tvTitle.setText("Update Order");
            etName.setText(order.getCustomerName());
            etPhone.setText(order.getPhoneNumber());
            etService.setText(order.getServiceType());
            etQty.setText(String.valueOf(order.getQuantity()));
            etAmount.setText(String.valueOf(order.getTotalAmount()));
            
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerStatus.getAdapter();
            int pos = adapter.getPosition(order.getOrderStatus());
            if (pos >= 0) spinnerStatus.setSelection(pos);
        }

        AlertDialog dialog = builder.create();

        // Keyboard Handling in Dialog
        KeyboardHandler.setOnActionDoneListener(etAmount, () -> {
            // Trigger save/update logic manually or click button
            // I'll keep it simple and just show a toast for now as per requirement 1
            EventLogger.logEvent("Done pressed in Add Order Dialog");
        });

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, order == null ? "Save" : "Update", (d, which) -> {
            // This listener will be overwritten to prevent auto-dismiss if validation fails
        });

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String service = etService.getText().toString().trim();
            String qtyStr = etQty.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem().toString();

            // Validation using InputValidator
            if (!InputValidator.isNotEmpty(name)) {
                etName.setError("Name required");
                return;
            }
            if (!InputValidator.isNotEmpty(phone)) {
                etPhone.setError("Phone required");
                return;
            }
            if (!InputValidator.isNotEmpty(qtyStr)) {
                etQty.setError("Quantity required");
                return;
            }
            if (!InputValidator.isNotEmpty(amountStr)) {
                etAmount.setError("Amount required");
                return;
            }

            int qty = Integer.parseInt(qtyStr);
            double amount = Double.parseDouble(amountStr);

            if (order == null) {
                dbHelper.addOrder(new Order(0, name, phone, service, qty, amount, status));
                EventLogger.showToast(getContext(), "Order Saved Successfully");
            } else {
                order.setCustomerName(name);
                order.setPhoneNumber(phone);
                order.setServiceType(service);
                order.setQuantity(qty);
                order.setTotalAmount(amount);
                order.setOrderStatus(status);
                dbHelper.updateOrder(order);
                EventLogger.showToast(getContext(), "Order Updated Successfully");
            }
            refreshData();
            dialog.dismiss();
        });
    }

    private void refreshData() {
        allOrders = dbHelper.getAllOrders();
        if (adapter == null) {
            adapter = new OrderAdapter(allOrders, this);
            rvOrders.setAdapter(adapter);
        } else {
            adapter.setOrders(allOrders);
        }
        updateMetrics();
    }

    @Override
    public void onOrderClick(Order order) {
        // Show update dialog on click
        showOrderDialog(order);
    }

    // 5. Delete Orders
    @Override
    public void onDeleteClick(Order order) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this order for " + order.getCustomerName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // DELETE: Remove from SQLite
                    dbHelper.deleteOrder(order.getId());
                    refreshData();
                    EventLogger.showToast(getContext(), "Order Deleted");
                })
                .setNegativeButton("Cancel", (dialog, which) -> refreshData()) // Reset swipe position
                .show();
    }

    private void showContextMenu(Order order) {
        String[] options = {"Edit", "Delete"};
        new AlertDialog.Builder(getContext())
                .setTitle("Order Options: " + order.getCustomerName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showOrderDialog(order);
                    } else if (which == 1) {
                        onDeleteClick(order);
                    }
                })
                .show();
    }
}
