package com.example.fisiotracker.fisio_tracker_activities;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.Display;
import android.view.WindowManager;
import android.os.Build;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fisiotracker.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;

public class activity_01_auth extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;

    private final Fragment fragment01 = new Fragment();
    private final Fragment fragment02 = new Fragment();

    Fragment fragmentAtual, proximoFragment;

    Button btnAlternar;

    private boolean isFragment01Visible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Fragment fragment01 = new fragment_01_login();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.motionLayout, fragment01)
                .commit();

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

        setContentView(R.layout.activity_01_auth);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        TextView textView = findViewById(R.id.signup);
        textView.setText(Html.fromHtml("<u>SignUp!</u>"));

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        window.setStatusBarColor(Color.TRANSPARENT);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.motionLayout, fragment01)
                    .commit();
        }

        btnAlternar = findViewById(R.id.signup);
        btnAlternar.setOnClickListener(v -> alternarFragment());
    }


    private void alternarFragment() {
        String textoProximoBotao;
        Fragment proximoFragment;

        if (isFragment01Visible) {
            proximoFragment = new fragment_02_register(); // Sempre novo
            textoProximoBotao = "SignIn";
            TextView textView = findViewById(R.id.signup);
            textView.setText(Html.fromHtml("<u>SignIn!</u>"));
            isFragment01Visible = false;
        } else {
            proximoFragment = new fragment_01_login(); // Sempre novo
            TextView textView = findViewById(R.id.signup);
            textView.setText(Html.fromHtml("<u>SignUp!</u>"));
            textoProximoBotao = "SignUp";
            isFragment01Visible = true;
        }

        btnAlternar.setText(textoProximoBotao);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.motionLayout, proximoFragment)
                .commit();
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFragment01Visible", isFragment01Visible);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        isFragment01Visible = savedInstanceState.getBoolean("isFragment01Visible", true);
        btnAlternar.setText(isFragment01Visible ? "SingUp" : "SignIn");

        Fragment fragmentParaMostrar = isFragment01Visible ? fragment01 : fragment02;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.motionLayout, fragmentParaMostrar)
                .commit();
    }


    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("GoogleAuth", "Google sign in failed", e);
                findViewById(R.id.lottie_loading_circle).setVisibility(View.INVISIBLE);
                findViewById(R.id.login).setVisibility(View.VISIBLE);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(activity_01_auth.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(activity_01_auth.this, activity_03_dashboard.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Log.w("FirebaseAuth", "signInWithCredential:failure", task.getException());
                            Toast.makeText(activity_01_auth.this, "Falha na autenticação", Toast.LENGTH_SHORT).show();
                            findViewById(R.id.lottie_loading_circle).setVisibility(View.INVISIBLE);
                            findViewById(R.id.login).setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (getWindow() != null && getWindow().getDecorView() != null) {
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(null);
        }

        if (googleSignInClient != null) {
            googleSignInClient = null;
        }
    }

}