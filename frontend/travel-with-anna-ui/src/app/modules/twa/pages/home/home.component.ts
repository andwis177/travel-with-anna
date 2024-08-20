import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {MenuComponent} from "../../components/menu/menu.component";
import {TripListComponent} from "../../components/trip-list/trip-list.component";
import {UserInformationService} from "../../../../services/user-information/user-information-service";
import {NgIf} from "@angular/common";
import {UsersListComponent} from "../../components/users-list/users-list.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    RouterOutlet,
    MenuComponent,
    TripListComponent,
    NgIf,
    UsersListComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})

export class HomeComponent implements OnInit  {
  role: string | null= '';
  isRoleAdmin: boolean = false;

  constructor(
    private userInformationService: UserInformationService
  ) {
  }

  ngOnInit(): void {
    this.role = this.userInformationService.getRole();
    if (this.role === 'ADMIN') {
      this.isRoleAdmin = true;
    }
  }

}
