import { UserService } from "src/services/user.service";

export class user {

    constructor(
        public username: string,
        public password: string,
        public passwordConfirmation?: string,
        public isSignedIn?: boolean,
        private accessToken?: string,
        private imageUrl?: string,
        private spotifyUsername?: string,
        private spotifyUID?: string,
        private userID?:string
    ) { }

    setAccessToken(accessToken) {
        this.accessToken = accessToken;
    }
    getAccessToken(): string {
        return this.accessToken;
    }
    setSpotifyUsername(spotifyUsername) {
        this.spotifyUsername = spotifyUsername;
    }
    getSpotifyUsername(): string {
        return this.spotifyUsername;
    }
    setImageURL(imageURL){
        this.imageUrl = imageURL
    }
    getImageURL(): string{
        return this.imageUrl
    }
    setSpotifyUID(spotifyUID){
        this.spotifyUID = spotifyUID;
    }
    getSpotifyUID(): string{
        return this.spotifyUID;
    }
    setUserID(userId){
         this.userID = userId;
    }
    getUserID(): string{
        return this.userID;
    }
    
    

}