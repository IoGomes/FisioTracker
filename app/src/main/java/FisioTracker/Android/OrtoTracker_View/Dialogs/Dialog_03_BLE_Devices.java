package FisioTracker.Android.OrtoTracker_View.Dialogs;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

import FisioTracker.Android.OrtoTracker_View.Activities.activity_02_dashboard;
import FisioTracker.Android.OrtoTracker_View.Graphs.graph_01_speedometer;
import FisioTracker.Android.OrtoTracker_Model.Services.service_04_Bluetooth;
import FisioTracker.Android.R;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class Dialog_03_BLE_Devices {
    private BluetoothAdapter bluetoothAdapter;
    private final ArrayList<String> bluetoothDevices = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private AlertDialog bleDialog;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic uartRxCharacteristic;
    private graph_01_speedometer speedView;
    private final Handler handler = new Handler();
    private BluetoothLeScanner bleScanner;
    private boolean isScanning = false;

    // Referência para a activity
    private activity_02_dashboard dashboardActivity;

    private static final UUID UART_SERVICE_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private static final UUID UART_TX_CHAR_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    // Interface para callback quando conectar
    public interface BleConnectionCallback {
        void onDeviceConnected(BluetoothGatt gatt);
        void onDeviceDisconnected();
        void onDataReceived(int value);
    }

    private BleConnectionCallback connectionCallback;

    // Construtor
    public Dialog_03_BLE_Devices(activity_02_dashboard activity, BluetoothAdapter bluetoothAdapter) {
        this.dashboardActivity = activity;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public void setConnectionCallback(BleConnectionCallback callback) {
        this.connectionCallback = callback;
    }

    public void setSpeedView(graph_01_speedometer speedView) {
        this.speedView = speedView;
    }

    public static void showBleDevicesDialog(activity_02_dashboard activity, BluetoothAdapter bluetoothAdapter, graph_01_speedometer speedView) {
        // Criar instância do dialog para uso interno
        Dialog_03_BLE_Devices dialogInstance = new Dialog_03_BLE_Devices(activity, bluetoothAdapter);
        if (speedView != null) {
            dialogInstance.setSpeedView(speedView);
        }

        dialogInstance.showDialog();
    }

    public void showDialog() {
            service_04_Bluetooth.verifyBluetooth(bluetoothAdapter, dashboardActivity);

            LayoutInflater inflater = LayoutInflater.from(dashboardActivity);
            View dialogView = inflater.inflate(R.layout.dialog_03_ble_devices, null);

            // Inicializar componentes do dialog
            ListView listView = dialogView.findViewById(R.id.dialog_list);
            ImageView closeIcon = dialogView.findViewById(R.id.close_icon);

            adapter = new ArrayAdapter<>(dashboardActivity, android.R.layout.simple_list_item_1, bluetoothDevices);
            listView.setAdapter(adapter);

            // Criar o dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(dashboardActivity);
            builder.setView(dialogView);
            bleDialog = builder.create();

            // Mostrar o dialog
            bleDialog.show();


        BlurView blurView = dialogView.findViewById(R.id.ble_device);

        if (blurView != null) {
            float radius = 15f;
            View decorView = dashboardActivity.getWindow().getDecorView();
            ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

            blurView.setupWith(rootView, new RenderScriptBlur(dashboardActivity))
                    .setBlurRadius(radius);
        }

        if (bleDialog.getWindow() != null) {
            bleDialog.getWindow().setBackgroundDrawable(
                    ContextCompat.getDrawable(dashboardActivity, R.drawable.list_dialog_background)
            );
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(dashboardActivity, "Selecionado: " + bluetoothDevices.get(position), Toast.LENGTH_SHORT).show();
            bleDialog.dismiss();
            stopBleScan();
            connectToDevice(position);
        });

        closeIcon.setOnClickListener(v -> {
            bleDialog.dismiss();
            stopBleScan();
        });

        startBleScan();
        bleDialog.show();
        Window window = bleDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(window.getAttributes());
            params.width = 950; // largura em pixels
            window.setAttributes(params);
        }
    }

    private void startBleScan() {
        if (isScanning) return;

        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bleScanner == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(dashboardActivity, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        bleScanner.startScan(null, settings, bleScanCallback);
        isScanning = true;

        handler.postDelayed(this::stopBleScan, 10000);
    }

    private void stopBleScan() {
        if (!isScanning || bleScanner == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(dashboardActivity, Manifest.permission.BLUETOOTH_SCAN)
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
                if (ActivityCompat.checkSelfPermission(dashboardActivity,
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
                dashboardActivity.runOnUiThread(() -> {
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
            dashboardActivity.runOnUiThread(() -> {
                Toast.makeText(dashboardActivity,
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
            if (ContextCompat.checkSelfPermission(dashboardActivity, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(dashboardActivity, "Permissão Bluetooth necessária para conectar", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
        if (device == null) {
            Toast.makeText(dashboardActivity, "Dispositivo não encontrado", Toast.LENGTH_SHORT).show();
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
            bluetoothGatt = device.connectGatt(dashboardActivity, false, gattCallback);
        } catch (SecurityException e) {
            Log.e("BLE", "Erro de permissão ao conectar GATT: " + e.getMessage());
            Toast.makeText(dashboardActivity, "Erro de permissão ao conectar", Toast.LENGTH_SHORT).show();
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                dashboardActivity.runOnUiThread(() -> {
                    Toast.makeText(dashboardActivity, "Conectado ao dispositivo BLE", Toast.LENGTH_SHORT).show();
                });

                if (connectionCallback != null) {
                    connectionCallback.onDeviceConnected(gatt);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(dashboardActivity,
                            Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        gatt.discoverServices();
                    }
                } else {
                    gatt.discoverServices();
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                dashboardActivity.runOnUiThread(() -> {
                    Toast.makeText(dashboardActivity, "Desconectado do dispositivo BLE", Toast.LENGTH_SHORT).show();
                });
                uartRxCharacteristic = null;

                if (connectionCallback != null) {
                    connectionCallback.onDeviceDisconnected();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e("BLE", "Service discovery failed with status: " + status);
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(dashboardActivity,
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

            return (float) (raw - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
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

                        dashboardActivity.runOnUiThread(() -> updateSpeedView(displayedSpeed));

                        if (connectionCallback != null) {
                            connectionCallback.onDataReceived(displayedSpeed);
                        }

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

    private int currentSpeed = 0;

    private boolean verifyBluetoothLocal() {
        if (bluetoothAdapter == null) {
            Toast.makeText(dashboardActivity, "Bluetooth não disponível", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(dashboardActivity, "Ative o Bluetooth primeiro", Toast.LENGTH_SHORT).show();
            return false;
        }

        bluetoothDevices.clear();
        BluetoothLeScanner bleScanner = bluetoothAdapter.getBluetoothLeScanner();

        if (bleScanner == null) {
            Toast.makeText(dashboardActivity, "Scanner BLE não disponível", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateSpeedView(int speed) {
        if (speedView != null) {
            handler.post(() -> {
                speedView.speedTo(speed);
                currentSpeed = speed;
                handleBeeping();
            });
        }
    }

    private void handleBeeping() {
        // Implementar lógica de beeping conforme necessário
        // Exemplo básico:
        if (currentSpeed > 20) { // Limite de exemplo
            // Lógica para beep de alerta
            // playBeepSound(); // se você tiver um método para isso
        }
    }

    // Métodos públicos para controle externo
    public void disconnect() {
        if (bluetoothGatt != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(dashboardActivity, Manifest.permission.BLUETOOTH_CONNECT)
                            == PackageManager.PERMISSION_GRANTED) {
                        bluetoothGatt.disconnect();
                        bluetoothGatt.close();
                    }
                } else {
                    bluetoothGatt.disconnect();
                    bluetoothGatt.close();
                }
            } catch (SecurityException e) {
                Log.e("BLE", "Erro de permissão ao desconectar: " + e.getMessage());
            }
            bluetoothGatt = null;
        }
        stopBleScan();
    }

    public boolean isConnected() {
        return bluetoothGatt != null && uartRxCharacteristic != null;
    }

    public void cleanup() {
        disconnect();
        handler.removeCallbacksAndMessages(null);
    }
}
