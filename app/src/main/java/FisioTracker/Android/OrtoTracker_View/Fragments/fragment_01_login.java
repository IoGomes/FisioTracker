package FisioTracker.Android.OrtoTracker_View.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import FisioTracker.Android.OrtoTracker_Model.Services.service_06_dateTimeInfo;
import FisioTracker.Android.OrtoTracker_View.Activities.activity_02_dashboard;
import FisioTracker.Android.OrtoTracker_Model.Entitys.Entity_01_Account;
import FisioTracker.Android.OrtoTracker_View.Dialogs.Dialog_02_Login_Credentials;
import FisioTracker.Android.R;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import java.util.HashMap;
import java.util.Map;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class fragment_01_login extends Fragment {

    private Button buttonGoogleLogin;
    private LottieAnimationView lottieView;
    private Button loginButton;
    private BlurView blurView;
    private EditText emailTextField;
    private EditText passwordTextField;
    private TextView textView2;

    Entity_01_Account account = new Entity_01_Account();

    service_06_dateTimeInfo dateTimeInfo = new service_06_dateTimeInfo();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference minhaColecao = db.collection("Sessão");

        View view = inflater.inflate(R.layout.fragment_01_login, container, false);
        buttonGoogleLogin = view.findViewById(R.id.google);
        lottieView = view.findViewById(R.id.lottie_loading_circle);
        loginButton = view.findViewById(R.id.login_button);
        blurView = view.findViewById(R.id.glass);
        emailTextField = view.findViewById(R.id.email_textfield);
        passwordTextField = view.findViewById(R.id.password_textfield);
        textView2 = view.findViewById(R.id.textView2);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        ViewGroup rootView = (ViewGroup) requireActivity().getWindow().getDecorView().findViewById(android.R.id.content);

        blurView.setupWith(rootView, new RenderScriptBlur(requireActivity()))
                .setFrameClearDrawable(requireActivity().getWindow().getDecorView().getBackground())
                .setBlurRadius(24f);

        String text = "Esqueceu a senha!";
        SpannableString spannableString = new SpannableString(text);

        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            spannableString.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView2.setText(spannableString);

        buttonGoogleLogin.setOnClickListener(v -> {
            Dialog_02_Login_Credentials dialog02LoginCredentials = new Dialog_02_Login_Credentials();

            String userEmail = emailTextField.getText().toString();
            String userPassword = passwordTextField.getText().toString();
            account.setUserEmail(userEmail);
            account.setUserPassword(userPassword);
            if (account.enabled()) {
                Map<String, Object> dados = new HashMap<>();
                dados.put("minhaString", "Olá Firestore!");

                minhaColecao.add(dados)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("Firestore", "Documento adicionado com ID: " + documentReference.getId());
                        })
                        .addOnFailureListener(e -> {
                            Log.w("Firestore", "Erro ao adicionar documento", e);
                        });
                database.child("usuario_email_input").setValue(userEmail);
                database.child("usuario_password_input").setValue(userPassword);
                /*Habilita a visualização da Animação lottie*/
                dateTimeInfo.printDateTime();
                lottieView.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);
                /*Inicia a visualização da proxima Activity*/
                Intent intent = new Intent(getActivity(), activity_02_dashboard.class);
                startActivity(intent);
            } else {
                dialog02LoginCredentials.showDialog(getActivity());
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (buttonGoogleLogin != null) {
            buttonGoogleLogin.setOnClickListener(null);
        }

        if (lottieView != null) {
            lottieView.cancelAnimation();
            lottieView.setVisibility(View.GONE);
        }

        lottieView = null;
        loginButton = null;
        buttonGoogleLogin = null;
        blurView = null;
        emailTextField = null;
        passwordTextField = null;
        textView2 = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        account = null;
    }
}




