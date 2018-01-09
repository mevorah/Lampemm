package Lampemm.Service.Controls;

import Lampemm.Model.BufferedValue;
import Lampemm.Model.CurrentPlayback;

import java.io.IOException;

/**
 * Created by Mark on 8/11/17.
 */
public class TrackProgressBar {
    private MotorizedSlidePotentiometer motorizedSlidePotentiometer;

    private final static float TRACKING_TOLERANCE_BUFFER = 10;
    private final static float ACTUAL_INTERVAL_BUFFER = 3;


    private static final TrackProgressBar instance = new TrackProgressBar();

    private float lastActualInterval = -1;

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
        setPositionForTimeElapsed(currentPlayback.getTimeElapsed(), currentPlayback.getDuration());
    }

    public void setPositionForTimeElapsed(int timeElapsed, int duration) {
        try {
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
        final float naturalInterval = Math.abs(motorizedSlidePotentiometer.getNaturalInterval());
        final float actualInterval = Math.abs(motorizedSlidePotentiometer.getActualInterval());

        BufferedValue bufferedNaturalInterval = new BufferedValue(naturalInterval, TRACKING_TOLERANCE_BUFFER);
        BufferedValue bufferedActualInterval = new BufferedValue(actualInterval, ACTUAL_INTERVAL_BUFFER);

        final boolean isTrackingNotNatural = !bufferedNaturalInterval.isWithinBoundsInclusive(actualInterval);
        final boolean isTrackingDifferentActual = !bufferedActualInterval.isWithinBoundsInclusive(lastActualInterval);

        this.lastActualInterval = actualInterval;

        final boolean isTracking = isTrackingDifferentActual && isTrackingNotNatural;

        System.out.println("IS Tracking:"+ isTracking);

        return isTracking;
    }
}
