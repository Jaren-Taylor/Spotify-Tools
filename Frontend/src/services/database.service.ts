import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable()
export class databaseService {

    constructor(private http: HttpClient) { }

    trySignIn(user) {
        return this.http.post(environment.apiUrls.signIn, user, {observe : 'response'});
    }

    tryRegister(user) {
        return this.http.post(environment.apiUrls.registerUser, user, {observe : 'response'});
    }

    tryAuthorize() {
        return this.http.get(environment.apiUrls.authorizeUser, {responseType : 'text'});
    }

    setAccessToken(authCode) {
        return this.http.post(environment.apiUrls.setAuthCode,authCode, {observe : 'response'});
    }

}