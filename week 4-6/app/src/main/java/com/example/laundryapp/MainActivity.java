package com.example.laundryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 9. SharedPreferences - Check login status
        SharedPreferences prefs = getSharedPreferences("LaundryPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            revealMainApplicationFlow();
        } else {
            bottomNavigationView.setVisibility(View.GONE);
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .commit();
            }
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (itemId == R.id.nav_orders) {
                selectedFragment = new OrdersFragment();
            } else if (itemId == R.id.nav_reports) { // Changed from earnings
                selectedFragment = new ReportsFragment();
            } else if (itemId == R.id.nav_customers) {
                selectedFragment = new CustomersFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            return false;
        });
    }

    /**
     * Reveals the core app navigation bar and transitions to the Dashboard.
     */
    public void revealMainApplicationFlow() {
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
    }
}
