package FisioTracker.Android.OrtoTracker_View.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import FisioTracker.Android.OrtoTracker_View.Activities.activity_02_dashboard;
import FisioTracker.Android.OrtoTracker_Model.Entitys.Entity_01_Account;
import FisioTracker.Android.OrtoTracker_View.Dialogs.Dialog_02_Login_Credentials;
import FisioTracker.Android.R;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import leakcanary.AppWatcher;

public class fragment_02_register extends Fragment {
    private LottieAnimationView lottieView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference minhaColecao = db.collection("Sessão");

        View view = inflater.inflate(R.layout.fragment_02_register, container, false);
        BlurView blurView = view.findViewById(R.id.glass);
        ViewGroup rootView = requireActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        Button signupButton = view.findViewById(R.id.signup);
        Button googleSignupButton = view.findViewById(R.id.google_signup);
        Button githubSignupButton = view.findViewById(R.id.github_signup);
        EditText userNameTextfield = view.findViewById(R.id.user_name_textfield);
        EditText userEmailTextfield = view.findViewById(R.id.user_email_textfield);
        EditText userPasswordTextfield = view.findViewById(R.id.user_password_textfield);
        EditText userCPFTextfield = view.findViewById(R.id.user_CPF_textfield);
        EditText userRegistroPRTextfield = view.findViewById(R.id.registro_PR_textfield);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Switch termosDePrivacidade = view.findViewById(R.id.termos_e_condições);
        lottieView = view.findViewById(R.id.lottie_loading_circle);

        blurView.setupWith(rootView, new RenderScriptBlur(requireActivity()))
                .setFrameClearDrawable(requireActivity().getWindow().getDecorView().getBackground())
                .setBlurRadius(24f);

        Entity_01_Account account = new Entity_01_Account();

        signupButton.setOnClickListener(v -> {

            Dialog_02_Login_Credentials dialog02LoginCredentials = new Dialog_02_Login_Credentials();

            String userName = userNameTextfield.getText().toString();
            String userEmail = userEmailTextfield.getText().toString();
            String userPassword = userPasswordTextfield.getText().toString();
            String userCPF = userCPFTextfield.getText().toString();
            String userRegistroPR = userRegistroPRTextfield.getText().toString();

            account.setUserName(userName);
            account.setUserPassword(userPassword);
            account.setUserEmail(userEmail);
            account.setUserCPF(userCPF);

            if (termosDePrivacidade.isActivated() && account.enabled()) {
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

                lottieView.setVisibility(View.VISIBLE);
                signupButton.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(getActivity(), activity_02_dashboard.class);
                startActivity(intent);
            } else {
                dialog02LoginCredentials.showDialog(getActivity());
            }
        });
        return view;
        }


        @Override
        public void onDestroyView () {
            super.onDestroyView();
            AppWatcher.INSTANCE.getObjectWatcher().watch(
                    getView(),
                    "Fragment view should be garbage collected"
            );
        }

        @Override
        public void onDestroy () {
            super.onDestroy();
            AppWatcher.INSTANCE.getObjectWatcher().watch(
                    this,
                    "Fragment instance should be garbage collected"
            );
        }
    }
