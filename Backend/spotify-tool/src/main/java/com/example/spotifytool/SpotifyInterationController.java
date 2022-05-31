package com.example.spotifytool;

import com.neovisionaries.i18n.CountryCode;
import net.bytebuddy.implementation.bytecode.Throw;
import se.michaelthelin.spotify.*;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumsTracksRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import se.michaelthelin.spotify.requests.data.browse.GetListOfNewReleasesRequest;
import se.michaelthelin.spotify.requests.data.follow.GetUsersFollowedArtistsRequest;
import se.michaelthelin.spotify.requests.data.library.GetUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.util.*;
import java.util.concurrent.*;
import java.io.IOException;
import java.net.URI;


public final class SpotifyInterationController {
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:4200/spotify-redirect");
    private static SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId("204e30860a96417ca13d62b7e6a2a339")
            .setClientSecret("6a826f0972fa47e89543b7638e743eda")
            .setRedirectUri(redirectUri)
            .build();
    private static AuthorizationCodeRequest authorizationCodeRequest;

    private SpotifyInterationController() {

    }

    public static String authorizationCodeUri_Sync() {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope("user-follow-read playlist-modify-public playlist-modify-private playlist-read-private")
                .build();
        final URI uri = authorizationCodeUriRequest.execute();

        return uri.toString();
    }

    public static void authorizationCode_Sync(AuthorizationCodeRequest authorizationCodeRequest) {
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static String createPlaylist_Sync(String username, String playlistName, boolean isCollaborative, boolean isPublic, String description) throws Exception {
        CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(username, playlistName)
                .collaborative(isCollaborative)
                .public_(isPublic)
                .description(description)
                .build();
        try {
            Playlist playlist = createPlaylistRequest.execute();

            return playlist.getId();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("?Error: " + e.getMessage());
        }
        throw new Exception("Unreachable code in SIC line 80");
    }


    public static void addItemsToPlaylist_Sync(String playlistId, String[] uris) {
        AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi
                .addItemsToPlaylist(playlistId, uris)
                .build();
        try {
            final SnapshotResult snapshotResult = addItemsToPlaylistRequest.execute();
            System.out.println(snapshotResult.getSnapshotId());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static List<String> getArtistsAlbums_Sync(String id) {
        System.out.println("Artist ID: " + id);
        List<String> albumIds = new ArrayList<>();
        GetArtistsAlbumsRequest getArtistsAlbumsRequest = spotifyApi.getArtistsAlbums(id)
                .album_type("album")
                .limit(50)
                .build();
        try {
            Paging<AlbumSimplified> albumSimplifiedPaging = getArtistsAlbumsRequest.execute();

            for (int i = 0; i < albumSimplifiedPaging.getItems().length - 1; i++) {
                albumIds.add(albumSimplifiedPaging.getItems()[i].getId());
            }

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return albumIds;
    }


    public static Map<String, Map<String, Object>> getUsersFollowedArtists_Sync() {
        Map<String, Map<String, Object>> artistMap = new HashMap<>();
        GetUsersFollowedArtistsRequest getUsersFollowedArtistsRequest = spotifyApi
                .getUsersFollowedArtists(ModelObjectType.ARTIST)
                .limit(50)
                .build();
        try {
            PagingCursorbased<Artist> artistPagingCursorbased = getUsersFollowedArtistsRequest.execute();
            Cursor[] cursors = artistPagingCursorbased.getCursors();
            //split the cursor to get the last artist retrieved, recall with last artist as seed.
            String[] after = Arrays.toString(artistPagingCursorbased.getCursors()).split("[=)]", 0);
            while (!after[1].equals("null")) {
                for (int i = 0; i < artistPagingCursorbased.getItems().length - 1; i++) {
                    Map<String, Object> propertyMap = new HashMap<>();
                    if (artistPagingCursorbased.getItems()[i].getImages().length > 0)
                        propertyMap.put("images", artistPagingCursorbased.getItems()[i].getImages()[artistPagingCursorbased.getItems()[i].getImages().length - 1]);
                    propertyMap.put("popularity", artistPagingCursorbased.getItems()[i].getPopularity());
                    propertyMap.put("id", artistPagingCursorbased.getItems()[i].getId());
                    propertyMap.put("genres", artistPagingCursorbased.getItems()[i].getGenres());
                    artistMap.put(artistPagingCursorbased.getItems()[i].getName(), propertyMap);
                }
                getUsersFollowedArtistsRequest = spotifyApi
                        .getUsersFollowedArtists(ModelObjectType.ARTIST)
                        .after(after[1])
                        .limit(50)
                        .build();
                artistPagingCursorbased = getUsersFollowedArtistsRequest.execute();
                after = Arrays.toString(artistPagingCursorbased.getCursors()).split("[=)]", 0);
            }


        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return artistMap;
    }


    public static User getCurrentUsersProfile_Sync() throws Exception {
        GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile()
                .build();
        try {
            final User user = getCurrentUsersProfileRequest.execute();

            return user;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        throw new Exception("Unreachable Line 135 in SIC");
    }

    public static List<String> getAlbumsTracks_Sync(String id) throws InterruptedException {
        System.out.println("Album ID: " + id);
        List<String> trackUris = new ArrayList<>();
        GetAlbumsTracksRequest getAlbumsTracksRequest = spotifyApi.getAlbumsTracks(id)
                .limit(50)
                .build();

        try {
            Paging<TrackSimplified> trackSimplifiedPaging = getAlbumsTracksRequest.execute();
            if (trackSimplifiedPaging.getItems().length == 1) {
                trackUris.add(trackSimplifiedPaging.getItems()[0].getUri());
                System.out.println("Single Track Name: " + trackSimplifiedPaging.getItems()[0].getName());
            } else {
                for (int i = 0; i < trackSimplifiedPaging.getItems().length - 1; i++) {
                    trackUris.add(trackSimplifiedPaging.getItems()[i].getUri());
                    System.out.println("Album Track Name: " + trackSimplifiedPaging.getItems()[i].getName());
                }
            }
            System.out.println(Objects.nonNull(trackSimplifiedPaging.getNext()));
            if (Objects.nonNull(trackSimplifiedPaging.getNext())) {
                String next = trackSimplifiedPaging.getNext();
                while ((Objects.nonNull(next))) {
                    System.out.println("Size is: " + trackSimplifiedPaging.getItems().length);
                    for (int i = 0; i < trackSimplifiedPaging.getItems().length - 1; i++) {
                        trackUris.add(trackSimplifiedPaging.getItems()[i].getUri());
                        System.out.println("Track Name: " + trackSimplifiedPaging.getItems()[i].getName() + " Artist Name: " + trackSimplifiedPaging.getItems()[i].getArtists()[0]);
                    }
                    getAlbumsTracksRequest = spotifyApi.getAlbumsTracks(id)
                            .setPath(next)
                            .limit(50)
                            .build();
                    next = trackSimplifiedPaging.getNext();
                    System.out.println(next);
                }
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());

        }
        return trackUris;
    }

    public static void getUsersSavedTracks_Sync() {
        GetUsersSavedTracksRequest getUsersSavedTracksRequest = spotifyApi.getUsersSavedTracks()
                .limit(10)
                .offset(0)
                .build();
        try {
            final Paging<SavedTrack> savedTrackPaging = getUsersSavedTracksRequest.execute();
            System.out.println("Total: " + savedTrackPaging.getTotal());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void createPlaylistOfLowPopularityArtists(String uid) throws Exception {
        final int MAX_URI_ARRAY_SIZE = 50;
        Map<String, Map<String, Object>> lpaMap = new HashMap<>();
        Map<String, Map<String, Object>> artistsMap = getUsersFollowedArtists_Sync();
        List<String> lpaAlbumIds = new ArrayList<>();
        List<String> lpaTrackUris = new ArrayList<>();
        //get artists with popularity below a certain value
        artistsMap.forEach((k, v) -> {
            if ((Integer) v.get("popularity") < 10) {
                lpaMap.put(k, v);
            }
        });

        lpaMap.forEach((k, v) -> {
            lpaAlbumIds.addAll(getArtistsAlbums_Sync(v.get("id").toString()));
        });
        lpaAlbumIds.forEach(id -> {
            try {
                lpaTrackUris.addAll(getAlbumsTracks_Sync(id));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        String lpaPlaylistId = createPlaylist_Sync(uid, "Low Popularity Artists Playlist", false, true, "A playlist of your followed artists with less than 10 popularity points (according to spotify metrics)");
        System.out.println("Playlist Name: " + lpaPlaylistId);

        System.out.println("Tracks Size: " + lpaTrackUris.size());
        if (lpaTrackUris.size() > MAX_URI_ARRAY_SIZE) {
            String[] formattedEphemeralList = new String[MAX_URI_ARRAY_SIZE];
            int i = 0;
            int j = 0;
            for (i = 0; i < lpaTrackUris.size() - 1; i++) {
                System.out.println("i: " + i + " j: " + j);
                formattedEphemeralList[j] = lpaTrackUris.get(i);
                j++;
                if (i == MAX_URI_ARRAY_SIZE - 1 || j - 1 == MAX_URI_ARRAY_SIZE - 1) {
                    System.out.println(Arrays.toString(formattedEphemeralList));
                    addItemsToPlaylist_Sync(lpaPlaylistId, formattedEphemeralList);
                    if (lpaTrackUris.size() - i < MAX_URI_ARRAY_SIZE) {
                        formattedEphemeralList = new String[lpaTrackUris.size() - i - 1];
                    } else {
                        formattedEphemeralList = new String[MAX_URI_ARRAY_SIZE];
                    }
                    j = 0;
                }

            }
        } else {
            String[] formattedEphemeralList = new String[lpaTrackUris.size() - 1];
            int i = 0;
            for (i = 0; i < lpaTrackUris.size() - 1; i++) {
                System.out.println("i: " + i);
                formattedEphemeralList[i] = lpaTrackUris.get(i);

            }
            System.out.println(Arrays.toString(formattedEphemeralList));
            addItemsToPlaylist_Sync(lpaPlaylistId, formattedEphemeralList);
        }
    }

    public static Map<String, Object> setAuthCode(String authToken) throws Exception {
        Map<String, Object> loginReturnObj = new HashMap<>();
        authorizationCodeRequest = spotifyApi.authorizationCode(authToken).build();
        authorizationCode_Sync(authorizationCodeRequest);
        User user = getCurrentUsersProfile_Sync();
        loginReturnObj.put("Username", user.getDisplayName());
        loginReturnObj.put("Profile Picture", user.getImages()[0].getUrl());
        loginReturnObj.put("UID", user.getId());
        loginReturnObj.put("Refresh Token", spotifyApi.getRefreshToken());
        createPlaylistOfLowPopularityArtists(user.getId());

        return loginReturnObj;
    }

//    public static void reduceArtistUris(List<String> uris) {
//        int size = lpaTrackUris.size();
//        if (size > 100) {
//            while (size > 100) {
//                List<String> ephemeralList = lpaTrackUris.subList(0, 99);
//                lpaTrackUris.subList(0, 99).clear();
//                size = lpaTrackUris.size();
//                String[] formattedEphemeralList = ephemeralList.toArray(ephemeralList.toArray(new String[0]));
//                addItemsToPlaylist_Sync(lpaPlaylistId, formattedEphemeralList);
//            }
//            String[] trackUris = lpaTrackUris.toArray(new String[0]);
//            addItemsToPlaylist_Sync(lpaPlaylistId, trackUris);
//        } else {
//            String[] trackUris = lpaTrackUris.toArray(new String[0]);
//            addItemsToPlaylist_Sync(lpaPlaylistId, trackUris);
//        }
//
//    }


}
