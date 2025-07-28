package com.example.fisiotracker.ft_activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.fisiotracker.R;
import com.example.fisiotracker.ft_services.service_00_cache_loader;

public class activity_00_splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        service_00_cache_loader.preload(this, R.raw.lottie_01_button_animated_gradient);
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Display.Mode[] modes = getDisplay().getSupportedModes();
            Display.Mode highestMode = modes[0];

            for (Display.Mode mode : modes) {
                if (mode.getRefreshRate() > highestMode.getRefreshRate()) {
                    highestMode = mode;
                }
            }

            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.preferredDisplayModeId = highestMode.getModeId();
            getWindow().setAttributes(layoutParams);
        }

        setContentView(R.layout.activity_00_splash);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.setStatusBarColor(Color.TRANSPARENT);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.setAnimation(R.raw.lottie_00_splash_heartbeat);
        animationView.setSpeed(3f);
        animationView.playAnimation();

        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(activity_00_splash.this, activity_01_auth.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }
}