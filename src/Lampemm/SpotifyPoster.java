package Lampemm;

import Lampemm.Model.CurrentPlayback;
import Lampemm.Model.DisplayableTime;
import Lampemm.Service.Controls.TrackProgressBar;
import Lampemm.Service.Display.MusicDisplay;
import Lampemm.Service.Display.TwoLineDisplay;
import Lampemm.Service.Spotify.SpotifyServiceProxy;
import com.wrapper.spotify.models.Track;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mark on 8/11/17.
 */
public class SpotifyPoster {
    private final ScheduledExecutorService trackProgressBarPoller = Executors.newSingleThreadScheduledExecutor();
    private final long POLLING_INITIAL_DELAY_MILLIS = 0;
    private final long PROGRESS_POLLING_RATE_MILLIS = 250;

    private final SpotifyServiceProxy spotifyServiceProxy;
    private final TrackProgressBar trackProgressBar;
    private final MusicDisplay musicDisplay;

    public SpotifyPoster (SpotifyServiceProxy spotifyServiceProxy,
                          TrackProgressBar trackProgressBar,
                          MusicDisplay musicDisplay) {
        this.spotifyServiceProxy = spotifyServiceProxy;
        this.trackProgressBar = trackProgressBar;
        this.musicDisplay = musicDisplay;
    }

    public void start() {
        final Runnable updater = new Runnable() {
            private boolean isTrackingPreviousValue = false;

            /**
             * Triggered callback when the track progress bar's
             * position is manually changed.
             */
            public void progressManuallyChanged() {
                CurrentPlayback currentPlayback = spotifyServiceProxy.getCurrentPlayback();
                int positionTime = trackProgressBar.getTimeElapsedForPosition(currentPlayback);
                spotifyServiceProxy.seekToPosition(positionTime);
            }

            @Override
            public void run() {
                if (trackProgressBar.isTracking()) {
                    CurrentPlayback currentPlayback = spotifyServiceProxy.getCachedCurrentPlayback();
                    int positionTime = trackProgressBar.getTimeElapsedForPosition(currentPlayback);
                    DisplayableTime displayableTime = new DisplayableTime(positionTime);
                    musicDisplay.setTime(displayableTime.toString());
                } else if (isTrackingPreviousValue && trackProgressBar.isTracking()) {
                    progressManuallyChanged();
                }
            }
        };

        final ScheduledFuture<?> updaterHandle = trackProgressBarPoller.scheduleAtFixedRate(updater,
                POLLING_INITIAL_DELAY_MILLIS, PROGRESS_POLLING_RATE_MILLIS, TimeUnit.MILLISECONDS);
    }

}
