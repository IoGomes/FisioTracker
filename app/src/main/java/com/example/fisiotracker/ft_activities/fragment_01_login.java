package com.example.fisiotracker.ft_activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.airbnb.lottie.LottieAnimationView;
import com.example.fisiotracker.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
public class fragment_01_login extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /*Instancias necessárias para o Fragment*/
        View view = inflater.inflate(R.layout.fragment_01_login, container, false);
        LottieAnimationView lottieView = view.findViewById(R.id.lottie_loading_circle);
        Button loginButton = view.findViewById(R.id.login);
        Button buttonGoogleLogin = view.findViewById(R.id.google);
        BlurView blurView = view.findViewById(R.id.glass);
        EditText emailTextField = view.findViewById(R.id.email_textfield);
        EditText passwordTextField = view.findViewById(R.id.password_textfield);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        ViewGroup rootView = (ViewGroup) requireActivity().getWindow().getDecorView().findViewById(android.R.id.content);

        /*Aplicando o efeito Blur ao container*/
        blurView.setupWith(rootView, new RenderScriptBlur(requireActivity()))
                .setFrameClearDrawable(requireActivity().getWindow().getDecorView().getBackground())
                .setBlurRadius(24f);

        buttonGoogleLogin.setOnClickListener(v -> {
            /*Captura o input text do EditText email_textfield e envia para a firebase database*/
            String email = emailTextField.getText().toString();
            String password = passwordTextField.getText().toString();
            database.child("usuario_email_input").setValue(email);
            database.child("usuario_password_input").setValue(password);
            /*Habilita a visualização da Animação lottie*/
            lottieView.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            /*Inicia a visualização da proxima Activity*/
            Intent intent = new Intent(getActivity(), activity_02_dashboard.class);
            startActivity(intent);
            onDestroyView();
            onDestroy();
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}