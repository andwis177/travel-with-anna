import {Component, OnInit} from '@angular/core';
import {Router, RouterOutlet} from "@angular/router";
import {TripListComponent} from "../../components/trip-list/trip-list.component";
import {NgIf} from "@angular/common";
import {LogoComponent} from "../../../components/menu/logo/logo.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    RouterOutlet,
    TripListComponent,
    NgIf,
    LogoComponent
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
