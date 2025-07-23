package com.example.fisiotracker.fisio_tracker_activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fisiotracker.R;

import eightbitlab.com.blurview.BlurView;

public class activity_02_register extends AppCompatActivity {

    BlurView blurView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_02_register);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        window.setStatusBarColor(Color.TRANSPARENT);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        blurView = findViewById(R.id.glass);
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        Drawable windowBackground = getWindow().getDecorView().getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(12f);

        Button signUpButton = findViewById(R.id.loginscreen);
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity_02_register.this, activity_01_auth.class);
            startActivity(intent);
        });
}}
