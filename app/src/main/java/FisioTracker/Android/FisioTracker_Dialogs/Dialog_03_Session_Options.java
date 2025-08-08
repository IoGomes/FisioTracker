package FisioTracker.Android.FisioTracker_Dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import FisioTracker.Android.R;

public class Dialog_03_Session_Options {

    public static void showDialog_03(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_03_add_pacient, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    ContextCompat.getDrawable(context, R.drawable.list_dialog_background)
            );
        }
        dialog.show();

        dialogView.findViewById(R.id.addpaciente).setOnClickListener(v -> {
            Dialog_04_Select_Pacient.showCustomDialog(context);
            dialog.dismiss();
        });
    }
}
