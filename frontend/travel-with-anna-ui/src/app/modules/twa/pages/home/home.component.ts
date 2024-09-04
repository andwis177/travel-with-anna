import {Component, OnInit} from '@angular/core';
import {Router, RouterOutlet} from "@angular/router";
import {TripListComponent} from "../../components/trip-list/trip-list.component";
import {UserInformationService} from "../../../../services/user-information/user-information-service";
import {NgIf} from "@angular/common";
import {UsersListComponent} from "../../components/users-list/users-list.component";
import {LogoComponent} from "../../components/menu/logo/logo.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    RouterOutlet,
    TripListComponent,
    NgIf,
    UsersListComponent,
    LogoComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})

export class HomeComponent implements OnInit  {
  role: string | null= '';
  isRoleAdmin: boolean = false;

  constructor(private route: Router,
    private userInformationService: UserInformationService
  ) {
  }

  ngOnInit(): void {
    this.role = this.userInformationService.getRole();
    this.isRoleAdmin = this.role === 'ADMIN';
    if (this.isRoleAdmin) {
      this.route.navigate(['/twa/users-list']).then();
    } else {
      this.route.navigate(['/twa/trip-list']).then();
    }
  }

}
