package Lampemm.Service.Display;

import Lampemm.Model.CurrentPlayback;
import Lampemm.Model.DisplayableTime;

/**
 * Created by Mark on 8/11/17.
 */
public class TwoLineDisplay implements MusicDisplay {

    private static final TwoLineDisplay instance = new TwoLineDisplay();

    private TwoLineDisplay() {}

    public static TwoLineDisplay getInstance() {
        return instance;
    }

    public void setDisplayForCurrentPlayback(CurrentPlayback currentPlayback) {
        setTitle(currentPlayback.getTitle());
        setArtist(currentPlayback.getArtists());
        setTime(new DisplayableTime(currentPlayback.getTimeElapsed()).toString());
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setArtist(String artist) {

    }

    @Override
    public void setTime(String displayableTimeElapsed) {

    }
}
