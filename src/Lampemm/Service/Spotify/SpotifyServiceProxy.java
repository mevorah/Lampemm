package Lampemm.Service.Spotify;

import Lampemm.Model.BufferedValue;
import Lampemm.Model.CurrentPlayback;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.AbstractRequest;
import com.wrapper.spotify.methods.CurrentPlaybackRequest;
import com.wrapper.spotify.methods.SeekToPositionRequest;
import com.wrapper.spotify.methods.authentication.RefreshAccessTokenRequest;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.RefreshAccessTokenCredentials;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.lang.System.exit;

/**
 * Created by Mark on 8/11/17.
 */
public class SpotifyServiceProxy {
    private final ScheduledExecutorService refreshTokenScheduler = Executors.newSingleThreadScheduledExecutor();
    private final long POLLING_INITIAL_DELAY_MILLIS = 0;
    private final long REFRESH_TOKEN_POLLING_RATE_MILLIS = 1000 * 60; // Refresh every minute

    private static final SpotifyServiceProxy instance = new SpotifyServiceProxy(Credentials.getInstance());
    private final Api spotify;
    private final Credentials credentials;

    private CurrentPlayback cachedCurrentPlayback;

    private boolean seekRequestInProgress = false;
    boolean wasSeekingForward = true;
    int durationBeforeSeek = -1;
    int lastSeekingTarget = -1;
    int positionBeforeSeek = -1;
    boolean isCurrentPositionInSyncWithLastSeek = true;

    private SpotifyServiceProxy(Credentials credentials) {
        this.credentials = credentials;

        this.spotify = Api.builder().
                accessToken(null).
                clientId(credentials.getClientId()).
                clientSecret(credentials.getSecret()).
                redirectURI(credentials.getRedirectUri()).build();

        // todo: change logic so that if code is missing, a tiny url is displayed
        if (Strings.isNullOrEmpty(credentials.getCode())) {
            String authorizeUrl = spotify.createAuthorizeURL(credentials.getScopes(), "none");
            System.out.println(authorizeUrl);
        }

        if (!Strings.isNullOrEmpty(credentials.getAccessToken()) &&
                !Strings.isNullOrEmpty(credentials.getRefreshToken())) {
            // If credentials exist in the file, use them
            spotify.setAccessToken(credentials.getAccessToken());
            spotify.setRefreshToken(credentials.getRefreshToken());
        } else {
            try {
                // If credentials don't ex
                AuthorizationCodeCredentials authorizationCodeCredentials = spotify.authorizationCodeGrant(credentials.getCode()).build().get();
                credentials.writeAuthorizationCodeCredentials(authorizationCodeCredentials);
                spotify.setAccessToken(authorizationCodeCredentials.getAccessToken());
                spotify.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            } catch (IOException | WebApiException ex) {
                ex.printStackTrace();
                // Credentials aren't working, clear credentials and generate again and put in a file
                credentials.clearAuthorizationCodeCredentials();
                String authorizeUrl = spotify.createAuthorizeURL(credentials.getScopes(), "none");
                System.out.println(authorizeUrl);
                exit(1);
            }
        }

        startTokenRefresh();
    }

    public static SpotifyServiceProxy getInstance() {
        return instance;
    }

    public void startTokenRefresh() {
        final Runnable updater = new Runnable() {
            @Override
            public void run() {
                try {
                    RefreshAccessTokenRequest refreshAccessTokenRequest = spotify.refreshAccessToken().build();
                    RefreshAccessTokenCredentials refreshAccessTokenCredentials = refreshAccessTokenRequest.get();
                    spotify.setAccessToken(refreshAccessTokenCredentials.getAccessToken());
                    credentials.writeRefreshAccessTokenCredentials(refreshAccessTokenCredentials);
                    System.out.println("Successfuly refreshed token");
                } catch (IOException | WebApiException ex) {
                    ex.printStackTrace();
                    System.err.println("Refresh credentials failed, will wait for the next one");
                }
            }
        };

        final ScheduledFuture<?> updaterHandle = refreshTokenScheduler.scheduleAtFixedRate(updater,
                POLLING_INITIAL_DELAY_MILLIS, REFRESH_TOKEN_POLLING_RATE_MILLIS, TimeUnit.MILLISECONDS);
    }

    /**
     * Call Spotify to get the user's current playback
     * @return
     */
    public CurrentPlayback getCurrentPlayback() {
        CurrentPlaybackRequest currentPlaybackRequest = spotify.currentPlayback().build();
        CurrentPlayback currentPlayback;
        try {
            currentPlayback = new CurrentPlayback(currentPlaybackRequest.get());
            cachedCurrentPlayback = currentPlayback;
        } catch (IOException | WebApiException ex) {
            System.err.println("Failed retrieve current playback, deffering to the result of the last " +
                    "CurrentPlayback request:"+ ex.getMessage());
            currentPlayback = cachedCurrentPlayback;
        }

        return currentPlayback;
    }

    /**
     * Get the CurrentPlayback of the last getCurrentPlaybackRequest
     * @return
     */
    public CurrentPlayback getCachedCurrentPlayback() {
        return cachedCurrentPlayback;
    }

    /**
     * Return if there is currenty a seek request in progress
     * @return
     */
    public boolean getIsSeekToPositionRequestInProgress() {
        return seekRequestInProgress;
    }

    /**
     * After a seek request has occurred, returns whether the most recent CurrentPlayback has become in sync
     * with the seek. This addresses an issue where even after a seek request has occurred, a getCurrentPlayback
     * request still returns an old value.
     * @return
     */
    public boolean isCurrentPositionInSyncWithLastSeek() {
        if (!isCurrentPositionInSyncWithLastSeek) {
            int currentPlaybackTimeElapsed = cachedCurrentPlayback.getTimeElapsed();
            BufferedValue songDuration = new BufferedValue(durationBeforeSeek, 2000);
            if (songDuration.isWithinBoundsInclusive(lastSeekingTarget)) {
                isCurrentPositionInSyncWithLastSeek = songDuration.isWithinBoundsInclusive(currentPlaybackTimeElapsed) || currentPlaybackTimeElapsed < 2000;
            } else if (wasSeekingForward) {
                isCurrentPositionInSyncWithLastSeek = currentPlaybackTimeElapsed > lastSeekingTarget;
            } else {
                isCurrentPositionInSyncWithLastSeek = currentPlaybackTimeElapsed < positionBeforeSeek && currentPlaybackTimeElapsed > lastSeekingTarget;
            }
        }
        return isCurrentPositionInSyncWithLastSeek;
    }

    /**
     * Call Spotify to move user's poition to the targetPositionMillis
     * @param targetPositionMillis
     */
    public void seekToPosition(int targetPositionMillis) {
        seekRequestInProgress = true;

        // Before seek occurs, set values for the currentInSync function above
        durationBeforeSeek = cachedCurrentPlayback.getDuration();
        positionBeforeSeek = cachedCurrentPlayback.getTimeElapsed() ;
        lastSeekingTarget = targetPositionMillis;
        wasSeekingForward = targetPositionMillis > positionBeforeSeek;
        isCurrentPositionInSyncWithLastSeek = false;

        SeekToPositionRequest seekToPositionRequest = spotify.seekToPosition(targetPositionMillis).build();
        try {
            seekToPositionRequest.get();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebApiException e) {
            e.printStackTrace();
        }

        seekRequestInProgress = false;
    }

}
