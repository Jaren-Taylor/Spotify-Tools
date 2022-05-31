package com.example.spotifytool;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Entity
public class UserEntity {
    @Id
    @GeneratedValue
    Long Id;

    @Column(name = "Username")
    String username;

    @Column(name = "Password")
    String password;

    @Column(name = "Credentials")
    String credentials;

    @Transient
    Boolean signInSucceeded;

    @Transient
    Boolean isValidUserInfo;

    @Transient
    Boolean isUserRegistered;

    @Transient
    String responseInfo;


    public UserEntity() {

    }

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserEntity(String username, String password, String credentials) {
        this.username = username;
        this.password = password;
        this.credentials = credentials;
    }


    public String getCredentials() {
        return credentials;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public Boolean getSignInSucceeded() {
        return signInSucceeded;
    }

    public Boolean getIsValidUserInfo() {
        return isValidUserInfo;
    }

    public Boolean getIsUserRegistered() {
        return isUserRegistered;
    }


    public @NotNull Boolean IsUserInDatabase(UserRepository userRepository) {
        // System.out.println(userRepository.findByUsername(this.username)); -> returns null if it doesn't exist
        return userRepository.findByUsername(this.username) != null;
    }

    @Override
    public String toString() {
        return this.username + " " + this.password + " " + this.signInSucceeded.toString() + " " + this.credentials + " ";
    }
}
