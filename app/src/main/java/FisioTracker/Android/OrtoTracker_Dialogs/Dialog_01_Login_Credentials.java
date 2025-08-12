package FisioTracker.Android.OrtoTracker_Dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import FisioTracker.Android.R;

public class Dialog_01_Login_Credentials {
    private static List<String> credentialResult = new ArrayList<>();

    public static void collector(String message) {
        credentialResult.add(message);
    }
    public static void showDialog_07(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_06_login_credentials, null);

        ListView loginCredentialsInformation = dialogView.findViewById(R.id.credentialsResult);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                dialogView.getContext(),
                R.layout.list_credentials_layout,
                credentialResult
        );

        loginCredentialsInformation.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    ContextCompat.getDrawable(context, R.drawable.list_dialog_background)
            );
        }

        dialogView.findViewById(R.id.close_login_credentials_dialog).setOnClickListener(v -> {dialog.dismiss();});

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(window.getAttributes());
            params.width = 950;
            window.setAttributes(params);
        }
    }

}
