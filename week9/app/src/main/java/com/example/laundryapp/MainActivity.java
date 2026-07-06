package com.example.laundryapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private BottomNavigationView bottomNavigationView;

    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    capturePhoto();
                } else {
                    Toast.makeText(this, getString(R.string.msg_camera_denied), Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<String> requestLocationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getLastLocation();
                } else {
                    Toast.makeText(this, getString(R.string.msg_location_denied), Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<Void> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components for Device Features
        Button btnCapturePhoto = findViewById(R.id.btnCapturePhoto);
        Button btnGetLocation = findViewById(R.id.btnGetLocation);
        imageView = findViewById(R.id.imageView);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);

        // Initialize UI components for Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnCapturePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                capturePhoto();
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        btnGetLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        });

        setupNavigation(savedInstanceState);
    }

    private void setupNavigation(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("LaundryPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            bottomNavigationView.setVisibility(View.VISIBLE);
            if (savedInstanceState == null) {
                loadFragment(new DashboardFragment());
            }
        } else {
            bottomNavigationView.setVisibility(View.GONE);
            if (savedInstanceState == null) {
                loadFragment(new LoginFragment());
            }
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (itemId == R.id.nav_orders) {
                selectedFragment = new OrdersFragment();
            } else if (itemId == R.id.nav_reports) {
                selectedFragment = new ReportsFragment();
            } else if (itemId == R.id.nav_customers) {
                selectedFragment = new CustomersFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    public void revealMainApplicationFlow() {
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
        loadFragment(new DashboardFragment());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void capturePhoto() {
        takePictureLauncher.launch(null);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        tvLatitude.setText(getString(R.string.label_latitude, String.valueOf(location.getLatitude())));
                        tvLongitude.setText(getString(R.string.label_longitude, String.valueOf(location.getLongitude())));
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.msg_location_null), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this, getString(R.string.msg_location_error, e.getMessage()), Toast.LENGTH_SHORT).show());
    }
}
