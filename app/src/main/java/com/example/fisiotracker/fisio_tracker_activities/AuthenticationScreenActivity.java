package com.example.fisiotracker.fisio_tracker_activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fisiotracker.R;

import eightbitlab.com.blurview.BlurView;

public class AuthenticationScreenActivity extends AppCompatActivity {
    BlurView blurView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_screen);

        TextView textView = findViewById(R.id.textView);
        textView.setText(Html.fromHtml("<u>Not registered yet? SignUp</u>"));

        TextView textView2 = findViewById(R.id.textView2);
        textView2.setText(Html.fromHtml("<u>Esqueceu a senha?</u>"));

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

        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(AuthenticationScreenActivity.this, DashboardScreenActivity.class);
            startActivity(intent);
        });
    }
}