package FisioTracker.Android.OrtoTracker_Services;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class service_04_Bluetooth {

    private BluetoothAdapter bluetoothAdapter;
    private final ArrayList<String> bluetoothDevices = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private static final int REQUEST_BLUETOOTH_CONNECT = 1001;

    public static void requestBluetoothPermissions(Activity activity) {
        List<String> permissionsToRequest = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN);
            }

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
            }

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADVERTISE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            }

        } else {

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH);
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADMIN);
        }

        if (!permissionsToRequest.isEmpty()) {
            String[] permissions = permissionsToRequest.toArray(new String[0]);
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_BLUETOOTH_CONNECT);
        }
    }

    public static boolean hasAllBluetoothPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static void initializeBluetoothAdapter(Activity activity, BluetoothAdapterCallback callback) {
        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            callback.onBluetoothNotSupported();
            return;
        }

        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            callback.onBluetoothNotSupported();
            return;
        }

        // Verificar se as permissões estão concedidas
        if (!hasAllBluetoothPermissions(activity)) {
            callback.onPermissionsNotGranted();
            return;
        }

        // Verificar se o Bluetooth está habilitado
        if (!bluetoothAdapter.isEnabled()) {
            callback.onBluetoothNotEnabled(bluetoothAdapter);
            return;
        }

        // Tudo OK
        callback.onBluetoothReady(bluetoothAdapter);
    }

    public interface BluetoothAdapterCallback {
        void onBluetoothReady(BluetoothAdapter adapter);
        void onBluetoothNotSupported();
        void onPermissionsNotGranted();
        void onBluetoothNotEnabled(BluetoothAdapter adapter);
    }

    public static void verifyBluetooth(BluetoothAdapter bluetoothAdapter, Context context) {

        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth não disponível", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(context, "Ative o Bluetooth primeiro", Toast.LENGTH_SHORT).show();
            return;
        }

        BluetoothLeScanner bleScanner = bluetoothAdapter.getBluetoothLeScanner();

        if (bleScanner == null) {
            Toast.makeText(context, "Scanner BLE não disponível", Toast.LENGTH_SHORT).show();
        }
    }
}
