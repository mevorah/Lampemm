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
            private boolean wasTrackingPreviously = false;

            @Override
            public void run() {
                try {
                    boolean isCurrentlyTracking = trackProgressBar.isTracking();
                    if (isCurrentlyTracking) {
                        wasTrackingPreviously = true;
                        CurrentPlayback currentPlayback = spotifyServiceProxy.getCachedCurrentPlayback();
                        if (currentPlayback != null) {
                            int positionTime = trackProgressBar.getTimeElapsedForPosition(currentPlayback);
                            DisplayableTime displayableTime = new DisplayableTime(positionTime);
                            System.out.println("[Poster] SettingTime:"+displayableTime);
                            musicDisplay.setDisplayForCurrentPlayback(currentPlayback.getTitle(), currentPlayback.getArtists(), displayableTime);
                        }
                    } else if (wasTrackingPreviously && !isCurrentlyTracking) {
                        wasTrackingPreviously = false;

                        // Get cached, only concerned with the song duration. We don't
                        // need to go through the trouble of making a new request
                        final CurrentPlayback currentPlayback = spotifyServiceProxy.getCachedCurrentPlayback();
                        final int targetPositionTime = trackProgressBar.getTimeElapsedForLastTrackingEvent(currentPlayback);
                        System.out.println("Seeking to last tracking event:"+targetPositionTime);

                        // Seek to position, if seek to position already in flight, overwrite with
                        // new request
                        spotifyServiceProxy.seekToPosition(targetPositionTime);

                    }
                } catch (Exception e) {
                    System.err.println("Unhandled exception thrown from poster thread:" + e.getMessage());
                }
            }
        };

        final ScheduledFuture<?> updaterHandle = trackProgressBarPoller.scheduleAtFixedRate(updater,
                POLLING_INITIAL_DELAY_MILLIS, PROGRESS_POLLING_RATE_MILLIS, TimeUnit.MILLISECONDS);
    }

}
