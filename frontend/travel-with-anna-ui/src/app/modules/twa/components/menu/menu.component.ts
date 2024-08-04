import {Component, OnInit} from '@angular/core';
import {MatToolbar, MatToolbarRow} from "@angular/material/toolbar";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {NgOptimizedImage} from "@angular/common";
import {MatDialog} from "@angular/material/dialog";
import {AccountComponent} from "../../dialog/account/account.component";
import {UserService} from "../../../../services/services/user.service";
import {MatLabel} from "@angular/material/form-field";
import {UserCredentials} from "../../../../services/models/user-credentials";
import {LogoutService} from "../../../../services/logout/logout.service";
import {MatDivider} from "@angular/material/divider";
import {MatTooltip} from "@angular/material/tooltip";
import {SharedService} from "../../../../pages/shared/shared.service";

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    MatToolbar,
    MatMenuTrigger,
    MatIconButton,
    MatIcon,
    MatMenu,
    MatMenuItem,
    MatToolbarRow,
    NgOptimizedImage,
    MatButton,
    MatLabel,
    MatDivider,
    MatTooltip

  ],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit {
  userCredentials: UserCredentials = { email: '', userName: '' };
  errorMsg: Array<string> = [];
  label: string = <string> this.userCredentials.userName;

  constructor(
    private logoutService: LogoutService,
    public dialog: MatDialog,
    private userService: UserService,
    private sharedService: SharedService
  ) {
  }

  openAccountDetails() {
    const dialogRef = this.dialog.open(AccountComponent, {})
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  ngOnInit(): void {
    this.errorMsg = [];
    this.sharedService.label$.subscribe((label) => {
      this.label = label;
    });
    this.userService.getProfile().subscribe({
      next: (response) => {
        this.userCredentials = response;
        this.sharedService.updateLabel(<string> this.userCredentials.userName);
      },
      error: (err) => {
        console.error('Error fetching user details:', err);
      }
    })
  }

  logout() {
    this.logoutService.logout();
  }
}
