import {Component} from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {MenuComponent} from "../../components/menu/menu.component";
import {TripListComponent} from "../../components/trip-list/trip-list.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    RouterOutlet,
    MenuComponent,
    TripListComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})

export class HomeComponent  {

}
