import {Component, HostListener, signal} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatCard, MatCardActions, MatCardHeader} from "@angular/material/card";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatToolbarRow} from "@angular/material/toolbar";
import {NgForOf, NgIf} from "@angular/common";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MatIcon} from "@angular/material/icon";
import {MatDivider} from "@angular/material/divider";
import {MatSnackBar} from "@angular/material/snack-bar";
import {ChangePasswordRequest} from "../../../../../services/models/change-password-request";
import {UserRespond} from "../../../../../services/models/user-respond";
import {UserService} from "../../../../../services/services/user.service";
import {ErrorService} from "../../../../../services/error/error.service";
import {ChangePassword$Params} from "../../../../../services/fn/user/change-password";


@Component({
  selector: 'app-password',
  standalone: true,
  imports: [
    FormsModule,
    MatButton,
    MatCard,
    MatCardActions,
    MatCardHeader,
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
  userRespond: UserRespond = {message: ''};

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
    console.log("Change Password Request:", this.changePasswordRequest);
    const params: ChangePassword$Params = {body: this.changePasswordRequest};
    this.userService.changePassword(params).
    subscribe({
      next: (respond) => {
        console.log("Respond:", respond);
        this.userRespond = respond;
        this.dialogRef.close();
        this._snackBar.open(<string> this.userRespond.message, 'Close');
      },
      error: (err) => {
        console.log(err.error.errors);
        this.errorService.errorHandler(err);
        this.errorMsg = this.errorService.errorHandler(err);
      }
    });
  }
}
