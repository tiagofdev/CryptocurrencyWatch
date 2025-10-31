import {Controller} from "../controller/controller";
import {AppState} from "../interface/app-state";
import {Response} from "../interface/response";
import {DataState} from "../interface/data-state.enum";
import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {map, Observable, of, startWith} from "rxjs";
import {catchError} from "rxjs/operators";
import {NgForm} from "@angular/forms";
import {Router, RouterModule} from "@angular/router";
import {Alert} from "../model/alert";
import {Price} from "../model/price";
import {SharedData} from "../interface/sharedData";

@Component({
  selector: 'login',
  templateUrl: './login.component.html'

})
export class LoginComponent {

  constructor(private controller: Controller, private router: Router,
              private sharedData: SharedData) {  }




  goTo(page: any) {
    console.log("child click")
    this.router.navigate(['app-root/create-account']);
  }


  username = "";
  sendUsername() {
    this.sharedData.updateData(this.username);
  }


  onLogin(form: NgForm): void {
    this.controller.login$(form.value.email, form.value.password).subscribe({
      next: (response) => {
        sessionStorage.setItem('token', response.data.results![0]);
        this.username = response.data.results![1];
        this.sendUsername();
        this.router.navigate(['app-root/dashboard']);
      },
      error: (err) => {
        console.error('Login error:', err);
        alert('Login failed!');
      },
    });
  }






}
