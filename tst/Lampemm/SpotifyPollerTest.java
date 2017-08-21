package Lampemm;

import Lampemm.Service.Controls.TrackProgressBar;
import Lampemm.Service.Display.TwoLineDisplay;
import Lampemm.Service.Spotify.SpotifyServiceProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Any;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Mark on 8/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class SpotifyPollerTest {

    @Mock
    private SpotifyServiceProxy spotifyServiceProxy;

    @Mock
    private TrackProgressBar trackProgressBar;

    @Mock
    private TwoLineDisplay twoLineDisplay;

    private SpotifyPoller spotifyPoller;

    @Before
    public void before() {
        System.out.println("hello");
        spotifyPoller = new SpotifyPoller(spotifyServiceProxy, trackProgressBar, twoLineDisplay);
    }



}
