import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import {DynamicChildLoaderDirective} from "./loadComponent.directive";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {LoginComponent} from "./components/login.component";
import {TokenInterceptor} from "./controller/TokenInterceptor";
import {CreateAccountComponent} from "./components/create-account.component";
import { RouterModule, Routes } from '@angular/router';
import {DashboardComponent} from "./components/dashboard.component";
import {ReportsComponent} from "./components/reports.component";


const routes: Routes = [
  { path: 'app-root', redirectTo: '/login', pathMatch: 'full' }, // Default route
  { path: 'app-root', component: AppComponent, // Parent component
    children: [
      { path: 'dashboard', component: DashboardComponent }, // Child route
      { path: 'login', component: LoginComponent },
      { path: 'create-account', component: CreateAccountComponent },
      { path: 'reports', component: ReportsComponent },
    ],
  },
];

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    CreateAccountComponent,
    DynamicChildLoaderDirective,
    DashboardComponent,
    ReportsComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(routes)
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true }
  ],
  exports: [
    RouterModule
  ],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule { }
