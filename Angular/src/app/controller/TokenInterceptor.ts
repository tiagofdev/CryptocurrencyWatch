import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Retrieve the token from localStorage or any other storage mechanism
    const token = sessionStorage.getItem('authToken'); // Adjust based on your implementation

    if (token) {
      // Clone the request to add the Authorization header
      const clonedReq = request.clone({
        headers: request.headers.set('Authorization', `Bearer ${token}`),
      });
      return next.handle(clonedReq);
    }

    // If no token is present, forward the request as is
    return next.handle(request);
  }
}
