package Lampemm.Service.Display;

import Lampemm.Model.CurrentPlayback;
import Lampemm.Model.DisplayableTime;

/**
 * Created by Mark on 8/11/17.
 */
public interface MusicDisplay {
    void setDisplayForCurrentPlayback(CurrentPlayback currentPlayback);

    void setDisplayForCurrentPlayback(final String title, final String artist, final DisplayableTime displayableTime);

    void setStatus(String status);

    void setTitle(String title);

    void setArtist(String artist);

    void setTime(String displayableTimeElapsed);
}
