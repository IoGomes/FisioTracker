package FisioTracker.Android.FisioTracker_Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import FisioTracker.Android.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class fragment_02_register extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        /*Instancias necessárias para o Fragment*/
        View view = inflater.inflate(R.layout.fragment_02_register, container, false);
        BlurView blurView = view.findViewById(R.id.glass);
        ViewGroup rootView = (ViewGroup) requireActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        Button signupButton = view.findViewById(R.id.signup);
        Button googleSignupButton = view.findViewById(R.id.google_signup);
        Button githubSignupButton = view.findViewById(R.id.github_signup);
        EditText userNameTextfield = view.findViewById(R.id.user_name_textfield);
        EditText userEmailTextfield = view.findViewById(R.id.user_email_textfield);
        EditText userCPFTextfield = view.findViewById(R.id.user_CPF_textfield);
        EditText userRegistroPR = view.findViewById(R.id.registro_PR_textfield);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        blurView.setupWith(rootView, new RenderScriptBlur(requireActivity()))
                .setFrameClearDrawable(requireActivity().getWindow().getDecorView().getBackground())
                .setBlurRadius(24f);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
