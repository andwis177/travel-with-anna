import {Component, OnInit} from '@angular/core';
import {Router, RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    RouterOutlet,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})

export class HomeComponent implements OnInit  {
  constructor(private router: Router)
  {}

  // @HostListener('window:beforeunload', ['$event'])
  // clearLocalStorage(event: any): void {
  //   localStorage.clear();
  // }

  ngOnInit(): void {
       this.router.navigate(['/twa/trip-list']).then();
  }

}
