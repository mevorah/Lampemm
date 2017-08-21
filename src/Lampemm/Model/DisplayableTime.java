package Lampemm.Model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mark on 8/12/17.
 */
public class DisplayableTime {

    private final int timeMillis;

    public DisplayableTime(int timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String toString() {
        int seconds = timeMillis / 1000;
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        String time = String.format("%02d:%02d", minutes, remainingSeconds);
        return time;
    }
}
