package Lampemm;

import Lampemm.Service.Controls.TrackProgressBar;
import Lampemm.Service.Display.MusicDisplay;
import Lampemm.Service.Display.TwoLineDisplay;
import Lampemm.Service.Spotify.SpotifyServiceProxy;

/**
 * Created by Mark on 8/11/17.
 */
public class Lampemm {
    public static void main(String [] args) {
        /**
         * TODO: 1. Fixed - Added pulsing - Make motor movement smoother, right now very basic - overshooting the target and then moving
         *          backwards. -
         * TODO: 2. Fixed - Added lock - Bug where time is displayed in the upper left corner of the screen
         * TODO: 3. Fixed - Issue where tracking stops altogether, happens randomly when started up (Maybe something to do
         *          with the initial variables/ if conditions in the pollers
         * TODO: 4. Improve tracking detection, occasionally jumps around
         * TODO: 5. Fixed - Time changes to what it was briefly before changing to the new time
         * TODO: 6. Pause -> ////
         * TODO: 7. Fixed - Fix artist disapearing
         *
         */

        // Set display first to have something shown at startup
        MusicDisplay display = TwoLineDisplay.getInstance();
        display.setStatus("Lampemm");

        SpotifyPoller spotifyPoller = new SpotifyPoller(SpotifyServiceProxy.getInstance(),
                TrackProgressBar.getInstance(), display);
        spotifyPoller.start();

        SpotifyPoster spotifyPoster = new SpotifyPoster(SpotifyServiceProxy.getInstance(),
                TrackProgressBar.getInstance(), display);
        spotifyPoster.start();

    }
}
