import {Component, HostListener} from '@angular/core';
import {Router} from "@angular/router";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatCard, MatCardHeader} from "@angular/material/card";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatToolbarRow} from "@angular/material/toolbar";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatDivider} from "@angular/material/divider";
import {ResetPasswordRequest} from "../../services/models/reset-password-request";
import {AuthenticationService} from "../../services/services/authentication.service";
import {NgForOf, NgIf} from "@angular/common";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [
    MatButton,
    MatCard,
    MatCardHeader,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatToolbarRow,
    ReactiveFormsModule,
    FormsModule,
    MatDivider,
    NgIf,
    NgForOf
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
        this.router.navigate(['login']).then(r => this._snackBar.open('Your password has been successfully reset and sent to your email.', 'Close'));
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

  onClose() {
    this.router.navigate(['login']);
  }
}
