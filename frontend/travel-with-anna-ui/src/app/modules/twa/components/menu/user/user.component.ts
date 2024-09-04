import {Component, OnInit} from '@angular/core';
import {LogoutService} from "../../../../../services/logout/logout.service";
import {MatDialog} from "@angular/material/dialog";
import {SharedService} from "../../../../../services/shared/shared.service";
import {UserInformationService} from "../../../../../services/user-information/user-information-service";
import {AccountComponent} from "../account/account.component";
import {MatIconButton} from "@angular/material/button";
import {MatLabel} from "@angular/material/form-field";
import {MatTooltip} from "@angular/material/tooltip";
import {NgIf} from "@angular/common";

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
    private userInformationService: UserInformationService,
  )
  {}


  ngOnInit(): void {
    this.sharedService.getUserName().subscribe((name) => {
      this.userName = name;
    });
    this.sharedService.getImage().subscribe((image) => {
      this.avatarImg = image;
    });
    this.role = this.userInformationService.getRole();
    this.verifyRole();
  }

  verifyRole(): string {
    switch (this.role) {
      case 'ADMIN':
        return 'admin-background';
      default:
        return 'default-background';
    }
  }

  openAccountDetails() {
    const dialogRef = this.dialog.open(AccountComponent, {})
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  logout() {
    this.logoutService.logout();
  }

}
