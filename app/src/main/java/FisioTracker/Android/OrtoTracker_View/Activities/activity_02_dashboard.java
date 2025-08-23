package FisioTracker.Android.OrtoTracker_View.Activities;

import static FisioTracker.Android.OrtoTracker_Model.Services.service_04_Bluetooth.initializeBluetoothAdapter;
import static FisioTracker.Android.OrtoTracker_Model.Services.service_04_Bluetooth.requestBluetoothPermissions;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.UUID;

import FisioTracker.Android.OrtoTracker_View.Dialogs.Dialog_02_ConfirmAction;
import FisioTracker.Android.OrtoTracker_View.Dialogs.Dialog_03_BLE_Devices;
import FisioTracker.Android.OrtoTracker_View.Dialogs.Dialog_04_Pacient_Form;
import FisioTracker.Android.OrtoTracker_View.Dialogs.Dialog_05_Session_Options;
import FisioTracker.Android.OrtoTracker_View.Graphs.graph_01_speedometer;
import FisioTracker.Android.OrtoTracker_View.Graphs.graph_02_dispersal;
import FisioTracker.Android.OrtoTracker_Model.Services.service_04_Bluetooth;
import FisioTracker.Android.R;
import eightbitlab.com.blurview.BlurView;

public class activity_02_dashboard extends AppCompatActivity {

    private BlurView blurView;
    private BlurView blurView2;
    private BlurView blurView3;
    private BlurView blurView4;

    private static final int REQUEST_ENABLE_BT = 1001;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic uartRxCharacteristic;
    private graph_01_speedometer speedView;
    private final Handler handler = new Handler();
    private static final UUID UART_SERVICE_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private static final UUID UART_TX_CHAR_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private final Handler beepHandler = new Handler();
    private Runnable beepRunnable;
    private boolean isBeeping = false;
    private float currentSpeed = 0f;
    private final float maxSpeed = 30f;
    private final ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    private Window window;

    private ImageButton ultimoClicado;
    private int corNormal;
    private HashMap<Object, Object> coresSelecionadas;

    private ImageButton botaoMaior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_02_dashboard);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.setStatusBarColor(Color.TRANSPARENT);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        blurView = findViewById(R.id.glass);
        blurView2 = findViewById(R.id.glass2);
        blurView3 = findViewById(R.id.glass3);
        blurView4 = findViewById(R.id.nav);

        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(decorView).setFrameClearDrawable(windowBackground).setBlurRadius(24f);
        blurView2.setupWith(decorView).setFrameClearDrawable(windowBackground).setBlurRadius(24f);
        blurView3.setupWith(decorView).setFrameClearDrawable(windowBackground).setBlurRadius(24f);
        blurView4.setupWith(decorView).setFrameClearDrawable(windowBackground).setBlurRadius(0f);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        Button menuIcon = findViewById(R.id.menu);
        menuIcon.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        Button stopButton = findViewById(R.id.deleteButton);
        stopButton.setOnClickListener(v -> {
            Dialog_02_ConfirmAction dialog02ConfirmAction = new Dialog_02_ConfirmAction();
            dialog02ConfirmAction.showDialog(this);
        });

        Button addPacient = findViewById(R.id.add_pacient);
        addPacient.setOnClickListener(v -> {
            Dialog_04_Pacient_Form.showDialog_06(this);
        });

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_dashboard) {
                Toast.makeText(this, "Home clicado", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_settings) {
                Toast.makeText(this, "Configurações clicado", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        speedView = findViewById(R.id.speedView);
        Button startButton = findViewById(R.id.start);
        startButton.setOnClickListener(v -> {
            Toast.makeText(this, "Aguardando dados do Arduino via BLE...", Toast.LENGTH_SHORT).show();
        });

        Button bleButton = findViewById(R.id.ble);
        bleButton.setOnClickListener(v -> {
            initializeBluetoothAdapter(this, new service_04_Bluetooth.BluetoothAdapterCallback() {
                @Override
                public void onBluetoothNotSupported() {
                    Toast.makeText(getApplicationContext(), "Este dispositivo não suporta Bluetooth", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionsNotGranted() {
                    requestBluetoothPermissions(activity_02_dashboard.this);
                }

                @Override
                public void onBluetoothNotEnabled(BluetoothAdapter adapter) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ActivityCompat.checkSelfPermission(activity_02_dashboard.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            requestBluetoothPermissions(activity_02_dashboard.this);
                            onBluetoothReady(adapter);
                            return;
                        }
                    }

                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }

                @Override
                public void onBluetoothReady(BluetoothAdapter adapter) {
                    Dialog_03_BLE_Devices.showBleDevicesDialog(activity_02_dashboard.this, adapter, speedView);
                }
            });
        });

        Button newSession = findViewById(R.id.new_session_button);
        newSession.setOnClickListener(v -> {
            Dialog_05_Session_Options dialog05SessionOptions = new Dialog_05_Session_Options();
            dialog05SessionOptions.showDialog(this);
        });

        botaoMaior = findViewById(R.id.huge_pacient);

        corNormal = getColor(R.color.gray);
        coresSelecionadas = new HashMap<>();
        coresSelecionadas.put(R.id.button_pacient_1, getColor(R.color.pacient1));
        coresSelecionadas.put(R.id.button_pacient_2, getColor(R.color.pacient2));
        coresSelecionadas.put(R.id.button_pacient_3, getColor(R.color.pacient3));
        coresSelecionadas.put(R.id.button_pacient_4, getColor(R.color.pacient4));
        coresSelecionadas.put(R.id.button_pacient_5, getColor(R.color.pacient5));
        coresSelecionadas.put(R.id.button_pacient_6, getColor(R.color.pacient6));
        coresSelecionadas.put(R.id.button_pacient_7, getColor(R.color.pacient7));
        coresSelecionadas.put(R.id.button_pacient_8, getColor(R.color.pacient8));
        coresSelecionadas.put(R.id.button_pacient_9, getColor(R.color.pacient9));
        coresSelecionadas.put(R.id.button_pacient_10, getColor(R.color.pacient10));

        graph_02_dispersal chartView = findViewById(R.id.chartView);

        int[] botoesIds = {
                R.id.button_pacient_1,
                R.id.button_pacient_2,
                R.id.button_pacient_3,
                R.id.button_pacient_4,
                R.id.button_pacient_5,
                R.id.button_pacient_6,
                R.id.button_pacient_7,
                R.id.button_pacient_8,
                R.id.button_pacient_9,
                R.id.button_pacient_10};

        for (int id : botoesIds) {
            ImageButton btn = findViewById(id);
            btn.setOnClickListener(v -> selecionarBotao((ImageButton) v));
        }
    }

    private void handleBeeping() {
        if (!isBeeping) {
            startBeeping();
        }
    }

    private void startBeeping() {
        isBeeping = true;

        beepRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isBeeping) return;

                float ratio = currentSpeed / maxSpeed;
                ratio = Math.min(1f, Math.max(0f, ratio));

                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100);

                long delay = (long) (2000 - 1800 * ratio);
                delay = Math.max(100, delay);

                beepHandler.postDelayed(this, delay);
            }
        };

        beepHandler.post(beepRunnable);
    }

    private void stopBeeping() {
        isBeeping = false;
        if (beepRunnable != null) {
            beepHandler.removeCallbacks(beepRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bluetoothGatt != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        bluetoothGatt.close();
                    }
                } else {
                    bluetoothGatt.close();
                }
            } catch (Exception e) {
                Log.e("BLE", "Error closing GATT: " + e.getMessage());
            }
            bluetoothGatt = null;
        }
    }

    public void enableNavBarOptions() {
        Button stopButton = findViewById(R.id.pause);
        Button deleteButton = findViewById(R.id.deleteButton);
        stopButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        deleteButton.startAnimation(animation);
    }

    public void disableNavBarOptions() {
        Button stopButton = findViewById(R.id.pause);
        Button deleteButton = findViewById(R.id.deleteButton);
        stopButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
    }

    public void enableSimultaneousPacientVisibility() {
        BlurView pacienteSimultaneo = findViewById(R.id.glass2);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.expand_animation);
        pacienteSimultaneo.startAnimation(animation);
        pacienteSimultaneo.setVisibility(View.VISIBLE);
        enableNavBarOptions();
        Button add_Pacient = findViewById(R.id.new_session_button);
        add_Pacient.setBackgroundResource(R.drawable.bg_container_gradient);
        add_Pacient.setClickable(false);
        Button start = findViewById(androidx.constraintlayout.widget.R.id.start);
        start.setBackgroundResource(R.drawable.button_bg_blue_gradient);
    }

    public void disableSimultaneousPacientVisibility(Context context) {
        BlurView pacienteSimultaneo = findViewById(R.id.glass2);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.srhink_animation);
        pacienteSimultaneo.startAnimation(animation);
        pacienteSimultaneo.setVisibility(View.GONE);
        disableNavBarOptions();
        Button add_Pacient = findViewById(R.id.new_session_button);
        add_Pacient.setClickable(true);
        add_Pacient.setBackgroundResource(R.drawable.button_bg_blue_gradient);
        add_Pacient.setTextColor(ContextCompat.getColor(this, R.color.white));
        LottieAnimationView animationView = findViewById(R.id.lottie_login_bg);
        Button start = findViewById(androidx.constraintlayout.widget.R.id.start);
        start.setBackgroundResource(R.drawable.shape_auth_container);
    }

    private void selecionarBotao(ImageButton botaoAtual) {
        if (ultimoClicado != null && ultimoClicado != botaoAtual) {
            ultimoClicado.setColorFilter(corNormal, PorterDuff.Mode.SRC_IN);
        }

        int corSelecionada = (int) coresSelecionadas.get(botaoAtual.getId());

        botaoAtual.setColorFilter(corSelecionada, PorterDuff.Mode.SRC_IN);

        graph_02_dispersal chartView = findViewById(R.id.chartView);

        int fillColor = (0x99 << 24) | (corSelecionada & 0x00FFFFFF); // alpha ~60%

        chartView.setChartColors(corSelecionada, fillColor, corSelecionada);

        botaoMaior.setColorFilter(corSelecionada, PorterDuff.Mode.SRC_IN);

        ultimoClicado = botaoAtual;
    }
}


