import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {map, Observable, pipe, throwError} from "rxjs";
import {tap, catchError} from "rxjs/operators";
import {Response} from "../interface/response";

@Injectable({ providedIn: 'root' })
export class Controller {
  private cryptoDataCache: any = null;

  constructor(private http: HttpClient) { }
  private readonly  apiUrl = 'http://localhost:8080';



  /**
   * This observable receives the list of currencies info from the SpringBoot
   *
   */
  getCurrencies$() : Observable<Response> {
    const token = sessionStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}`});
    return this.http.get<Response>(`${this.apiUrl}/getCurrencies`, {headers})
      .pipe(
        tap(console.log),
        catchError(Controller.handleError)
      );
  }

  getPrices$() : Observable<Response> {
    const token = sessionStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}`});
    return this.http.get<Response>(`${this.apiUrl}/getPrices`, {headers})
      .pipe(
        tap(console.log),
        catchError(Controller.handleError)
      );
  }

  getHistorical$() : Observable<Response> {
    const token = sessionStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}`});
    return this.http.get<Response>(`${this.apiUrl}/getHistorical`, {headers})
      .pipe(
        tap(console.log),
        catchError(Controller.handleError)
      );
  }

  getProjectionsSMA$(inputName: string) : Observable<Response> {
    const token = sessionStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}`});
    return this.http.get<Response>(`${this.apiUrl}/getProjectionsSMA/${inputName}`, {headers})
      .pipe(
        tap(console.log),
        catchError(Controller.handleError)
      );
  }

  getProjectionsLR$(inputName: string) : Observable<Response> {
    // Retrieve the token from sessionStorage
    const token = sessionStorage.getItem('token');
    // Set up headers with the token
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}`});

    return this.http.get<Response>(`${this.apiUrl}/getProjectionsLR/${inputName}`, {headers})
      .pipe(
        tap(console.log),
        catchError(Controller.handleError)
      );
  }

  private static handleError(error: HttpErrorResponse): Observable<never> {
    console.log(error);
    return throwError(`Error occurred - Error Code: ${error.status}`);
  }

  login$(username: string, password: string): Observable<Response> {
    const loginHeaders = new HttpHeaders({ 'Content-Type': 'application/json' });
    const body = { username, password };

    return this.http
      .post<Response>(`${this.apiUrl}/auth/login`, body, { headers: loginHeaders })
      .pipe(
        tap(),
        catchError((error) => {
          console.error('Login failed:', error);
          throw error;
        })
      );
  }

  /**
   * Sends user registration data to the backend.
   * @param username The username for the new account.
   * @param email
   * @param password The password for the new account.
   * @returns Observable containing the server response.
   */
  registerUser$(username: string, password: string): Observable<Response> {
    const reHeaders = new HttpHeaders({ 'Content-Type': 'application/json' });
    const body = { username, password };

    return this.http.post<Response>(`${this.apiUrl}/register`, body, { headers: reHeaders })
      .pipe(
        catchError((error) => {
          console.error('Registration failed:', error);
          throw error;
        })
      );
  }



  saveAlerts$(username: string, currency: string, subscribe: string, criteria: string,
              operator: string, threshold: string): Observable<Response> {
    const token = sessionStorage.getItem('token');
    if (!token) {
      console.error('No token found in sessionStorage.');
      // @ts-ignore
      return;
    }
    // Set up headers with the token
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}`});
    // let params = new URLSearchParams();
    // params.append("username", username);
    // params.append("currency", currency);
    // params.append("criteria", criteria);
    // params.append("subscribe", subscribe);
    // params.append("username", username);
    // params.append("username", username);

    console.log("controller");
    console.log("username: ", username);
    console.log("currency: ", currency);
    console.log("subscribe: ", subscribe);
    console.log("criteria: ",criteria);
    console.log("operator: ", operator);
    console.log("threshold: ", threshold);

    return this.http.get<Response>(`${this.apiUrl}/saveAlerts/${username}+
    ${currency}+${subscribe}+${criteria}+${operator}+${threshold}`,
      { headers})
      .pipe(
        tap(response => console.log),
        catchError((error) => {
          console.error('Saving Alerts failed:', error);
          throw error;
        })
      );
  }

  getAlerts$(username: string): Observable<Response> {
    const token = sessionStorage.getItem('token');
    // Set up headers with the token

    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}`});
    console.log("username: ", username);
    return this.http.get<Response>(`${this.apiUrl}/getAlerts/${username}`, { headers })
      .pipe(
        tap(console.log),
        catchError((error) => {
          console.error('Getting Alerts failed:', error);
          throw error;
        })
      );
  }

  test$() {
    const token = sessionStorage.getItem('token');
    // Set up headers with the token

    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}`});

    let response = this.http.get<Response>(`${this.apiUrl}/test`, { headers })
    response.subscribe(response => {
      console.log(response);
    })
    console.log("response: ", response);
  }


  /**
   * Retrieves the stored JWT token from localStorage.
   */
  getToken(): string | null {
    return sessionStorage.getItem('token');
  }

}


