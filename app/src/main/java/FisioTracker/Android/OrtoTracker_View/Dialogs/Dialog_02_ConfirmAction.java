package FisioTracker.Android.OrtoTracker_View.Dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

import FisioTracker.Android.OrtoTracker_Model.Entitys.Entity_01_Account;
import FisioTracker.Android.OrtoTracker_Model.Remote.CurrencyService;
import FisioTracker.Android.OrtoTracker_Model.Remote.RetrofitClient;
import FisioTracker.Android.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("all")
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

        // Botão para fechar
        dialogView.findViewById(R.id.dismiss_button).setOnClickListener(v -> dialog.dismiss());

        // ListView do diálogo
        ListView listView = dialogView.findViewById(R.id.user_listview);

        LottieAnimationView lottieAnimationView = dialogView.findViewById(R.id.raw_loading);

// Show the animation while loading
        lottieAnimationView.setVisibility(View.VISIBLE);

        CurrencyService service = RetrofitClient.getApiService();
        service.groupList().enqueue(new Callback<List<Entity_01_Account>>() {
            @Override
            public void onResponse(Call<List<Entity_01_Account>> call, Response<List<Entity_01_Account>> response) {
                lottieAnimationView.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Entity_01_Account> users = response.body();
                    List<String> userNames = new ArrayList<>();
                    for (Entity_01_Account user : users) {
                        userNames.add(user.getName() + " - " + user.getEmail());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            context,
                            android.R.layout.simple_list_item_1,
                            userNames
                    );
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(context, "Erro ao carregar usuários", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Entity_01_Account>> call, Throwable t) {
                // Hide animation if request fails
                lottieAnimationView.setVisibility(View.GONE);
                Toast.makeText(context, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }}
