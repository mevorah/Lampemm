package Lampemm.Service.Spotify;

import com.wrapper.spotify.methods.AbstractRequest;
import com.wrapper.spotify.methods.CurrentPlaybackRequest;

/**
 * Created by Mark on 8/12/17.
 */
public class SeekToPositionRequest extends AbstractRequest {

    private static final String endpoint = "/v1/me/player/seek";

    public SeekToPositionRequest(Builder<?> builder) {
        super(builder);
    }



    public static final class Builder extends AbstractRequest.Builder<SeekToPositionRequest.Builder> {

        public Builder accessToken(String accessToken) {
            return header("Authorization", "Bearer " + accessToken);
        }

        public SeekToPositionRequest build() {
            path(endpoint);
            return new SeekToPositionRequest(this);
        }
    }
}
