import {Component, HostListener, signal, ViewEncapsulation} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {MatCard, MatCardContent, MatCardHeader} from "@angular/material/card";
import {MatDivider} from "@angular/material/divider";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatToolbarRow} from "@angular/material/toolbar";
import {NgForOf, NgIf} from "@angular/common";
import {PasswordRequest} from "../../services/models/password-request";
import {UserService} from "../../services/services/user.service";
import {LogoutService} from "../../services/logout/logout.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Router} from "@angular/router";
import {UserRespond} from "../../services/models/user-respond";


@Component({
  selector: 'app-delete-account',
  standalone: true,
  imports: [
    FormsModule,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatDivider,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatSuffix,
    MatToolbarRow,
    NgForOf,
    NgIf
  ],
  templateUrl: './delete-account.component.html',
  styleUrl: './delete-account.component.scss',
  encapsulation: ViewEncapsulation.None
})
export class DeleteAccountComponent {

  errorMsg: Array<string> = [];
  passwordRequest: PasswordRequest = {password: '' };
  userRespond: UserRespond = {message: ''};

  constructor(private userService: UserService,
              private logoutService: LogoutService,
              private _snackBar: MatSnackBar,
              private router: Router)
  {
  }

  hide = signal(true);
  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
    this.cancel();
  }

  @HostListener('document:keydown.enter', ['$event'])
  onEnterKeydownHandler(event: KeyboardEvent): void {
    this.deleteAccount();
  }

  deleteAccount() {
    this.errorMsg = [];
    this.userService.delete({
      body: this.passwordRequest}).
    subscribe({
      next: (respond ) => {
        this.userRespond = respond;
        this.logoutService.logout();
        if (this.userRespond.message != null) {
          this.router.navigate(['login']).then(r => this._snackBar.open(<string>this.userRespond.message, 'Close'));
        }
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

  cancel() {
    this.router.navigate(['twa'])
  }
}
