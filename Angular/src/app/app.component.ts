import {
  AfterViewInit,
  Component,
  OnInit,
  ViewChild,
  ViewContainerRef,
  ViewEncapsulation
} from '@angular/core';
import {Controller} from "./controller/controller";
import {DataState} from "./interface/data-state.enum";
import {LoginComponent} from "./components/login.component";
import {CreateAccountComponent} from "./components/create-account.component";
import {NgForm} from "@angular/forms";
import {DynamicChildLoaderDirective} from "./loadComponent.directive";
import {DashboardComponent} from "./components/dashboard.component";
import {Observable} from "rxjs";
import {AppState} from "./interface/app-state";
import {NavigationEnd, Router} from "@angular/router";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit {


  @ViewChild(DynamicChildLoaderDirective, {static: true})
  dynamicChild!: DynamicChildLoaderDirective;


  title = 'Crypto Surge';
  showMenu: boolean = false;
  currentRoute = "app-root/login";


  constructor(public controller: Controller,
              public viewContainerRef: ViewContainerRef,
              public router: Router) {
  }

  ngAfterViewInit(): void {
    if (sessionStorage.getItem('token') == null) {
      console.log("token: ", sessionStorage.getItem('token'));
      this.router.navigate(['app-root/login']);
    } else {
      this.router.navigate(['app-root/dashboard']);
    }



  }



  ngOnInit(): void {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        // Update `showDiv` based on the current route
        const currentRoute = event.urlAfterRedirects;
        console.log("event: ", currentRoute);
        this.showMenu = !['/app-root/login', '/app-root/create-account'].includes(currentRoute);
      }
    });
    this.router.navigate(['app-root/login']);


  }


  logout() {
    // Remove token from local storage or cookies
    sessionStorage.removeItem('token');

    // TO DO
    // Request to server

  }



}
