import {Component, HostListener, OnInit} from '@angular/core';
import {AdminService} from "../../../../../services/services/admin.service";
import {MatDialogRef} from "@angular/material/dialog";
import {SharedService} from "../../../../../services/shared/shared.service";
import {GetAvatar$Params} from "../../../../../services/fn/admin/get-avatar";
import {MatToolbarRow} from "@angular/material/toolbar";
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {EditComponent} from "../edit/edit.component";
import {AvatarImg} from "../../../../../services/models/avatar-img";
import {ErrorService} from "../../../../../services/error/error.service";

@Component({
  selector: 'app-image',
  standalone: true,
  imports: [
    MatToolbarRow,
    MatIconButton,
    MatIcon
  ],
  templateUrl: './image.component.html',
  styleUrl: './image.component.scss'
})
export class ImageComponent implements OnInit{
  errorMsg: Array<string> = [];
  userAvatar: AvatarImg | null = null;

  constructor(private adminService: AdminService,
              public dialogRef: MatDialogRef<EditComponent>,
              private shareService: SharedService,
              private errorService: ErrorService) {
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
              console.error(err.error.errors);
            }
          })
        } else {
          console.error('User ID is null, cannot delete user.');
        }
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    });
  }

  onClose() {
    this.dialogRef.close();
  }
}
