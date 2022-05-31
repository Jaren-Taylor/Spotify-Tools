import { Injectable, OnChanges, SimpleChanges } from '@angular/core';
import { Subject, BehaviorSubject, Observable } from 'rxjs';
import { user } from 'src/app/user';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private user: user;

  assignUser(user: user) {
    this.user = user;
    this.saveUserToLocalStorage();
  }

  getUser(): user {
    return this.user;
  }

  // saveAllProperties(user) {
  //   if (this.user['accessToken'] !== undefined) {
  //     this.userSubject.getValue()['accessToken'] = this.user['accessToken'];
  //   }
  //   Object.assign(this.user, user);

  // }

  saveUserToLocalStorage() {
    window.sessionStorage.setItem("user", JSON.stringify(this.user));
  }
   getUserFromLocalStorage(): user {
    let u = JSON.parse(window.sessionStorage.getItem("user"))
    this.user = new user(u.username, u.password, u.passwordConfirmation, u.isSignedIn, u.accessToken, u.imageUrl, u.spotifyUsername, u.spotifyUID, u.userID);
    return this.user;
  }

  dismissUser() {
    this.user = undefined;
  }

  setAccessToken(accessToken) {
    this.user.setAccessToken(accessToken);
  }
  getAccessToken(): string {
    return this.user.getAccessToken();
  }
  setSpotifyUsername(spotifyUsername) {
    this.user.setSpotifyUsername(spotifyUsername);
  }
  getSpotifyUsername(): string {
    return this.user.getSpotifyUsername();
  }
  setImageURL(imageURL) {
    this.user.setImageURL(imageURL);
  }
  getImageURL(): string {
    return this.user.getImageURL();
  }
  setSpotifyUID(spotifyUID) {
    this.user.setSpotifyUID(spotifyUID);
  }
  getSpotifyUID(): string {
    return this.user.getSpotifyUID();
  }
  setUserID(userId) {
    this.user.setUserID(userId);
  }
  getUserID(): string {
    return this.user.getUserID();
  }

  // TODO - Fix logic here to replace Object Assign, otherwise some properties may experience lossy behaviour.
  // saveAllProperties(){
  //   for(let prop in this.userSubject.getValue()){
  //     if ((this.user[prop] !== undefined && this.user[prop] !== null) && (this.userSubject[prop] === undefined || this.userSubject[prop] === null)){
  //       console.log("prop is: ", prop);
  //       console.log("prop on user is: ", this.user[prop]);
  //       console.log("prop on subject is: ", this.userSubject.getValue[prop]);
  //       this.userSubject.getValue()[prop] = this.user[prop] 
  //    }
  //   }

}




