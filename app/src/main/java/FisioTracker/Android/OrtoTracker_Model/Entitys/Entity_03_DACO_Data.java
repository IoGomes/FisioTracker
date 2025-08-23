package FisioTracker.Android.OrtoTracker_Model.Entitys;

public class Entity_03_DACO_Data implements Entity_00_Interface {
    private String deviceName;
    private String dataOfScan;
    private int scanRawData;
    private boolean deviceConnected;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDataOfScan() {
        return dataOfScan;
    }

    public void setDataOfScan(String dataOfScan) {
        this.dataOfScan = dataOfScan;
    }

    public int getScanRawData() {
        return scanRawData;
    }

    public void setScanRawData(int scanRawData) {
        this.scanRawData = scanRawData;
    }

    public boolean isDeviceConnected() {
        return deviceConnected;
    }

    public void setDeviceConnected(boolean deviceConnected) {
        this.deviceConnected = deviceConnected;
    }

    @Override
    public boolean enabled() {
        return false;
    }
}
