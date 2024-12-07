import {Component, HostListener, signal} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {MatIconButton} from "@angular/material/button";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatToolbarRow} from "@angular/material/toolbar";
import {NgForOf, NgIf} from "@angular/common";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MatIcon} from "@angular/material/icon";
import {MatDivider} from "@angular/material/divider";
import {MatSnackBar} from "@angular/material/snack-bar";
import {ChangePasswordRequest} from "../../../../../services/models/change-password-request";
import {UserService} from "../../../../../services/services/user.service";
import {ChangePassword$Params} from "../../../../../services/fn/user/change-password";
import {UserResponse} from "../../../../../services/models/user-response";
import {AccountComponent} from "../account/account.component";
import {ErrorService} from "../../../../../services/error/error.service";

@Component({
  selector: 'app-password',
  standalone: true,
  imports: [
    FormsModule,
    MatFormField,
    MatInput,
    MatLabel,
    MatToolbarRow,
    NgIf,
    MatIcon,
    MatIconButton,
    MatDivider,
    NgForOf,
    MatSuffix
  ],
  templateUrl: './password.component.html',
  styleUrl: './password.component.scss'
})
export class PasswordComponent {
  errorMsg: Array<string> = [];
  changePasswordRequest: ChangePasswordRequest = {currentPassword: '', newPassword: '', confirmPassword: ''};
  userRespond: UserResponse = {message: ''};

  constructor(public dialogRef: MatDialogRef<PasswordComponent>,
              public dialog: MatDialog,
              private userService: UserService,
              private errorService: ErrorService,
              private _snackBar: MatSnackBar,) {}

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
    this.changePassword();
  }

  onClose() {
    this.dialog.getDialogById('password-dialog')?.close();
  }

  changePassword() {
    this.errorMsg = [];
    const params: ChangePassword$Params = {body: this.changePasswordRequest};
    this.userService.changePassword(params).
    subscribe({
      next: (respond) => {
        this.userRespond = respond;
        this.dialog.getDialogById('password-dialog')?.close();
        this.openAccountDetails();
        this._snackBar.open(<string> this.userRespond.message, 'Close');
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    });
  }
  openAccountDetails() {
    const dialogRef = this.dialog.open(AccountComponent, {
      maxWidth: '90vw',
      maxHeight: '90vh',
      width: 'auto',
      height: 'auto',
      id: 'account-dialog',
    })
    dialogRef.afterClosed().subscribe(() => {
    });
  }
}
