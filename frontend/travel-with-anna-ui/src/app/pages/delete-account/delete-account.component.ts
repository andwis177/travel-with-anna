import {ChangeDetectorRef, Component, HostListener, signal, ViewEncapsulation} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {NgForOf, NgIf} from "@angular/common";
import {PasswordRequest} from "../../services/models/password-request";
import {UserService} from "../../services/services/user.service";
import {LogoutService} from "../../services/logout/logout.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Router} from "@angular/router";
import {ErrorService} from "../../services/error/error.service";
import {UserResponse} from "../../services/models/user-response";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-delete-account',
  standalone: true,
  imports: [
    FormsModule,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatSuffix,
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
  userRespond: UserResponse = {message: ''};

  constructor(private userService: UserService,
              private logoutService: LogoutService,
              private _snackBar: MatSnackBar,
              private router: Router,
              private dialog: MatDialog,
              private errorService: ErrorService,
              private changeDetector: ChangeDetectorRef)
  {
  }

  hide = signal(true);
  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
    this.onClose();
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
        this.onClose();
        if (this.userRespond.message != null) {
          this.router.navigate(['login']).then(r => this._snackBar.open(<string>this.userRespond.message, 'Close'));
        }
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
        this.changeDetector.markForCheck();
      }
    })
  }

  onClose() {
    this.dialog.getDialogById('delete-account-dialog')!.close();
  }
}
