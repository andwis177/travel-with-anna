import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {MatIconButton} from "@angular/material/button";
import {MatTooltip} from "@angular/material/tooltip";
import {NgOptimizedImage} from "@angular/common";

@Component({
  selector: 'app-logo',
  standalone: true,
  imports: [
    MatIconButton,
    MatTooltip,
    NgOptimizedImage
  ],
  templateUrl: './logo.component.html',
  styleUrl: './logo.component.scss'
})
export class LogoComponent {
  role: string | null = '';


  constructor(
    private router: Router,
  ) {
  }

  goToHomePage(event: Event) {
    event.preventDefault();
    this.router.navigate(['twa/']).then();
  }

}
