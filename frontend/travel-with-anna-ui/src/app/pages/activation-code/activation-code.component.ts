import {Component, HostListener} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCard, MatCardContent, MatCardHeader} from "@angular/material/card";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {NgForOf, NgIf} from "@angular/common";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/services/authentication.service";
import {ErrorService} from "../../services/error/error.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-activation-code',
  imports: [
    FormsModule,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatFormField,
    MatInput,
    MatLabel,
    NgForOf,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './activation-code.component.html',
  styleUrl: './activation-code.component.scss'
})
export class ActivationCodeComponent {
  errorMsg: Array<string> = [];
  email: string = '';

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
    this.requestActivationCode();
  }

  requestActivationCode() {
    this.errorMsg = [];
    this.authService.resendActivationCode({
      email: this.email
    }).subscribe({
      next: () => {
        this.router.navigate(['login']).then(r =>
          this._snackBar.open('New activation code has been send', 'Close'));
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
