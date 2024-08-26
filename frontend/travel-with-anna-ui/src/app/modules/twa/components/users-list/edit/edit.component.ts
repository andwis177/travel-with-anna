import {Component, HostListener, OnInit, signal} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCard, MatCardActions, MatCardHeader} from "@angular/material/card";
import {MatDivider} from "@angular/material/divider";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatToolbarRow} from "@angular/material/toolbar";
import {MatTooltip} from "@angular/material/tooltip";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {MatCheckbox} from "@angular/material/checkbox";
import {AdminService} from "../../../../../services/services/admin.service";
import {MatDialogRef} from "@angular/material/dialog";
import {SharedService} from "../../../../../services/shared/shared.service";
import {UserAdminView} from "../../../../../services/models/user-admin-view";
import {UserAdminEdit} from "../../../../../services/models/user-admin-edit";
import {UserAdminUpdateRequest} from "../../../../../services/models/user-admin-update-request";
import {GetUserAdminViewByIdentifier$Params} from "../../../../../services/fn/admin/get-user-admin-view-by-identifier";
import {MatOption} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";
import {UpdateUser$Params} from "../../../../../services/fn/admin/update-user";

@Component({
  selector: 'app-edit',
  standalone: true,
  imports: [
    FormsModule,
    MatCard,
    MatCardActions,
    MatCardHeader,
    MatDivider,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatSuffix,
    MatToolbarRow,
    MatTooltip,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    MatCheckbox,
    MatOption,
    MatSelect,
    NgClass
  ],
  templateUrl: './edit.component.html',
  styleUrl: './edit.component.scss'
})
export class EditComponent implements OnInit {
  errorMsg: Array<string> = [];
  userId: number = 0;
  roles: Array<string> = [];
  userAdminView: UserAdminView = {};
  userAdminEdit: UserAdminEdit = {userId: 0, accountLocked: false, enabled: true, roleName: ''};
  passwordRequest: string = '';
  userAdminUpdateRequest: UserAdminUpdateRequest = {};


  constructor(private adminService: AdminService,
              public dialogRef: MatDialogRef<EditComponent>,
              private shareService: SharedService ){
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
          const params: GetUserAdminViewByIdentifier$Params = {identifier: userId.toString()};
          this.adminService.getUserAdminViewByIdentifier(params).subscribe({
            next: (data) => {
              this.userAdminView = data;
              this.getRoles();
            },
            error: (err) => {
              console.log(err.error.errors);
              if (err.error.errors && err.error.errors.length > 0) {
                this.errorMsg = err.error.errors;
              } else {
                this.errorMsg.push('Failed to load User details', err);
              }
            }
          })
        } else {
          console.error('User ID is null, cannot delete user.');
        }
      },
      error: (err) => {
        console.log(err.error.errors);
        if (err.error.errors && err.error.errors.length > 0) {
          this.errorMsg = err.error.errors;
        } else {
          this.errorMsg.push('Failed to get User ID', err);
        }
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
    this.dialogRef.close();
  }

  update() {
    this.errorMsg = [];
    this.userAdminEdit.userId = this.userAdminView.userId;
    this.userAdminEdit.accountLocked = this.userAdminView.accountLocked;
    this.userAdminEdit.enabled = this.userAdminView.enabled;
    this.userAdminEdit.roleName = this.userAdminView.roleName;
    this.userAdminUpdateRequest.password = this.passwordRequest;
    this.userAdminUpdateRequest.userAdminEdit = this.userAdminEdit;
    const params: UpdateUser$Params = {body: this.userAdminUpdateRequest};
    this.adminService.updateUser(params).subscribe({
      next: () => {
        this.dialogRef.close();
        window.location.reload();
      },
      error: (err) => {
        console.log(err.error.errors);
        if (err.error.errors && err.error.errors.length > 0) {
          this.errorMsg = err.error.errors;
        } else {
          this.errorMsg.push('Failed to update user with Id ' + this.userAdminEdit.userId, err);
        }
      }
    })
  }

  getRoleClass(role: string | undefined): string {
    switch (role) {
      case 'ADMIN':
        return 'admin';
      default:
        return 'default';
    }
  }
}
