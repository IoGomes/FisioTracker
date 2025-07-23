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
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
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
import com.example.fisiotracker.fisio_tracker_graphs.graph_01_speedometer;
import com.example.fisiotracker.R;
import com.google.android.material.navigation.NavigationView;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanSettings;

import eightbitlab.com.blurview.BlurView;

public class activity_02_dashboard extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH_CONNECT = 1001;
    private BlurView blurView;
    private BluetoothAdapter bluetoothAdapter;
    private final ArrayList<String> bluetoothDevices = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private AlertDialog bleDialog;
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
        setContentView(R.layout.activity_02_dashboard);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        window.setStatusBarColor(Color.TRANSPARENT);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        blurView = findViewById(R.id.glass);
        ViewGroup rootView = (ViewGroup) window.getDecorView().getRootView();
        Drawable windowBackground = window.getDecorView().getBackground();
        blurView.setupWith(rootView).setFrameClearDrawable(windowBackground).setBlurRadius(20f);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        Button menuIcon = findViewById(R.id.menu);
        menuIcon.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            blurView = findViewById(R.id.glass);
            blurView.setupWith(rootView)
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(12f);
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

        Button menubutton = findViewById(R.id.menuicon);
        menubutton.setOnClickListener(v -> showCustomDialog());

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
        View dialogView = inflater.inflate(R.layout.dialog_02_daco, null);

        ListView listView = dialogView.findViewById(R.id.dialog_list);
        Button menuIcon = dialogView.findViewById(R.id.menu_icon);

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


        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth não disponível", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Ative o Bluetooth primeiro", Toast.LENGTH_SHORT).show();
            return;
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT
                        }, REQUEST_BLUETOOTH_CONNECT);
                return;
            }
        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_BLUETOOTH_CONNECT);
                return;
            }
        }

        bluetoothDevices.clear();

        BluetoothLeScanner bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bleScanner == null) {
            Toast.makeText(this, "Scanner BLE não disponível", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_01_ble_devices, null);
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
            stopBleScan();
            connectToDevice(position);
        });

        closeIcon.setOnClickListener(v -> {
            bleDialog.dismiss();
            stopBleScan();
        });

        // Iniciar scan BLE
        startBleScan();

        bleDialog.show();
    }

    private BluetoothLeScanner bleScanner;
    private boolean isScanning = false;

    private void startBleScan() {
        if (isScanning) return;

        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bleScanner == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        bleScanner.startScan(null, settings, bleScanCallback);
        isScanning = true;

        new Handler().postDelayed(this::stopBleScan, 10000);
    }

    private void stopBleScan() {
        if (!isScanning || bleScanner == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        bleScanner.stopScan(bleScanCallback);
        isScanning = false;
    }

    private final ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(activity_02_dashboard.this,
                        Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }

            String deviceName = device.getName();
            if (deviceName == null || deviceName.isEmpty()) {
                deviceName = "Dispositivo Desconhecido";
            }

            String deviceInfo = deviceName + "\n" + device.getAddress();

            if (!bluetoothDevices.contains(deviceInfo)) {
                runOnUiThread(() -> {
                    bluetoothDevices.add(deviceInfo);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("BLE", "Scan failed with error code: " + errorCode);
            runOnUiThread(() -> {
                Toast.makeText(activity_02_dashboard.this,
                        "Falha no scan BLE: " + errorCode, Toast.LENGTH_SHORT).show();
            });
        }
    };

    private void connectToDevice(int position) {
        if (position < 0 || position >= bluetoothDevices.size()) return;

        String deviceInfo = bluetoothDevices.get(position);
        String[] parts = deviceInfo.split("\n");
        if (parts.length < 2) return;
        String macAddress = parts[1];

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão Bluetooth necessária para conectar", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
        if (device == null) {
            Toast.makeText(this, "Dispositivo não encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bluetoothGatt != null) {
            try {
                bluetoothGatt.close();
            } catch (SecurityException e) {
                Log.e("BLE", "Erro de permissão ao fechar GATT: " + e.getMessage());
            }
            bluetoothGatt = null;
        }

        try {
            bluetoothGatt = device.connectGatt(this, false, gattCallback);
        } catch (SecurityException e) {
            Log.e("BLE", "Erro de permissão ao conectar GATT: " + e.getMessage());
            Toast.makeText(this, "Erro de permissão ao conectar", Toast.LENGTH_SHORT).show();
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread(() -> Toast.makeText(activity_02_dashboard.this, "Conectado ao dispositivo BLE", Toast.LENGTH_SHORT).show());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(activity_02_dashboard.this,
                            Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        gatt.discoverServices();
                    }
                } else {
                    gatt.discoverServices();
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread(() -> Toast.makeText(activity_02_dashboard.this, "Desconectado do dispositivo BLE", Toast.LENGTH_SHORT).show());
                uartRxCharacteristic = null;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e("BLE", "Service discovery failed with status: " + status);
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(activity_02_dashboard.this,
                        Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }

            BluetoothGattService uartService = gatt.getService(UART_SERVICE_UUID);
            if (uartService != null) {
                BluetoothGattCharacteristic uartTxCharacteristic = uartService.getCharacteristic(UART_TX_CHAR_UUID);
                if (uartTxCharacteristic != null) {
                    uartRxCharacteristic = uartTxCharacteristic;

                    boolean notificationSet = gatt.setCharacteristicNotification(uartTxCharacteristic, true);
                    if (!notificationSet) {
                        Log.e("BLE", "Failed to set characteristic notification");
                        return;
                    }

                    BluetoothGattDescriptor descriptor = uartTxCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);
                    if (descriptor != null) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        boolean writeResult = gatt.writeDescriptor(descriptor);
                        if (!writeResult) {
                            Log.e("BLE", "Failed to write descriptor");
                        }
                    } else {
                        Log.e("BLE", "Descriptor not found");
                    }
                } else {
                    Log.e("BLE", "UART TX characteristic not found");
                }
            } else {
                Log.e("BLE", "UART service not found");
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
                if (data != null && data.length > 0) {
                    String received = new String(data, StandardCharsets.UTF_8).trim();

                    try {
                        long rawValue = Long.parseLong(received);
                        float kg = mapRawToKg(rawValue, -2842470, -742470, 0f, 30f);
                        int displayedSpeed = Math.round(kg);

                        runOnUiThread(() -> updateSpeedView(displayedSpeed));

                    } catch (NumberFormatException e) {
                        Log.e("Bluetooth", "Erro ao converter leitura: " + received);
                    }
                }
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE", "Descriptor written successfully");
            } else {
                Log.e("BLE", "Failed to write descriptor, status: " + status);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopBleScan();

        if (bluetoothGatt != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                            == PackageManager.PERMISSION_GRANTED) {
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


    private void updateSpeedView(int speed) {
        handler.post(() -> {
            speedView.speedTo(speed);
            currentSpeed = speed;
            handleBeeping();
        });
    }
}