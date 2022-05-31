import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { databaseService } from 'src/services/database.service';
import { UserService } from 'src/services/user.service';

@Component({
  selector: 'app-spotify-redirect',
  templateUrl: './spotify-redirect.component.html',
  styleUrls: ['./spotify-redirect.component.css']
})
export class SpotifyRedirectComponent implements OnInit {

  constructor(private activatedRoute: ActivatedRoute, private userService: UserService, private router: Router, private databaseService: databaseService) {

  }

  ngOnInit(): void {
    this.userService.getUserFromLocalStorage();
    this.activatedRoute.queryParams.subscribe(params => {
      this.userService.setAccessToken(params.code);
      this.databaseService.setAccessToken(this.userService.getAccessToken()).subscribe(res => {
        this.userService.setAccessToken(res.body["Refresh Token"]);
        this.userService.setImageURL(res.body["Profile Picture"]);
        this.userService.setSpotifyUID(res.body["UID"]);
        this.userService.setSpotifyUsername(res.body["Username"]);
        this.userService.saveUserToLocalStorage();
      });
    });
    this.router.navigate(['/user'])
  }

}
