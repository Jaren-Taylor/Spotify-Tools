import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { UserSignInFormComponent } from './user-sign-in-form/user-sign-in-form.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { databaseService } from 'src/services/database.service';
import { UserPageComponent } from './user-page/user-page.component'
import { UserService } from 'src/services/user.service';
import { SpotifyRedirectComponent } from './spotify-redirect/spotify-redirect.component';

@NgModule({
  declarations: [
    AppComponent,
    UserSignInFormComponent,
    UserPageComponent,
    SpotifyRedirectComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterModule.forRoot(
      [
        { path: 'user', component: UserPageComponent },
        { path: 'signIn', component: UserSignInFormComponent },
        { path: 'spotify-redirect', component: SpotifyRedirectComponent },
        {path: '', redirectTo: '/signIn', pathMatch: 'full'}

      ]
    ),
    FormsModule,
    HttpClientModule
  ],
  providers: [databaseService],
  bootstrap: [AppComponent]
})
export class AppModule { }
