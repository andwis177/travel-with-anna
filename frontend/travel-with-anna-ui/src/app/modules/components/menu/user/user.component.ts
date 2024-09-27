import {Component, OnInit} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {MatIconButton} from "@angular/material/button";
import {MatLabel} from "@angular/material/form-field";
import {MatTooltip} from "@angular/material/tooltip";
import {NgIf} from "@angular/common";
import {AccountComponent} from "./account/account.component";
import {LogoutService} from "../../../../services/logout/logout.service";
import {SharedService} from "../../../../services/shared/shared.service";

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [
    MatIconButton,
    MatLabel,
    MatTooltip,
    NgIf
  ],
  templateUrl: './user.component.html',
  styleUrl: './user.component.scss'
})
export class UserComponent implements OnInit {
  role: string | null = '';
  userName: string | null = '';
  avatarImg: string | null = null;

  constructor(
    private logoutService: LogoutService,
    public dialog: MatDialog,
    private sharedService: SharedService,
  )
  {}

  ngOnInit(): void {
    this.sharedService.getUserName().subscribe((name) => {
      this.userName = name;
    });
    this.sharedService.getImage().subscribe((image) => {
      this.avatarImg = image;
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

  logout() {
    this.logoutService.logout();
  }
}
