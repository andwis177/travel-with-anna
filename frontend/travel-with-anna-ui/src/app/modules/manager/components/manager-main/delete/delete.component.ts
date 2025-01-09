import {Component, HostListener, OnInit, signal} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {NgForOf, NgIf} from "@angular/common";
import {PasswordRequest} from "../../../../../services/models/password-request";
import {AdminService} from "../../../../../services/services/admin.service";
import {MatDialogRef} from "@angular/material/dialog";
import {SharedService} from "../../../../../services/shared/shared.service";
import {ErrorService} from "../../../../../services/error/error.service";

@Component({
  selector: 'app-delete',
  imports: [
    FormsModule,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatSuffix,
    NgForOf,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './delete.component.html',
  styleUrl: './delete.component.scss'
})
export class DeleteComponent  implements OnInit{

  errorMsg: Array<string> = [];
  passwordRequest: PasswordRequest = {password: ''};
  userId: number | null = null;

  constructor(private adminService: AdminService,
              public dialogRef: MatDialogRef<DeleteComponent>,
              private shareService: SharedService,
              private errorService: ErrorService
  ) {
  }

  ngOnInit(): void {
    this.shareService.getUserAdminEditId().subscribe({
      next: (id: number | null) => {
        if (id !== null) {
          this.userId = id;
        } else {
          console.error('User ID is null');
        }
      },
      error: (error) => {
        console.error('Error fetching User ID:', error);
      }
    });
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
    this.shareService.getUserAdminEditId().subscribe({
      next: (userId: number | null) => {
        if (userId !== null) {
          const userAdminDeleteRequest = {userId: userId, password: this.passwordRequest.password};
          this.adminService.deleteUser({body: userAdminDeleteRequest}).subscribe({
            next: () => {
              this.dialogRef.close();
              window.location.reload()
            },
            error: (err) => {
              this.errorMsg = this.errorService.errorHandler(err);
            }
          })
        } else {
          console.error('User ID is null, cannot delete user.');
        }
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    })
  }
  cancel() {
    this.dialogRef.close();
  }
}

