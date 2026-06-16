package com.example.laundryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.tvStatusDescription.setText(order.getOrderStatus());
        
        // Show context info (e.g. time left or pickup time)
        holder.tvTimeContext.setText("Processing..."); // Placeholder

        // Load specific Figma-style icons based on status
        String iconUrl;
        if ("ready".equalsIgnoreCase(order.getOrderStatus())) {
            iconUrl = "https://cdn-icons-png.flaticon.com/512/190/190411.png"; // Green check/bag
            holder.tvStatusDescription.setTextColor(0xFF16A34A);
        } else if ("completed".equalsIgnoreCase(order.getOrderStatus())) {
            iconUrl = "https://cdn-icons-png.flaticon.com/512/1008/1008010.png"; // Grey check
            holder.tvStatusDescription.setTextColor(0xFF9CA3AF);
        } else {
            iconUrl = "https://cdn-icons-png.flaticon.com/512/3003/3003984.png"; // Orange machine
            holder.tvStatusDescription.setTextColor(0xFFEA580C);
        }

        Glide.with(holder.itemView.getContext())
                .load(iconUrl)
                .into(holder.ivStatusIcon);

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvStatusDescription, tvTimeContext;
        ImageView ivStatusIcon;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvStatusDescription = itemView.findViewById(R.id.tvStatusDescription);
            tvTimeContext = itemView.findViewById(R.id.tvTimeContext);
            ivStatusIcon = itemView.findViewById(R.id.ivStatusIcon);
        }
    }
}
