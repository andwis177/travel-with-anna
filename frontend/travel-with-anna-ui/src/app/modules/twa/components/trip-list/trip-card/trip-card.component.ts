import {Component, Input} from '@angular/core';
import {MatDivider} from "@angular/material/divider";
import {Router} from "@angular/router";
import {NgIf} from "@angular/common";
import {TripResponse} from "../../../../../services/models/trip-response";

@Component({
  selector: 'app-trip-card',
  standalone: true,
  imports: [
    MatDivider,
    NgIf
  ],
  templateUrl: './trip-card.component.html',
  styleUrl: './trip-card.component.scss'
})
export class TripCardComponent {

  private _trip: TripResponse = {};

  constructor(
    private router: Router,) {
  }

  get trip(): TripResponse {
    return this._trip;
  }

  @Input()
  set trip(value: TripResponse) {
    this._trip = value;
  }

  selectTrip() {
    this.router.navigate(['/twa/trip-details', this._trip.tripId]).then();
  }
}
