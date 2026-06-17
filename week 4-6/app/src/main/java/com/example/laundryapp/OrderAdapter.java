package com.example.laundryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onDeleteClick(Order order);
    }

    public OrderAdapter(List<Order> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tvOrderId.setText("#" + order.getId());
        holder.tvCustomerName.setText(order.getCustomerName());
        holder.tvServiceType.setText(order.getServiceType());
        holder.tvQuantity.setText("Qty: " + order.getQuantity());
        holder.tvAmount.setText("Sh. " + (int)order.getTotalAmount());
        holder.tvStatusDescription.setText(order.getOrderStatus());
        
        // Load specific icons based on status
        String iconUrl;
        int statusColor;
        
        if ("Ready for Pickup".equalsIgnoreCase(order.getOrderStatus())) {
            iconUrl = "https://cdn-icons-png.flaticon.com/512/190/190411.png"; 
            statusColor = 0xFF7C3AED; // Purple
        } else if ("Completed".equalsIgnoreCase(order.getOrderStatus())) {
            iconUrl = "https://cdn-icons-png.flaticon.com/512/1008/1008010.png";
            statusColor = 0xFF16A34A; // Green
        } else if ("Washing".equalsIgnoreCase(order.getOrderStatus())) {
            iconUrl = "https://cdn-icons-png.flaticon.com/512/3003/3003984.png";
            statusColor = 0xFF2563EB; // Blue
        } else {
            iconUrl = "https://cdn-icons-png.flaticon.com/512/109/109613.png";
            statusColor = 0xFFEA580C; // Orange
        }

        holder.tvStatusDescription.setTextColor(statusColor);

        Glide.with(holder.itemView.getContext())
                .load(iconUrl)
                .into(holder.ivStatusIcon);

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvServiceType, tvQuantity, tvAmount, tvStatusDescription;
        ImageView ivStatusIcon;
        ImageButton btnDelete;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvServiceType = itemView.findViewById(R.id.tvServiceType);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatusDescription = itemView.findViewById(R.id.tvStatusDescription);
            ivStatusIcon = itemView.findViewById(R.id.ivStatusIcon);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
