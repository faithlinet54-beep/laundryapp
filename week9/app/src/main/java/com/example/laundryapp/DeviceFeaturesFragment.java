package com.example.laundryapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class DeviceFeaturesFragment extends Fragment {

    private ImageView imageView;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    capturePhoto();
                } else {
                    Toast.makeText(getContext(), getString(R.string.msg_camera_denied), Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<String> requestLocationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getLastLocation();
                } else {
                    Toast.makeText(getContext(), getString(R.string.msg_location_denied), Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<Void> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_features, container, false);

        Button btnCapturePhoto = view.findViewById(R.id.btnCapturePhoto);
        Button btnGetLocation = view.findViewById(R.id.btnGetLocation);
        imageView = view.findViewById(R.id.imageView);
        tvLatitude = view.findViewById(R.id.tvLatitude);
        tvLongitude = view.findViewById(R.id.tvLongitude);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        btnCapturePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                capturePhoto();
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        btnGetLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        });

        return view;
    }

    private void capturePhoto() {
        takePictureLauncher.launch(null);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        tvLatitude.setText(getString(R.string.label_latitude, String.valueOf(location.getLatitude())));
                        tvLongitude.setText(getString(R.string.label_longitude, String.valueOf(location.getLongitude())));
                    } else {
                        Toast.makeText(getContext(), getString(R.string.msg_location_null), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(requireActivity(), e -> 
                        Toast.makeText(getContext(), getString(R.string.msg_location_error, e.getMessage()), Toast.LENGTH_SHORT).show());
    }
}
