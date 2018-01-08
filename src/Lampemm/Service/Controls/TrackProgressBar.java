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

    /**
     * Sets position on the motorized slide potentiometer based on current
     * playback.
     * @param currentPlayback
     */
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

    /**
     * Use current playback to get track duration. Compare to position of
     * the slide potentiometer to determine tracked time.
     * @param currentPlayback
     * @return
     */
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

    /**
     * Returns whether or not a user is manually tracking the
     * progress bar.
     * @return
     */
    public boolean isTracking() {
        final float naturalInterval = motorizedSlidePotentiometer.getNaturalInterval();
        final float actualInterval = motorizedSlidePotentiometer.getActualInterval();

        if (actualInterval != 0 && actualInterval != naturalInterval) {
            return true;
        } else {
            return false;
        }
    }
}
