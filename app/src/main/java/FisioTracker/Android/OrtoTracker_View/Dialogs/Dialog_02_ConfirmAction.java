package FisioTracker.Android.OrtoTracker_View.Dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import FisioTracker.Android.OrtoTracker_View.Activities.activity_02_dashboard;
import FisioTracker.Android.R;

public class Dialog_02_ConfirmAction implements Dialog_00_Interface {

    @Override
    public void showDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_02_confirm_action, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    ContextCompat.getDrawable(context, R.drawable.list_dialog_background)
            );
        }

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(window.getAttributes());
            params.width = 900;
            window.setAttributes(params);
        }

        dialogView.findViewById(R.id.dismiss_button).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.cancel_button).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.addpaciente).setOnClickListener(v -> {
            activity_02_dashboard activity_02_dashboard = new activity_02_dashboard();
            if (!activity_02_dashboard.isDestroyed() && !activity_02_dashboard.isFinishing()) {
                activity_02_dashboard.disableSimultaneousPacientVisibility(activity_02_dashboard);
                activity_02_dashboard.disableNavBarOptions();
            }
            dialog.dismiss();
        });

    }
}
