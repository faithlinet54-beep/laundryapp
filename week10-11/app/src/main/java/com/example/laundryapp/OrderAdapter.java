package com.example.laundryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> implements Filterable {

    private final List<Order> orderList;
    private List<Order> orderListFull;
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onDeleteClick(Order order);
    }

    public OrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.orderListFull = new ArrayList<>(orderList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderId.setText(holder.itemView.getContext().getString(R.string.order_id_format, order.getId()));
        holder.tvCustomerName.setText(order.getCustomerName());
        holder.tvServiceType.setText(order.getServiceType());
        holder.tvQuantity.setText(holder.itemView.getContext().getString(R.string.order_quantity_format, order.getQuantity()));
        holder.tvStatusDescription.setText(order.getOrderStatus());
        holder.tvAmount.setText(holder.itemView.getContext().getString(R.string.order_amount_format, order.getTotalAmount()));

        String iconUrl = "https://cdn-icons-png.flaticon.com/512/3502/3502688.png";
        if ("Ready for Pickup".equalsIgnoreCase(order.getOrderStatus())) {
            iconUrl = "https://cdn-icons-png.flaticon.com/512/190/190411.png";
        } else if ("Completed".equalsIgnoreCase(order.getOrderStatus())) {
            iconUrl = "https://cdn-icons-png.flaticon.com/512/1160/1160515.png";
        }

        Glide.with(holder.itemView.getContext())
                .load(iconUrl)
                .into(holder.ivStatusIcon);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    @Override
    public Filter getFilter() {
        return orderFilter;
    }

    private final Filter orderFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Order> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(orderListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Order item : orderListFull) {
                    if (item.getCustomerName().toLowerCase().contains(filterPattern) ||
                        item.getPhoneNumber().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            orderList.clear();
            orderList.addAll((List<Order>) results.values);
            notifyDataSetChanged();
        }
    };

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvServiceType, tvQuantity, tvStatusDescription, tvAmount;
        ImageView ivStatusIcon;
        ImageButton btnDelete;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvServiceType = itemView.findViewById(R.id.tvServiceType);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvStatusDescription = itemView.findViewById(R.id.tvStatusDescription);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            ivStatusIcon = itemView.findViewById(R.id.ivStatusIcon);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
