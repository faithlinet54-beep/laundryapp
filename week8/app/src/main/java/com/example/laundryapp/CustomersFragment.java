package com.example.laundryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomersFragment extends Fragment {

    private RecyclerView rvCustomers;
    private SwipeRefreshLayout swipeRefresh;
    private UserAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_customers, container, false);

        rvCustomers = view.findViewById(R.id.rvCustomers);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        rvCustomers.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the Swipe to Refresh interaction
        swipeRefresh.setOnRefreshListener(() -> {
            // This code runs when the user pulls down to refresh
            fetchUsers();
        });

        // Optional: Customize the loading spinner colors
        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        // Fetch users on initial load
        fetchUsers();

        return view;
    }

    private void fetchUsers() {
        // Show the loading spinner programmatically
        swipeRefresh.setRefreshing(true);

        RetrofitClient.getApiService().getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!isAdded()) return; // Safety check

                // Hide the loading spinner immediately after response
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    adapter = new UserAdapter(response.body());
                    rvCustomers.setAdapter(adapter);
                    Toast.makeText(getContext(), "Customers Updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                if (!isAdded()) return;
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
