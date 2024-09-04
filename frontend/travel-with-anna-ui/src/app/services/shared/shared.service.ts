import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {LocalStorageService} from "../local-storage/local-storage.service";

@Injectable({
  providedIn: 'root'
})
export class SharedService {
  private userName = new BehaviorSubject<string |null>('');
  private userName$: Observable<string |null> = this.userName.asObservable();
  private avatarImg = new BehaviorSubject<string | null>(null);
  private avatarImg$:Observable<string | null> = this.avatarImg.asObservable();
  private userAdminEditId = new BehaviorSubject<number | null>(null);
  private userAdminEditId$:Observable<number | null> = this.userAdminEditId.asObservable();
  private userAdminViewIdentifier= new BehaviorSubject<string>('');
  public userAdminViewIdentifier$:Observable<string> = this.userAdminViewIdentifier.asObservable();

  private userNameKey: string = 'userName';
  private imageKey: string = 'image';

  constructor(
    private localStorageService: LocalStorageService)
  {}

  clean(): void {
    this.localStorageService.clear();
    this.userName.next('User Name');
    this.avatarImg.next(null);
  }

  updateUserName(newUserName: string): void {
    this.userName.next(newUserName);
    this.localStorageService.setItem(this.userNameKey, newUserName);
  }

  getUserName(): Observable<string | null> {
    this.userName.next(this.localStorageService.getItem(this.userNameKey));
    return this.userName$;
  }

  setUserAdminEditId(userAdminEditId: number | undefined): void {
    if (userAdminEditId !== undefined) {
      this.userAdminEditId.next(userAdminEditId);
    } else {
      this.userAdminEditId.next(null);
    }
  }

  getUserAdminEditId(): Observable<number | null> {
    return this.userAdminEditId$;
  }

  setUserAdminViewIdentifier(identifier: string): void {
      this.userAdminViewIdentifier.next(identifier);
  }

  updateAvatarImg(newImg: string): void {
    this.avatarImg.next(newImg);
  }

  storeImage(base64: string | ArrayBuffer): void {
    this.localStorageService.setItem(this.imageKey, base64.toString());
  }

  getImage(): Observable<string | null> {
    this.avatarImg.next(this.localStorageService.getItem(this.imageKey));
    return this.avatarImg$;
  }
}

