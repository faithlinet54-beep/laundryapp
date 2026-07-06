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

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_customers, container, false);

        recyclerView = view.findViewById(R.id.rvCustomers);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout.setOnRefreshListener(this::fetchUsers);

        fetchUsers();

        return view;
    }

    private void fetchUsers() {
        swipeRefreshLayout.setRefreshing(true);
        RetrofitClient.getApiService().getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new UserAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Customers Updated!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to fetch customers", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
