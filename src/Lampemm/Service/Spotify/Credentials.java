package Lampemm.Service.Spotify;

import Lampemm.Lampemm;
import com.google.common.collect.Lists;
import com.sun.org.apache.regexp.internal.RE;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.RefreshAccessTokenCredentials;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Mark on 8/11/17.
 */
public class Credentials {
    private static final Credentials instance = new Credentials();

    private final static String CLIENT_ID = "0e54da7537af4eb19f300aa19bc21bd3";
    private final static String REDIRECT_URI = "https://seattlelofts.tumblr.com/";
    private final static String SECRET_PATH = "/spotify/secret";
    private final static String CODE_PATH = "/spotify/code";
    private final static String ACCESS_TOKEN_PATH = "/spotify/accesstoken";
    private final static String REFRESH_TOKEN_PATH = "/spotify/refreshtoken";
    private final List<String> scopes = Lists.newArrayList("user-read-playback-state", "user-modify-playback-state");

    private Credentials() {
    }

    public static Credentials getInstance() {
        return instance;
    }

    public String getClientId() {
        return CLIENT_ID;
    }

    public String getRedirectUri() {
        return REDIRECT_URI;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public String getSecret() {
        // Todo: add status to display if fails
        return getFirstLineOfFile(SECRET_PATH).get();
    }

    public String getCode() {
        // Todo: add status to display if fails
        return getFirstLineOfFile(CODE_PATH).orElse("");
    }

    public String getAccessToken() {
        return getFirstLineOfFile(ACCESS_TOKEN_PATH).orElse("");
    }

    public String getRefreshToken() {
        return getFirstLineOfFile(REFRESH_TOKEN_PATH).orElse("");
    }

    public Optional<String> getFirstLineOfFile(String filePath) {
        Path path = FileSystems.getDefault().getPath(filePath);
        String firstLine;
        try {
            firstLine = Files.lines(path, StandardCharsets.UTF_8).findFirst().orElseThrow(RuntimeException::new);
        } catch (IOException | RuntimeException ex) {
            System.err.print("Problem reading given file at path, returning null:"+filePath);
            firstLine = null;
        }
        return Optional.ofNullable(firstLine);
    }

    public void writeRefreshAccessTokenCredentials(RefreshAccessTokenCredentials refreshAccessTokenCredentials) {
        writeFirstLineOfFile(ACCESS_TOKEN_PATH, refreshAccessTokenCredentials.getAccessToken());
    }

    public void writeAuthorizationCodeCredentials(AuthorizationCodeCredentials authorizationCodeCredentials) {
        writeFirstLineOfFile(ACCESS_TOKEN_PATH, authorizationCodeCredentials.getAccessToken());
        writeFirstLineOfFile(REFRESH_TOKEN_PATH, authorizationCodeCredentials.getRefreshToken());
    }

    public void clearAuthorizationCodeCredentials() {
        clearFile(CODE_PATH);
        clearFile(ACCESS_TOKEN_PATH);
        clearFile(REFRESH_TOKEN_PATH);
    }

    public void clearFile(String filePath) {
        writeFirstLineOfFile(filePath, "");
    }

    public void writeFirstLineOfFile(String filePath, String line) {
        try {
            List<String> lines = Arrays.asList(line);
            Path file = Paths.get(filePath);
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
