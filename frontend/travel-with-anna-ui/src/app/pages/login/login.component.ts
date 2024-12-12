import {Component, HostListener, OnInit, signal, ViewEncapsulation} from '@angular/core';
import {AuthenticationRequest} from "../../services/models/authentication-request";
import {MatCardModule} from '@angular/material/card';
import {FormsModule, ReactiveFormsModule,} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/services/authentication.service";
import {MatDivider} from "@angular/material/divider";
import {AuthService} from "../../services/auth/auth.service";
import {SharedService} from "../../services/shared/shared.service";
import {ImageFileService} from "../../services/image-file-service/image-file-service";
import {UserInformationService} from "../../services/user-information/user-information-service";
import {AuthenticationResponse} from "../../services/models/authentication-response";
import {firstValueFrom} from "rxjs";
import {LocalStorageService} from "../../services/local-storage/local-storage.service";
import {ErrorService} from "../../services/error/error.service";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    MatCardModule, FormsModule, MatFormFieldModule, MatInputModule, ReactiveFormsModule, NgIf, NgForOf, MatIcon, MatIconButton, MatDivider
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  encapsulation: ViewEncapsulation.None
})
export class LoginComponent implements OnInit {
  authRequest: AuthenticationRequest = {email: '', password: ''};
  errorMsg: Array<string> = [];
  avatarFile: File | null = null;
  stringImg: string | ArrayBuffer = '';

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private localStorage: LocalStorageService,
    private authService: AuthService,
    private sharedService: SharedService,
    private userInformationService: UserInformationService,
    private imageFileService: ImageFileService,
    private errorService: ErrorService
  ) {
  }

  hide = signal(true);
  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }

  @HostListener('document:keydown.enter', ['$event'])
  onEnterKeydownHandler(event: KeyboardEvent): void {
    this.signIn();
  }

  ngOnInit() {
    this.localStorage.clear()
  }

  register() {
    this.router.navigate(['register']).then(r => console.log('Register'));
  }

  resetPassword() {
    this.router.navigate(['reset-password']).then(r => console.log('Reset password'));
  }

  activateAccount() {
    this.router.navigate(['activate-account']).then();
  }

  signIn() {
    this.errorMsg = [];
    this.authenticationService.authenticate({
      body: this.authRequest}).
    subscribe({
      next: (response) => {
        this.singInSuccess(response).then();
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    })
  }

  async singInSuccess(response: AuthenticationResponse) {
    this.authService.setToken( response.token as string);
    this.userInformationService.setUserCredentials(
      response.userName as string, response.email as string);
    try {
      await this.loadAvatar(response.userName as string);
      if (response.role === 'ADMIN') {
        this.router.navigate(['admin']).then(r => this.sharedService.updateUserName(<string>response.userName));
      }
      if (response.role === 'USER') {
        this.router.navigate(['twa']).then(r => this.sharedService.updateUserName(<string>response.userName));
      }
    } catch (error) {
      console.error('Failed to load avatar:', error);
    }
  }

  async loadAvatar(userName: string): Promise<void> {
    try {
      const blob: Blob = await firstValueFrom(this.imageFileService.getAvatar());
      this.avatarFile = new File([blob], `${userName}.jpg`, { type: blob.type });
      this.stringImg = await this.imageFileService.convertBase64ToString(this.avatarFile);
      this.sharedService.storeImage(this.stringImg);
    } catch (error) {
      console.error('Error loading avatar:', error);
    }
  }
}
