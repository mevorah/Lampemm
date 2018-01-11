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
                boolean isCurrentlyTracking = trackProgressBar.isTracking();
                if (isCurrentlyTracking) {
                    wasTrackingPreviously = true;
                    CurrentPlayback currentPlayback = spotifyServiceProxy.getCachedCurrentPlayback();
                    if (currentPlayback != null) {
                        int positionTime = trackProgressBar.getTimeElapsedForPosition(currentPlayback);
                        DisplayableTime displayableTime = new DisplayableTime(positionTime);
                        musicDisplay.setTime(displayableTime.toString());
                    }
                } else if (wasTrackingPreviously && !isCurrentlyTracking) {
                    System.out.println("Going to make a seek request");
                    wasTrackingPreviously = false;

                    // Ensure no seek request is going on, and ensure everything is updated from the last request
                    boolean hasRetrievedPlaybackAfterLastSeek = spotifyServiceProxy.getHasRetrievedPlaybackAfterLastSeek();
                    System.out.println("hasRetrievedPlaybackAfterLastSeek:" + hasRetrievedPlaybackAfterLastSeek);
                    if (hasRetrievedPlaybackAfterLastSeek) {
                        // Get cached, only concerned with the song duration. We don't
                        // need to go through the trouble of making a new request
                        final CurrentPlayback currentPlayback = spotifyServiceProxy.getCachedCurrentPlayback();
                        final int positionTime = trackProgressBar.getTimeElapsedForLastTrackingEvent(currentPlayback);
                        System.out.println("Seeking to last tracking event:"+positionTime);
                        spotifyServiceProxy.seekToPosition(positionTime);
                    }
                }
            }
        };

        final ScheduledFuture<?> updaterHandle = trackProgressBarPoller.scheduleAtFixedRate(updater,
                POLLING_INITIAL_DELAY_MILLIS, PROGRESS_POLLING_RATE_MILLIS, TimeUnit.MILLISECONDS);
    }

}
