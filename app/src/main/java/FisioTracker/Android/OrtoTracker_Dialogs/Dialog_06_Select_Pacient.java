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

import FisioTracker.Android.OrtoTracker_Activities.activity_02_dashboard;
import FisioTracker.Android.R;

public class Dialog_06_Select_Pacient {

    private static final String[] nomes = {
            "João", "Maria", "Pedro", "Ana", "Lucas",
            "Fernanda", "Carlos", "Juliana", "Marcos", "Patrícia"
    };

    private static final boolean[] selecionados = new boolean[nomes.length];

    public static void showCustomDialog(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_04_pacient_list, null);

        ListView listView = dialogView.findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_multiple_choice,
                nomes
        );
        listView.setAdapter(adapter);

        for (int i = 0; i < nomes.length; i++) {
            listView.setItemChecked(i, selecionados[i]);
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selecionados[position] = listView.isItemChecked(position);
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    ContextCompat.getDrawable(context, R.drawable.list_dialog_background)
            );
        }

        dialogView.findViewById(R.id.conclude_add_pacient).setOnClickListener(v -> {
            if (context instanceof activity_02_dashboard) {
                ((activity_02_dashboard) context).enableSimultaneousPacientVisibility();
            }
            dialog.dismiss();
        });

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(window.getAttributes());
            params.width = 900;
            params.height = 1100;
            window.setAttributes(params);
        }
    }
}
