import {Controller} from "../controller/controller";
import {AppState} from "../interface/app-state";
import {Response} from "../interface/response";
import {DataState} from "../interface/data-state.enum";
import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {map, Observable, of, startWith} from "rxjs";
import {catchError} from "rxjs/operators";
import {NgForm} from "@angular/forms";
import {Router} from "@angular/router";

@Component({
  selector: 'create-account',
  templateUrl: './create-account.component.html'

})
export class CreateAccountComponent {



  constructor(private controller: Controller, private router: Router) {


  }

  get myForm(): NgForm {
    return this._myForm;
  }

  set myForm(value: NgForm) {
    this._myForm = value;
  }

  @ViewChild('createAccountForm')
  private _myForm!: NgForm;


  /*create_accountResponse$!: Observable<AppState<Response>>;
  registerAccount(form: NgForm): Observable<AppState<Response>> {
    this.create_accountResponse$ = this.services.login$(form.value.inputCoins, form.value.inputChange)
      .pipe(
        map(results => {
          return { dataState: DataState.LOADED_STATE, response: results }
        }),
        startWith({ dataState: DataState.LOADING_STATE }),
        catchError((err: string) => {
          return of({ dataState: DataState.ERROR_STATE, error: err })
        })
      );
    // Save token here
    this.create_accountResponse$.subscribe(response => {
      if (response.dataState === DataState.LOADED_STATE) {
        let results = response.response?.data.results;
        if (results) {
          sessionStorage.setItem('authToken', results[0])
        }
        // Further processing with response.response
      } else if (response.dataState === DataState.ERROR_STATE) {
        console.error('Error:', response.error);
      }
    });

    return this.create_accountResponse$;
  }
*/



  onRegister(form: NgForm): void {
    if (form.value.password !== form.value.confirmPassword) {
      alert('Passwords do not match!');
      return;
    }

    this.controller.registerUser$(form.value.username, form.value.password).subscribe({
      next: (response) => {
        console.log('Registration successful:', response);
        alert('Registration successful!');
        this.router.navigate(['app-root/login']);
      },
      error: (err) => {
        console.error('Registration error:', err);
        alert('Registration failed!');
      },
    });
  }

}
