package FisioTracker.Android.OrtoTracker_Dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import eightbitlab.com.blurview.BlurView;

import FisioTracker.Android.R;

public class Dialog_06_Session_Options {

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


        setupBlurView(context, dialogView);

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(window.getAttributes());
            params.width = 900;
            window.setAttributes(params);
        }

        dialogView.findViewById(R.id.addpaciente).setOnClickListener(v -> {
            Dialog_05_Select_Pacient.showCustomDialog(context);
            dialog.dismiss();
        });
    }

    private static void setupBlurView(Context context, View dialogView) {

        BlurView blurView = dialogView.findViewById(R.id.dialogglass);
        if (blurView == null) {
            return;
        }

        ViewGroup rootView = (ViewGroup) ((View) dialogView.getParent()).getRootView();

        Drawable windowBackground = dialogView.getBackground();
        if (windowBackground == null && context instanceof android.app.Activity) {
            windowBackground = ((android.app.Activity) context).getWindow().getDecorView().getBackground();
        }

        blurView.setupWith(rootView).setFrameClearDrawable(windowBackground).setBlurRadius(24f);
    }


}
