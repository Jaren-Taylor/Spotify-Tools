import { AfterViewChecked, AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { databaseService } from 'src/services/database.service';
import { UserService } from 'src/services/user.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./user-page.component.css']
})
//TODO mem-leak possible from not destroying the subscription instance here.
export class UserPageComponent implements AfterViewChecked, OnInit {


  imagePath: string = "";
  spotifyUsername: string = "";

  constructor(private userService: UserService, private databaseService: databaseService, private router: Router) {

  }
  ngAfterViewChecked(): void {
    this.userInit();
  }

  appState(): boolean {
    let state = false;
    typeof (this.userService.getSpotifyUsername()) === "string" ? state = true : state = false;
    return state;
  }

  userHasToken(): boolean {
    if (this.userService.getAccessToken())
      return true;
    return false;
  }
  userInit() {
    this.imagePath = this.userService.getImageURL();
    this.spotifyUsername = this.userService.getSpotifyUsername();
    console.log("user  is: ", this.userService.getUser());
    console.log("user image is: ", this.userService.getImageURL());

  }

  ngOnInit(): void {
    this.userService.getUser() === null || this.userService.getUser() === undefined ? this.checkSessionUser() : this.evaluateCurrUser();
  }
  
   checkSessionUser() {
    if (!(window.sessionStorage.getItem("user") === null || window.sessionStorage.getItem("user") === undefined)) {
      this.userService.getUserFromLocalStorage();
      this.userInit();
    } else {
     this.signOut();
      throw ("unreachable code");
    }

  }

  collapsibleEvent(id) {
    let div = document.getElementById(id);
    let content = <HTMLElement>div.nextElementSibling;
    content.classList.toggle('active');
    if (content.style.display === "block") {
      content.style.display = "none";
    } else {
      content.style.display = "block";
    }
  }
  signOut(){
    this.userService.dismissUser();
    window.sessionStorage.clear();
    this.router.navigate(['/signIn'])
  }
  evaluateCurrUser() {
    if (this.appState() === true) {
      this.userInit();
    }
    else {
      console.log("authorize");
    }
  }

  dismissUser() {
    this.router.navigate(['/signIn']);
  }

  tryAuthorizeUser() {
    this.databaseService.tryAuthorize().subscribe(res => {
      this.userService.saveUserToLocalStorage();
      window.open(res, "_self");
    })
  }


}
