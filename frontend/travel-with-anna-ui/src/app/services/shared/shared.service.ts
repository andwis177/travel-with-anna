import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";
import {LocalStorageService} from "../local-storage/local-storage.service";

@Injectable({
  providedIn: 'root'
})
export class SharedService {
   private userName = new BehaviorSubject<string |null>('');
   public userName$: Observable<string |null> = this.userName.asObservable();
  private email = new BehaviorSubject<string |null>('');
  public email$: Observable<string |null> = this.email.asObservable();
   private avatarImg = new BehaviorSubject<string | null>(null);
   public avatarImg$:Observable<string | null> = this.avatarImg.asObservable();
  private userNameKey: string = 'userName';
  private emailKey: string = 'email';
  private imageKey: string = 'image';


  constructor(private localStorageService: LocalStorageService) {
  }

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

  updateEmail(newEmail: string): void {
    this.email.next(newEmail);
    this.localStorageService.setItem(this.emailKey, newEmail);
  }

  updateAvatarImg(newImg: string): void {
    this.avatarImg.next(newImg);
  }

  convertToBase64(file: File): Promise<string | ArrayBuffer | null> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = error => reject(error);
    });
  }

  storeImage(file: File): void {
    this.convertToBase64(file).then(base64 => {
      if (base64) {
        this.localStorageService.setItem(this.imageKey, base64.toString());
      }
    }).catch(error => console.error(error));
  }

  getImage(): Observable<string | null> {
    this.avatarImg.next(this.localStorageService.getItem(this.imageKey));
    return this.avatarImg$;
  }
}

