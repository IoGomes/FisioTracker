package FisioTracker.Android.FisioTracker_Dialogs;
import FisioTracker.Android.FisioTracker_Activities.activity_02_dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import FisioTracker.Android.R;

public class Dialog_04_Select_Pacient {
    public static void showCustomDialog(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_04_new_pacient, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    ContextCompat.getDrawable(context, R.drawable.list_dialog_background)
            );
        }
        dialog.show();

        dialogView.findViewById(R.id.luisa).setOnClickListener(v -> {
            if (context instanceof activity_02_dashboard ) {
                ((activity_02_dashboard) context).mostrarBotao();
                ((activity_02_dashboard) context).mostrarContainer();

            }
            dialog.dismiss();
        });
    }
}
