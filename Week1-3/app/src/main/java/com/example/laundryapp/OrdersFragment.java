package com.example.laundryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private LinearLayout containerOrdersList;

    private static class OrderData {
        String id, customer, status, time;
        int color;
        OrderData(String id, String customer, String status, String time, int color) {
            this.id = id; this.customer = customer; this.status = status; this.time = time; this.color = color;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_orders, container, false);
        containerOrdersList = view.findViewById(R.id.containerOrdersList);

        List<OrderData> orders = new ArrayList<>();
        // Using colors from the Figma design (Orange for washing, Green for ready)
        orders.add(new OrderData("#1024", "John Doe", "washing", "12 min left", 0xFFEA580C));
        orders.add(new OrderData("#1023", "Mary Wanjiku", "ready for pickup", "Today, 5:00 pm", 0xFF16A34A));

        renderOrders(orders);
        return view;
    }

    private void renderOrders(List<OrderData> orders) {
        if (containerOrdersList == null) return;
        containerOrdersList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (OrderData order : orders) {
            View card = inflater.inflate(R.layout.item_order_card, containerOrdersList, false);
            
            ((TextView) card.findViewById(R.id.tvOrderId)).setText(order.id);
            ((TextView) card.findViewById(R.id.tvCustomerName)).setText(order.customer);
            ((TextView) card.findViewById(R.id.tvStatusDescription)).setText(order.status);
            ((TextView) card.findViewById(R.id.tvTimeContext)).setText(order.time);
            
            ImageView statusIcon = card.findViewById(R.id.ivStatusIcon);
            statusIcon.setColorFilter(order.color);

            card.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new OrdersDetailsFragment())
                            .addToBackStack(null)
                            .commit();
                }
            });

            containerOrdersList.addView(card);
        }
    }
}
