package Lampemm;

import Lampemm.Model.CurrentPlayback;
import Lampemm.Model.DisplayableTime;
import Lampemm.Service.Controls.TrackProgressBar;
import Lampemm.Service.Display.TwoLineDisplay;
import Lampemm.Service.Spotify.SpotifyServiceProxy;

/**
 * Created by Mark on 8/11/17.
 */
public class SpotifyPoster {

    private final SpotifyServiceProxy spotifyServiceProxy;
    private final TrackProgressBar trackProgressBar;
    private final TwoLineDisplay twoLineDisplay;

    public SpotifyPoster() {
        this.spotifyServiceProxy = SpotifyServiceProxy.getInstance();
        this.trackProgressBar = TrackProgressBar.getInstance();
        this.twoLineDisplay = TwoLineDisplay.getInstance();
    }

    public void start() {
        // TODO: After force detected
        // while force is detected
        CurrentPlayback currentPlayback = spotifyServiceProxy.getCachedCurrentPlayback();
        int positionTime = trackProgressBar.getTimeElapsedForPosition(currentPlayback);
        DisplayableTime displayableTime = new DisplayableTime(positionTime);
        twoLineDisplay.setTime(displayableTime.toString());

        // when force no longer detected:
        progressManuallyChanged();
    }

    /**
     * Triggered callback when the track progress bar's
     * position is manually changed.
     */
    public void progressManuallyChanged() {
        // TODO: Ensure this is called only when the user let's go of the dial - don't want to spam the endpoint
        // TODO: Add test - user is stil tracking even after the song finishes playing - should go to the next song,
        // and tracking should be applied to the new song - below is correct
        CurrentPlayback currentPlayback = spotifyServiceProxy.getCurrentPlayback();
        int positionTime = trackProgressBar.getTimeElapsedForPosition(currentPlayback);
        spotifyServiceProxy.seekToPosition(positionTime);
    }

}
