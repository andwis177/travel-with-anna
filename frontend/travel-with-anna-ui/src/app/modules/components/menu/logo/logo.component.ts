import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {MatIconButton} from "@angular/material/button";
import {MatTooltip} from "@angular/material/tooltip";
import {UserService} from "../../../../services/services/user.service";

@Component({
  selector: 'app-logo',
  standalone: true,
  imports: [
    MatIconButton,
    MatTooltip
  ],
  templateUrl: './logo.component.html',
  styleUrl: './logo.component.scss'
})
export class LogoComponent {
  errorMsg = [];

  constructor(
    private router: Router,
    private userService: UserService,
  ) {
  }

  goToHomePage(event: Event) {
    event.preventDefault();
    this.errorMsg = [];
    this.userService.fetchRole().subscribe({
      next: (response) => {
        if (response.roleName === 'ADMIN') {
          this.router.navigate(['/manager/manager-main']).then();
        }
        if (response.roleName === 'USER') {
          this.router.navigate(['twa']).then();
        }
      },
      error: (err: any) => {
        console.error(err);
      }
    })
  }
}
