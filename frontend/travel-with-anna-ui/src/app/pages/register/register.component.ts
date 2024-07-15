import {Component, ViewEncapsulation} from '@angular/core';
import {RegistrationRequest} from "../../services/models/registration-request";
import {FormsModule} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {NgForOf, NgIf} from "@angular/common";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/services/authentication.service";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    FormsModule,
    MatButton,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatSuffix,
    NgForOf,
    NgIf
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  encapsulation: ViewEncapsulation.None
})
export class RegisterComponent {

  registerRequest: RegistrationRequest = {userName: '', email: '', password: ''};
  repeatPassword: string = '';
  errorMsg: Array<string> = [];
  hidePassword: boolean = true;

  constructor(
    private router: Router,
    private authService: AuthenticationService,
  ) {}

  togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }

  register() {
    this.errorMsg = [];
    if(this.registerRequest.password !== this.repeatPassword) {
      this.errorMsg.push('Passwords do not match');
      throw new HttpErrorResponse({status: 400, statusText: 'Bad Request', error: {errorMsg: 'Passwords do not match'}});
    } else {
      this.authService.register({
        body: this.registerRequest
      }).subscribe( {
        next: () => {
          this.router.navigate(['activate-account']);
        },
        error: (err) => {
          console.log(err);
          if (err.error.errors && err.error.errors.length > 0) {
            this.errorMsg = err.error.errors;
          } else {
            this.errorMsg.push('Unexpected error occurred');
          }
        }
      })
    }
  }
  login() {
    this.router.navigate(['login']);
  }
}
