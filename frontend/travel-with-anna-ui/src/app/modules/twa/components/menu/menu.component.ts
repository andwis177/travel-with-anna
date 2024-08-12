import {Component, OnInit} from '@angular/core';
import {MatToolbar, MatToolbarRow} from "@angular/material/toolbar";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {MatDialog} from "@angular/material/dialog";
import {AccountComponent} from "../../dialog/account/account.component";
import {MatLabel} from "@angular/material/form-field";
import {LogoutService} from "../../../../services/logout/logout.service";
import {MatDivider} from "@angular/material/divider";
import {MatTooltip} from "@angular/material/tooltip";
import {SharedService} from "../../../../services/shared/shared.service";

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
    NgIf
  ],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit  {
  errorMsg: Array<string> = [];
  userName: string | null= '';
  avatarImg: string | null = null;

  constructor(
    private logoutService: LogoutService,
    public dialog: MatDialog,
    private sharedService: SharedService,
  ) {
  }

  ngOnInit(): void {
    this.sharedService.getUserName().subscribe((name) => {
      this.userName = name;
    });
    this.sharedService.getImage().subscribe((image) => {
      this.avatarImg = image;
    });
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
