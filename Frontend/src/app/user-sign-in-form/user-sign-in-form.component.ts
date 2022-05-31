import { Component, OnDestroy, OnInit } from '@angular/core';
import { user } from '../user';
import { databaseService } from 'src/services/database.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from 'src/services/user.service';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-sign-in-form',
  templateUrl: './user-sign-in-form.component.html',
  styleUrls: ['./user-sign-in-form.component.css']
})
export class UserSignInFormComponent implements OnInit{

  user = new user(null, null, null, null, null);
  isRegister: boolean;
  signInSucceeded: boolean;
  isUserRegistered: boolean;
  constructor(private restService: databaseService, private userService:UserService, private router: Router) { 
  }
  ngOnInit(): void {
    if(window.sessionStorage.getItem("user") !== null ){
     this.redirectToUserPage();
    }else{
    console.log("user is ",this.userService.getUser());
    console.log("User session storage user is ",window.sessionStorage.getItem("user"));
  }
  }

  onSubmit(user) {
    this.TrySignIn(user);
  }
  overWriteUser() {
    this.user = new user(null, null, null, null, null);
  }

  switchForm() {
    this.isRegister = !this.isRegister;
    this.clearInput();

  }

  TrySignIn(userX) {
    this.restService.trySignIn(userX).subscribe(res => {
      this.isUserRegistered = res.body['isUserRegistered'];
      this.signInSucceeded = res.body['signInSucceeded'];
      if(res.body['signInSucceeded']){
      this.setUser(res.body);
      this.redirectToUserPage();
      }else{
        this.signInSucceeded = false;
      }
    });
  }

  tryRegisterUser(user) {
    this.restService.tryRegister(user).subscribe(res => {
      this.isUserRegistered = res.body['isUserRegistered'];
      this.signInSucceeded = res.body['signInSucceeded'];
      if(res.body['signInSucceeded']){
      this.setUser(res.body);
      this.redirectToUserPage();
      }else{
        this.signInSucceeded = false;
      }
    });

  }
  setUser(userX){
    this.userService.assignUser(new user(userX['username'], userX['password'], null, true, userX['credentials']))
    this.userService.setUserID("testUser");
    this.userService.assignUser(this.userService.getUser());

  }

  clearInput() {
    this.overWriteUser();
    this.signInSucceeded = false;
    this.isUserRegistered = true;
  }

  redirectToUserPage(){
    this.router.navigate(['/user']);
  }
}
     
