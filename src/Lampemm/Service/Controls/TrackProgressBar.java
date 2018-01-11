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
    private float lastTrackingPosition = -1;

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
            float trackProgress = motorizedSlidePotentiometer.getPosition();
            int timeElapsed = getTimeElapsed(duration, trackProgress);
            return timeElapsed;
        } catch (IOException ex) {
            System.out.println(ex);
            return 0;
        }
    }

    public int getTimeElapsedForLastTrackingEvent(CurrentPlayback currentPlayback) {
        int duration = currentPlayback.getDuration();
        float trackProgress = this.lastTrackingPosition;
        int timeElapsed = getTimeElapsed(duration, trackProgress);
        return timeElapsed;
    }

    public int getTimeElapsed (int duration, float progress) {
        return Math.round(duration * progress);
    }

    /**
     * Returns whether or not a user is manually tracking the
     * progress bar.
     * @return
     */
    public boolean isTracking() {

        final float naturalInterval = motorizedSlidePotentiometer.getNaturalInterval();
        final boolean isNaturalIntervalNegative = naturalInterval < 0;
        final float actualInterval = motorizedSlidePotentiometer.getActualInterval();
        final float actualIntervalAbs = Math.abs(actualInterval);

        BufferedValue bufferedNaturalInterval = new BufferedValue(Math.abs(naturalInterval), TRACKING_TOLERANCE_BUFFER);
        BufferedValue bufferedActualInterval = new BufferedValue(actualIntervalAbs, ACTUAL_INTERVAL_BUFFER);

        final boolean isTrackingNotNatural = !bufferedNaturalInterval.isWithinBoundsInclusive(actualIntervalAbs);
        final boolean isTrackingDifferentActual = !bufferedActualInterval.isWithinBoundsInclusive(lastActualInterval);

        this.lastActualInterval = actualIntervalAbs;

        final boolean isTracking = isTrackingDifferentActual && isTrackingNotNatural && !isNaturalIntervalNegative;

        if (isTracking) {
            // Cancel any current motor movement - we're tracking!
            motorizedSlidePotentiometer.setInterupt();
            try {
                this.lastTrackingPosition = motorizedSlidePotentiometer.getPosition();
            } catch (IOException e) {
                System.out.println("Couldn't get position:" + e);
            }
        }

        return isTracking;
    }
}


