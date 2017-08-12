package Lampemm;

import Lampemm.Service.Controls.TrackProgressBar;
import Lampemm.Service.Display.TwoLineDisplay;
import Lampemm.Service.Spotify.SpotifyServiceProxy;

/**
 * Created by Mark on 8/11/17.
 */
public class Lampemm {
    public static void main(String [] args) {
        SpotifyPoller spotifyPoller = new SpotifyPoller(SpotifyServiceProxy.getInstance(),
                TrackProgressBar.getInstance(), TwoLineDisplay.getInstance());
        spotifyPoller.start();
//        SpotifyPoster spotifyPoster = new SpotifyPoster();
//        spotifyPoster.start();
    }
}
