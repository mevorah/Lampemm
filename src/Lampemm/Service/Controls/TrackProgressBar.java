package Lampemm.Service.Controls;

import Lampemm.Model.CurrentPlayback;

/**
 * Created by Mark on 8/11/17.
 */
public class TrackProgressBar {
    private MotorizedSlidePotentiometer motorizedSlidePotentiometer;

    private static final TrackProgressBar instance = new TrackProgressBar();

    private TrackProgressBar() {}

    public static TrackProgressBar getInstance() {
        return instance;
    }

    public void setPositionForTimeElapsed(CurrentPlayback currentPlayback) {
        int timeElapsed = currentPlayback.getTimeElapsed();
        int duration = currentPlayback.getDuration();
        float trackProgress = (float) timeElapsed / duration;
        motorizedSlidePotentiometer.setPosition(trackProgress);
    }

    public int getTimeElapsedForPosition(CurrentPlayback currentPlayback) {
        int duration = currentPlayback.getDuration();
        float trackProgress = motorizedSlidePotentiometer.getPosition();
        int timeElapsed = Math.round(duration * trackProgress);
        return timeElapsed;
    }
}
