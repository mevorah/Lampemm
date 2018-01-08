package Lampemm;

import Lampemm.Model.CurrentPlayback;
import Lampemm.Service.Controls.TrackProgressBar;
import Lampemm.Service.Display.MusicDisplay;
import Lampemm.Service.Spotify.SpotifyServiceProxy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Responsible for polling spotify playback and
 * updating peripheral devices.
 */
public class SpotifyPoller {
    private final ScheduledExecutorService playbackScheduler = Executors.newSingleThreadScheduledExecutor();
    private final long POLLING_INITIAL_DELAY_MILLIS = 0;
    private final long PLAYBACK_POLLING_RATE_MILLIS = 500;

    private final SpotifyServiceProxy spotifyServiceProxy;
    private final TrackProgressBar trackProgressBar;
    private final MusicDisplay musicDisplay;

    public SpotifyPoller (SpotifyServiceProxy spotifyServiceProxy,
                          TrackProgressBar trackProgressBar,
                          MusicDisplay musicDisplay) {
        this.spotifyServiceProxy = spotifyServiceProxy;
        this.trackProgressBar = trackProgressBar;
        this.musicDisplay = musicDisplay;
    }

    /**
     * Begin continously polling and updating relevant
     * hardware (progress bar and display)
     */
    public void start () {
        final Runnable updater = new Runnable() {
            @Override
            public void run() {
                CurrentPlayback currentPlayback = spotifyServiceProxy.getCurrentPlayback();
                if (!trackProgressBar.isTracking()) {
                    trackProgressBar.setPositionForTimeElapsed(currentPlayback);
                    musicDisplay.setDisplayForCurrentPlayback(currentPlayback);
                }
            }
        };

        final ScheduledFuture<?> updaterHandle = playbackScheduler.scheduleAtFixedRate(updater,
                POLLING_INITIAL_DELAY_MILLIS, PLAYBACK_POLLING_RATE_MILLIS, TimeUnit.MILLISECONDS);
    }

}
