import {Component, HostListener, OnInit, signal} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatOption} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";
import {MatToolbarRow} from "@angular/material/toolbar";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {UserAdminResponse} from "../../../../../services/models/user-admin-response";
import {UserAdminEditRequest} from "../../../../../services/models/user-admin-edit-request";
import {UserAdminUpdateRequest} from "../../../../../services/models/user-admin-update-request";
import {AdminService} from "../../../../../services/services/admin.service";
import {MatDialogRef} from "@angular/material/dialog";
import {SharedService} from "../../../../../services/shared/shared.service";
import {ErrorService} from "../../../../../services/error/error.service";
import {UpdateUser$Params} from "../../../../../services/fn/admin/update-user";
import {GetUserByIdentifier$Params} from "../../../../../services/fn/admin/get-user-by-identifier";
import {AuthenticationService} from "../../../../../services/services/authentication.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-edit',
  imports: [
    FormsModule,
    MatCheckbox,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatOption,
    MatSelect,
    MatToolbarRow,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    NgClass,
    MatSuffix
  ],
  templateUrl: './edit.component.html',
  styleUrl: './edit.component.scss'
})
export class EditComponent implements OnInit{
  errorMsg: Array<string> = [];
  userId: number = 0;
  roles: Array<string> = [];
  userAdminResponse: UserAdminResponse = {};
  userAdminEditRequest: UserAdminEditRequest = {userId: 0, accountLocked: false, enabled: true, roleName: ''};
  passwordRequest: string = '';
  userAdminUpdateRequest: UserAdminUpdateRequest = {userAdminEditRequest: this.userAdminEditRequest};

  constructor(private adminService: AdminService,
              private authService: AuthenticationService,
              public dialogRef: MatDialogRef<EditComponent>,
              private shareService: SharedService,
              private errorService: ErrorService,
              private _snackBar: MatSnackBar
  ) {
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
    this.update();
  }

  ngOnInit(): void {
    this.getUserAdminView();
  }

  getUserAdminView() {
    this.shareService.getUserAdminEditId().subscribe({
      next: (userId: number | null) => {
        if (userId !== null) {
          const params: GetUserByIdentifier$Params = {identifier: userId.toString()};
          this.adminService.getUserByIdentifier(params).subscribe({
            next: (data) => {
              this.userAdminResponse = data;
              this.getRoles();
            },
            error: (err) => {
              this.errorMsg = this.errorService.errorHandlerWithJson(err);
            }
          })
        } else {
          console.error('User ID is null, cannot delete user.');
        }
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandlerWithJson(err);
      }
    });
  }

  getRoles() {
    this.adminService.getAllRoleNamesWithAdmin()
      .subscribe( {
        next: (role) => {
          this.roles = role;
        },
        error: (err) => {
          this.errorMsg = this.errorService.errorHandlerWithJson(err);
        }
      })
  }

  onClose() {
    this.dialogRef.close();
  }

  update() {
    this.errorMsg = [];
    this.userAdminEditRequest.userId = this.userAdminResponse.userId!;
    this.userAdminEditRequest.accountLocked = this.userAdminResponse.accountLocked;
    this.userAdminEditRequest.enabled = this.userAdminResponse.enabled;
    this.userAdminEditRequest.roleName = this.userAdminResponse.roleName!;
    this.userAdminUpdateRequest.password = this.passwordRequest;
    this.userAdminUpdateRequest.userAdminEditRequest = this.userAdminEditRequest;
    const params: UpdateUser$Params = {body: this.userAdminUpdateRequest};
    this.adminService.updateUser(params).subscribe({
      next: () => {
        this.dialogRef.close();
        window.location.reload();
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandlerWithJson(err);
      }
    })
  }

  getRoleClass(role: string): string {
    switch (role) {
      case 'ADMIN':
        return 'negative-color';
      default:
        return 'positive-color';
    }
  }

  resendCode(): void {
    this.errorMsg = [];
    this.authService.resendActivationCode({
      email: this.userAdminResponse.email!
    }).subscribe({
      next: () => {
        this._snackBar.open('New activation code has been send to ' + this.userAdminResponse.email + ".", 'Close');
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandlerWithJson(err);
      }
    })
  }
}
