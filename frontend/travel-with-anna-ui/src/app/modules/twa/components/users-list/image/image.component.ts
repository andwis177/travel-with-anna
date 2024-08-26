import {Component, HostListener, OnInit} from '@angular/core';
import {MatCard} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatToolbarRow} from "@angular/material/toolbar";
import {AdminService} from "../../../../../services/services/admin.service";
import {MatDialogRef} from "@angular/material/dialog";
import {SharedService} from "../../../../../services/shared/shared.service";
import {EditComponent} from "../edit/edit.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {NgIf} from "@angular/common";
import {GetAvatar$Params} from "../../../../../services/fn/admin/get-avatar";
import {UserAvatar} from "../../../../../services/models/user-avatar";

@Component({
  selector: 'app-image',
  standalone: true,
  imports: [
    MatCard,
    MatIcon,
    MatIconButton,
    MatToolbarRow,
    FormsModule,
    MatFormField,
    MatInput,
    MatLabel,
    MatSuffix,
    ReactiveFormsModule,
    NgIf
  ],
  templateUrl: './image.component.html',
  styleUrl: './image.component.scss'
})
export class ImageComponent implements OnInit {
  userAvatar: UserAvatar | null = null;

  constructor(private adminService: AdminService,
              public dialogRef: MatDialogRef<EditComponent>,
              private shareService: SharedService ){
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
    this.onClose();
  }

  ngOnInit(): void {
    this.getUserAvatar();
  }

  getUserAvatar() {
    this.shareService.getUserAdminEditId().subscribe({
      next: (userId: number | null) => {
        if (userId !== null) {
          const params: GetAvatar$Params = {userId: userId};
          this.adminService.getAvatar(params).subscribe({
            next: (avatar) => {
              this.userAvatar = avatar;
            },
            error: (err) => {
              console.log(err.error.errors);
            }
          })
        } else {
          console.error('User ID is null, cannot delete user.');
        }
      },
      error: (err) => {
        console.log(err.error.errors);
      }
    });
  }

  onClose() {
    this.dialogRef.close();
  }
}


