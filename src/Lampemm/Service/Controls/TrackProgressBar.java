package Lampemm.Service.Controls;

import Lampemm.Model.CurrentPlayback;

import java.io.IOException;

/**
 * Created by Mark on 8/11/17.
 */
public class TrackProgressBar {
    private MotorizedSlidePotentiometer motorizedSlidePotentiometer;

    private static final TrackProgressBar instance = new TrackProgressBar();

    private TrackProgressBar() {
        this.motorizedSlidePotentiometer = MotorizedSlidePotentiometer.getInstance();
    }

    public static TrackProgressBar getInstance() {
        return instance;
    }

    public void setPositionForTimeElapsed(CurrentPlayback currentPlayback) {
        try {
            int timeElapsed = currentPlayback.getTimeElapsed();
            int duration = currentPlayback.getDuration();
            float trackProgress = (float) timeElapsed / duration;
            motorizedSlidePotentiometer.setPosition(trackProgress);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public int getTimeElapsedForPosition(CurrentPlayback currentPlayback) {
        try {
            int duration = currentPlayback.getDuration();
            float trackProgress = 0;
            trackProgress = motorizedSlidePotentiometer.getPosition();
            int timeElapsed = Math.round(duration * trackProgress);
            return timeElapsed;
        } catch (IOException ex) {
            System.out.println(ex);
            return 0;
        }
    }
}
