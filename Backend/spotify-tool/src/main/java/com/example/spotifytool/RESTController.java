package com.example.spotifytool;

import org.apache.coyote.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class RESTController {
    private final UserRepository userRepository;

    public RESTController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/hello")
    public String Hello() {

        System.out.println("works");
        return "API Works";
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/tryRegisterUser")
    public ResponseEntity<UserEntity> TryRegisterUser(@RequestBody UserEntity user) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (!CheckUserIsInDatabase(user)) {
            user.signInSucceeded = true;
            this.userRepository.save(user);
        } else {
            user.signInSucceeded = false;
        }
        user.isUserRegistered = true;
        return new ResponseEntity<UserEntity>(user, httpHeaders, HttpStatus.OK);
    }

    public Boolean CheckUserIsInDatabase(@NotNull UserEntity user) {
        try {
            return user.IsUserInDatabase(this.userRepository) ? true : false;
        } catch (NullPointerException e) {
            return false;
        }

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/tryGetUser")
    public ResponseEntity<UserEntity> TryGetUser(@RequestBody UserEntity user) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (CheckUserIsInDatabase(user)) {
            user.signInSucceeded = true;
            return new ResponseEntity<UserEntity>(user, httpHeaders, HttpStatus.OK);
        }
        user.signInSucceeded = false;
        user.isUserRegistered = false;
        return new ResponseEntity<UserEntity>(user, httpHeaders, HttpStatus.OK);
    }

    //this method will match the pw provided with what is in the database. If its correct, it returns true to the front end, else false.
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/trySignIn")
    public ResponseEntity<UserEntity> TrySignIn(@RequestBody UserEntity user) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            if (this.userRepository.findByUsername(user.username).password.equals(user.password)) {
                user.signInSucceeded = true;
                user.isUserRegistered = true;
                user.isValidUserInfo = true;
                return new ResponseEntity<UserEntity>(user, httpHeaders, HttpStatus.OK);
            } else {
                user.signInSucceeded = false;
                user.isUserRegistered = true;
                user.isValidUserInfo = false;
                return new ResponseEntity<UserEntity>(user, httpHeaders, HttpStatus.OK);
            }

        } catch (NullPointerException e) {
            user.isUserRegistered = false;
            user.signInSucceeded = false;
            user.isValidUserInfo = false;
            return new ResponseEntity<UserEntity>(user, httpHeaders, HttpStatus.OK);
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/tryCreateListOfRecentReleases")
    public String TryCreateListOfRecentReleases(@RequestBody String uid) throws Exception {
        SpotifyInterationController.createPlaylistOfLowPopularityArtists(uid);
        return "test";
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/tryAuthorizeUser")
    public ResponseEntity<String> TryAuthorizeUser() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(SpotifyInterationController.authorizationCodeUri_Sync(), httpHeaders, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/setAuthCode")
    public ResponseEntity<Map<String, Object>> AddCredentials(@RequestBody String authCode) throws Exception {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<Map<String, Object>>(SpotifyInterationController.setAuthCode(authCode), httpHeaders, HttpStatus.OK);
    }
}
