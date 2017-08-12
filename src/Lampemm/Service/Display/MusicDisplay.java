package Lampemm.Service.Display;

import Lampemm.Model.CurrentPlayback;

/**
 * Created by Mark on 8/11/17.
 */
public interface MusicDisplay {
    void setDisplayForCurrentPlayback(CurrentPlayback currentPlayback);

    void setTitle(String title);

    void setArtist(String artist);

    void setTime(String displayableTimeElapsed);
}
