import {Component, HostListener} from '@angular/core';
import {Router} from "@angular/router";
import {MatCard, MatCardContent, MatCardHeader} from "@angular/material/card";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ResetPasswordRequest} from "../../services/models/reset-password-request";
import {AuthenticationService} from "../../services/services/authentication.service";
import {NgForOf, NgIf} from "@angular/common";
import {MatSnackBar} from "@angular/material/snack-bar";
import {ErrorService} from "../../services/error/error.service";

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [
    MatCard,
    MatCardHeader,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    FormsModule,
    NgIf,
    NgForOf,
    MatCardContent
  ],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.scss'
})
export class ResetPasswordComponent {
  credential: ResetPasswordRequest = {credential: ''};
  errorMsg: Array<string> = [];
  isResetPassword = false;

  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private errorService: ErrorService,
    private _snackBar: MatSnackBar
  ) {
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
    this.onClose();
  }

  @HostListener('document:keydown.enter', ['$event'])
  onEnterKeydownHandler(event: KeyboardEvent): void {
    this.resetPassword();
  }

  resetPassword() {
    this.isResetPassword = false;
    this.errorMsg = [];
    this.authService.resetPassword({
      body: this.credential
    }).subscribe({
      next: () => {
        this.router.navigate(['login']).then(() => this._snackBar.open('Your password has been successfully reset and sent to your email.', 'Close'));
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandlerWithJson(err);
      }
    })
  }

  onClose() {
    this.router.navigate(['login']).then();
  }
}
