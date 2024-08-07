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
import {TokenService} from "../../services/token/token.service";
import {MatDivider} from "@angular/material/divider";


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
export class LoginComponent{
  authRequest: AuthenticationRequest = {email: '', password: ''};
  errorMsg: Array<string> = [];


  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private tokenService: TokenService,
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
    this.router.navigate(['register']);
  }

  resetPassword() {
    this.router.navigate(['reset-password']);
  }

  signIn() {
    this.errorMsg = [];
    this.authService.authenticate({
      body: this.authRequest}).
    subscribe({
      next: (response) => {
        this.tokenService.token = response.token as string;
        this.router.navigate(['twa']);
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
}
