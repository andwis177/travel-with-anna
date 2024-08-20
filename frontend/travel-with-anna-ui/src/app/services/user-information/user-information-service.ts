import {Injectable} from "@angular/core";
import {LocalStorageService} from "../local-storage/local-storage.service";

@Injectable({
  providedIn: 'root'
})
export class UserInformationService {
  private userName: string = 'userName';
  private email: string = 'email';
  private role: string = 'role';

  constructor(private localStorageService: LocalStorageService) {}

  setUserCredentials(userName: string, email: string, role: string): void {
    this.localStorageService.setItem(this.userName, userName);
    this.localStorageService.setItem(this.email, email);
    this.localStorageService.setItem(this.role, role);
  }

  getUserName(): string | null {
    return this.localStorageService.getItem(this.userName);
  }

  getEmail() {
    return this.localStorageService.getItem(this.email);
  }

  getRole() {
    return this.localStorageService.getItem(this.role);
  }
}
