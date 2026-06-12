package com.example.laundryapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Set default view to Login Fragment
        if (savedInstanceState == null) {
            loadFragment(new LoginFragment());
        }

        // Handle navigation selection clicks
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_login) {
                loadFragment(new LoginFragment());
                return true;
            } else if (itemId == R.id.nav_dashboard) {
                loadFragment(new DashboardFragment());
                return true;
            } else if (itemId == R.id.nav_orders) {
                loadFragment(new OrdersFragment());
                return true;
            } else if (itemId == R.id.nav_details) {
                loadFragment(new OrdersDetailsFragment());
                return true;
            } else if (itemId == R.id.nav_earnings) {
                loadFragment(new EarningsFragment());
                return true;
            }
            return false;
        });
    }

    // Helper method to load the chosen fragment into the container
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
