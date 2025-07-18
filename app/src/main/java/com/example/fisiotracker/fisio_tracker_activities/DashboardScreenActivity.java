package com.example.fisiotracker.fisio_tracker_activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import android.media.AudioManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.fisiotracker.fisio_tracker_services.GradientTubeSpeedometer;
import com.example.fisiotracker.R;
import com.google.android.material.navigation.NavigationView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import eightbitlab.com.blurview.BlurView;

public class DashboardScreenActivity extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH_CONNECT = 1001;
    private BlurView blurView;
    private BluetoothAdapter bluetoothAdapter;
    private final ArrayList<String> bluetoothDevices = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private AlertDialog bleDialog;

    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic uartRxCharacteristic;

    private GradientTubeSpeedometer speedView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        List<PointF> dadosAleatorios = new ArrayList<>();
        Random random = new Random();

        int quantidadePontos = 10;
        for(int i = 1; i <= quantidadePontos; i++) {
            float x = i;
            float y = random.nextFloat() * 10f; // valor entre 0 e 10
            dadosAleatorios.add(new PointF(x, y));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_screen);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        window.setStatusBarColor(Color.TRANSPARENT);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // BlurView
        blurView = findViewById(R.id.glass);
        ViewGroup rootView = (ViewGroup) window.getDecorView().getRootView();
        Drawable windowBackground = window.getDecorView().getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(20f);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        Button menuIcon = findViewById(R.id.menu);
        menuIcon.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        // Listener dos itens do menu lateral
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
        bleButton.setOnClickListener(v -> openBleDialog());

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth não suportado", Toast.LENGTH_LONG).show();
        }
    }


    private void showCustomDialog() {
        String[] options = {
                "• Placa DCP",
                "• Placa em L",
                "• Síntese DCS",
                "• Síntese DHS",
                "• Placa Maleolar",
                "• Fixador externo tubular",
                "• Ilizarov"
        };

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        ListView listView = dialogView.findViewById(R.id.dialog_list);
        ImageView menuIcon = dialogView.findViewById(R.id.menu_icon);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    ContextCompat.getDrawable(this, R.drawable.list_dialog_background)
            );
        }

        dialog.show();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(this, "Selecionado: " + options[position], Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        menuIcon.setOnClickListener(v -> dialog.dismiss());
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


    private void openBleDialog() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Ative o Bluetooth primeiro", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
            return;
        }

        bluetoothDevices.clear();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);

        bluetoothAdapter.startDiscovery();

        View dialogView = getLayoutInflater().inflate(R.layout.ble_devices_dialog, null);
        ListView listView = dialogView.findViewById(R.id.dialog_list);
        ImageView closeIcon = dialogView.findViewById(R.id.close_icon);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bluetoothDevices);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        bleDialog = builder.create();
        if (bleDialog.getWindow() != null) {
            bleDialog.getWindow().setBackgroundDrawable(
                    ContextCompat.getDrawable(this, R.drawable.list_dialog_background)
            );
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(this, "Selecionado: " + bluetoothDevices.get(position), Toast.LENGTH_SHORT).show();
            bleDialog.dismiss();
            bluetoothAdapter.cancelDiscovery();
            unregisterReceiver(bluetoothReceiver);

            connectToDevice(position);
        });

        closeIcon.setOnClickListener(v -> {
            bleDialog.dismiss();
            bluetoothAdapter.cancelDiscovery();
            unregisterReceiver(bluetoothReceiver);
        });

        bleDialog.show();
    }

    private void connectToDevice(int position) {
        if (position < 0 || position >= bluetoothDevices.size()) return;

        String deviceInfo = bluetoothDevices.get(position);
        String[] parts = deviceInfo.split("\n");
        if (parts.length < 2) return;
        String macAddress = parts[1];

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissão Bluetooth necessária para conectar", Toast.LENGTH_SHORT).show();
            return;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
        if (device == null) {
            Toast.makeText(this, "Dispositivo não encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bluetoothGatt != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    bluetoothGatt.close();
                }
            } else {
                bluetoothGatt.close();
            }
            bluetoothGatt = null;
        }

        bluetoothGatt = device.connectGatt(this, false, gattCallback);
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device != null &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                                == PackageManager.PERMISSION_GRANTED &&
                        device.getName() != null) {

                    String deviceInfo = device.getName() + "\n" + device.getAddress();
                    if (!bluetoothDevices.contains(deviceInfo)) {
                        bluetoothDevices.add(deviceInfo);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread(() -> Toast.makeText(DashboardScreenActivity.this, "Conectado ao dispositivo BLE", Toast.LENGTH_SHORT).show());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(DashboardScreenActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        gatt.discoverServices();
                    }
                } else {
                    gatt.discoverServices();
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread(() -> Toast.makeText(DashboardScreenActivity.this, "Desconectado do dispositivo BLE", Toast.LENGTH_SHORT).show());
                uartRxCharacteristic = null;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(DashboardScreenActivity.this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }

            BluetoothGattService uartService = gatt.getService(UART_SERVICE_UUID);
            if (uartService != null) {
                BluetoothGattCharacteristic uartTxCharacteristic = uartService.getCharacteristic(UART_TX_CHAR_UUID);
                if (uartTxCharacteristic != null) {
                    uartRxCharacteristic = uartTxCharacteristic;

                    gatt.setCharacteristicNotification(uartTxCharacteristic, true);

                    BluetoothGattDescriptor descriptor = uartTxCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);
                    if (descriptor != null) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(descriptor);
                    }
                }
            }
        }

        private float mapRawToKg(long raw, long inMin, long inMax, float outMin, float outMax) {
            if (raw < inMin) raw = inMin;
            if (raw > inMax) raw = inMax;

            return (float)(raw - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (UART_TX_CHAR_UUID.equals(characteristic.getUuid())) {
                byte[] data = characteristic.getValue();
                String received = new String(data, StandardCharsets.UTF_8).trim();

                try {
                    long rawValue = Long.parseLong(received);

                    float kg = mapRawToKg(rawValue, -2842470, -742470, 0f, 30f);

                    int displayedSpeed = Math.round(kg);
                    updateSpeedView(displayedSpeed);

                } catch (NumberFormatException e) {
                    Log.e("Bluetooth", "Erro ao converter leitura: " + received);
                }
            }
        }

    };

    private void updateSpeedView(int speed) {
        handler.post(() -> {
            speedView.speedTo(speed);
            currentSpeed = speed;
            handleBeeping();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(bluetoothReceiver);
        } catch (Exception ignored) {
        }

        if (bluetoothGatt != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        == PackageManager.PERMISSION_GRANTED) {
                    bluetoothGatt.close();
                }
            } else {
                bluetoothGatt.close();
            }
            bluetoothGatt = null;
        }
    }
}