package Lampemm.Model;

import com.wrapper.spotify.models.Artist;

/**
 * Created by Mark on 8/12/17.
 */
public class CurrentPlayback {

    com.wrapper.spotify.models.CurrentPlayback currentPlayback;

    public CurrentPlayback(com.wrapper.spotify.models.CurrentPlayback currentPlayback) {
        this.currentPlayback = currentPlayback;
    }

    /**
     * Returns the artist for the current track
     * @return
     */
    public String getArtists() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String artist : currentPlayback.getArtist()) {
            stringBuilder.append(artist + " ");
        }
        return stringBuilder.toString();
    }

    /**
     * Returns the title for the current track
     * @return
     */
    public String getTitle() {
        return currentPlayback.getTitle();
    }

    /**
     * Returns the amount of time elapsed for the current track
     * in milliseconds
     * @return
     */
    public int getTimeElapsed() {
        return currentPlayback.getTimeElapsed();
    }

    /**
     * Returns the duration of the current track in milliseconds
     * @return
     */
    public int getDuration() {
        return currentPlayback.getDuration();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Title:" + getTitle() + "\n");
        stringBuilder.append("Artist:" + getArtists() + "\n");
        stringBuilder.append("Time:" + getTimeElapsed() + "/" + getDuration());
        return stringBuilder.toString();
    }
}
