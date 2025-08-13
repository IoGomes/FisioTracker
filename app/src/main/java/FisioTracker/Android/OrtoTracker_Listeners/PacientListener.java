package FisioTracker.Android.OrtoTracker_Listeners;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import FisioTracker.Android.R;

public class PacientListener {

    private ImageButton button;

    public PacientListener(View rootView) {
        button = rootView.findViewById(R.id.button_pacient_1);

        button.setOnClickListener(v -> {

        });
    }
}
