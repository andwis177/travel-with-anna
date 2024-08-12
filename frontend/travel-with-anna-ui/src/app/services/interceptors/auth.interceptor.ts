import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {LocalStorageService} from "../local-storage/local-storage.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private localStorageService: LocalStorageService) {
  }
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authToken = this.localStorageService.getItem('token');
    const excludedEndpoints = ['register', 'authenticate', 'activate-account', 'reset-password', 'delete-account'];

    const isExcluded = excludedEndpoints.some(endpoint => {
      const regex = new RegExp(endpoint, 'i');
      return regex.test(req.url);
    });

    if (authToken && !isExcluded) {
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${authToken}`
        }
      });
      return next.handle(authReq);
    }
    return next.handle(req);
  }
}
