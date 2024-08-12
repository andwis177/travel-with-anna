import {Injectable} from "@angular/core";
import {LocalStorageService} from "../local-storage/local-storage.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private tokenKey = 'token';

  constructor(private localStorageService: LocalStorageService) {}

  isAuthenticated(): boolean {
    const token = this.localStorageService.getItem(this.tokenKey);
    return !!token;
  }

  setToken(token: string): void {
    this.localStorageService.setItem(this.tokenKey, token);
  }
}
