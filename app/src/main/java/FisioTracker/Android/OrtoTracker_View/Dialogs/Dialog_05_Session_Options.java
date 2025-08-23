package FisioTracker.Android.OrtoTracker_View.Dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import FisioTracker.Android.R;

public class Dialog_05_Session_Options implements Dialog_00_Interface {

    @Override
    public void showDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_05_new_session, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    ContextCompat.getDrawable(context, R.drawable.list_dialog_background)
            );
        }

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(window.getAttributes());
            params.width = 900;
            window.setAttributes(params);
        }

        dialogView.findViewById(R.id.addpaciente).setOnClickListener(v -> {
            Dialog_06_Select_Pacient dialog06SelectPacient = new Dialog_06_Select_Pacient();
            dialog06SelectPacient.showDialog(context);
            dialog.dismiss();
        });

        dialog.show();

    }
}
