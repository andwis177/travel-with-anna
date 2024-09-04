import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {UserInformationService} from "../../../../../services/user-information/user-information-service";
import {MatIconButton} from "@angular/material/button";
import {MatToolbar, MatToolbarRow} from "@angular/material/toolbar";
import {MatTooltip} from "@angular/material/tooltip";
import {NgClass, NgOptimizedImage} from "@angular/common";

@Component({
  selector: 'app-logo',
  standalone: true,
  imports: [
    MatIconButton,
    MatToolbar,
    MatToolbarRow,
    MatTooltip,
    NgOptimizedImage,
    NgClass
  ],
  templateUrl: './logo.component.html',
  styleUrl: './logo.component.scss'
})
export class LogoComponent implements OnInit {
  role: string | null = '';


  constructor(
    private route: Router,
    private userInformationService: UserInformationService,
  ) {
  }

  ngOnInit(): void {
    this.role = this.userInformationService.getRole();
    this.verifyRole();
  }

  verifyRole(): string {
    switch (this.role) {
      case 'ADMIN':
        return 'admin-background';
      default:
        return 'user-background';
    }
  }

  goToHomePage() {
    if (this.role === 'ADMIN') {
      this.route.navigate(['/twa/users-list']).then();
    } else {
      this.route.navigate(['/twa/trip-list']).then();
    }
  }

}
