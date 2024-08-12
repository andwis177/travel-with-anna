import {Component, HostListener, signal, ViewEncapsulation} from '@angular/core';
import {AuthenticationRequest} from "../../services/models/authentication-request";
import {MatCardModule} from '@angular/material/card';
import {FormsModule, ReactiveFormsModule,} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatAnchor, MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/services/authentication.service";
import {MatDivider} from "@angular/material/divider";
import {AuthService} from "../../services/auth/auth.service";
import {SharedService} from "../../services/shared/shared.service";
import {ImageFileService} from "../../services/image-file-service/image-file-service";
import {UserInformationService} from "../../services/user-information/user-information-service";
import {AuthenticationResponse} from "../../services/models/authentication-response";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    MatCardModule, FormsModule, MatFormFieldModule, MatInputModule, ReactiveFormsModule, NgIf, NgForOf, MatButton, MatIcon, MatIconButton, MatAnchor, MatDivider
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  encapsulation: ViewEncapsulation.None
})
export class LoginComponent {
  authRequest: AuthenticationRequest = {email: '', password: ''};
  errorMsg: Array<string> = [];
  private avatarFile: File | null = null;

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private authService: AuthService,
    private sharedService: SharedService,
    private userInformationService: UserInformationService,
    private imageFileService: ImageFileService,
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

  register() {
    this.router.navigate(['register']).then(r => console.log('Register'));
  }

  resetPassword() {
    this.router.navigate(['reset-password']).then(r => console.log('Reset password'));

  }

  signIn() {
    this.errorMsg = [];
    this.authenticationService.authenticate({
      body: this.authRequest}).
    subscribe({
      next: (response) => {
        this.singInSuccess(response).then(r => console.log('Success'));
      },
      error: (err) => {
        console.log(err.error.errors);
        if (err.error.errors && err.error.errors.length > 0) {
          this.errorMsg = err.error.errors;
        } else {
          this.errorMsg.push('Unexpected error occurred');
        }
      }
    })
  }

  async singInSuccess(response: AuthenticationResponse) {
    this.authService.setToken( response.token as string);
    this.userInformationService.setUserCredentials(response.userName as string, response.email as string);
    this.loadAvatar(response.userName as string);
    setTimeout(() => {
      this.router.navigate(['twa']).then(r => this.sharedService.updateUserName(<string>response.userName));
    } , 1000);
  }

  loadAvatar(userName : string): void {
    this.imageFileService.getAvatar().subscribe(blob => {
      this.avatarFile = new File([blob], userName + '.jpg', { type: blob.type });
      this.sharedService.storeImage(this.avatarFile);
    });
  }
}
