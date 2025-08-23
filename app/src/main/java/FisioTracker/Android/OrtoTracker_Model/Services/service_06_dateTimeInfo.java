package FisioTracker.Android.OrtoTracker_Model.Services;

import android.util.Log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class service_06_dateTimeInfo {
    private LocalDateTime dateTimeInfo;

    public service_06_dateTimeInfo(){
        this.dateTimeInfo = LocalDateTime.now();
    }

    public void printDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Log.e("Data", "dateTime: " + dateTimeInfo.format(formatter));
    }
}
