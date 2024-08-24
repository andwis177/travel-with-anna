import {Component, HostListener, OnInit} from '@angular/core';
import {MatToolbar, MatToolbarRow} from "@angular/material/toolbar";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {NgClass, NgIf, NgOptimizedImage} from "@angular/common";
import {MatDialog} from "@angular/material/dialog";
import {AccountComponent} from "./account/account.component";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {LogoutService} from "../../../../services/logout/logout.service";
import {MatDivider} from "@angular/material/divider";
import {MatTooltip} from "@angular/material/tooltip";
import {SharedService} from "../../../../services/shared/shared.service";
import {UserInformationService} from "../../../../services/user-information/user-information-service";
import {FormsModule} from "@angular/forms";
import {MatCardContent} from "@angular/material/card";
import {MatInput} from "@angular/material/input";

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
    MatTooltip,
    NgIf,
    NgClass,
    FormsModule,
    MatCardContent,
    MatFormField,
    MatInput
  ],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit  {
  errorMsg: Array<string> = [];
  userName: string | null= '';
  avatarImg: string | null = null;
  role: string | null= '';
  identifier: string = '';

  constructor(
    private logoutService: LogoutService,
    public dialog: MatDialog,
    private sharedService: SharedService,
    private userInformationService: UserInformationService,
  ) {
  }

  @HostListener('document:keydown.enter', ['$event'])
  onEnterKeydownHandler(event: KeyboardEvent): void {
    if (this.identifier.length > 0) {
      this.findUser();
    } else {
      if (this.identifier.length === 0) {
        this.AllUsers();
      }
    }
  }

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

  openAccountDetails() {
    const dialogRef = this.dialog.open(AccountComponent, {})
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  logout() {
    this.logoutService.logout();
  }

  verifyRole(): string {
    switch (this.role) {
      case 'ADMIN':
        return 'admin-background';
      default:
        return 'default-background';
    }
  }

  findUser() {
    this.sharedService.setUserAdminViewIdentifier(this.identifier);
    this.identifier = '';
  }

  AllUsers() {
    this.identifier = '';
    this.sharedService.setUserAdminViewIdentifier('');
  }
}
