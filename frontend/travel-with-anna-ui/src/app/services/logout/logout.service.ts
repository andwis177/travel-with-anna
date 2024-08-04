import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {BaseService} from "../base-service";
import {ApiConfiguration} from "../api-configuration";

@Injectable({
  providedIn: 'root'
})
export class LogoutService extends BaseService {
  errorMsg: Array<string> = [];

  constructor(config: ApiConfiguration,
              http: HttpClient,
              private router: Router){
    super(config, http);
  }

  logout(): void   {
    this.errorMsg = [];
    this.http.post<void>(`${this.rootUrl}/execute_logout`, {},
      {responseType:'json'}).subscribe({
      next: () => {
        localStorage.removeItem('token');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Error logging out:', err);
        this.errorMsg.push('Logout failed. Please try again.');
      },
      complete: () => {
        console.log('Logout complete.');
      }
    });
  }
}
