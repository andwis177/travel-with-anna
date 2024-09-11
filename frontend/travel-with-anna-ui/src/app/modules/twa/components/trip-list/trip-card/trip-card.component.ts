import {Component, Input} from '@angular/core';
import {MatDivider} from "@angular/material/divider";
import {Router} from "@angular/router";
import {NgIf} from "@angular/common";
import {TripRequest} from "../../../../../services/models/trip-request";

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

  private _trip: TripRequest = {};

  constructor(
    private router: Router,) {
  }

  get trip(): TripRequest {
    return this._trip;
  }

  @Input()
  set trip(value: TripRequest) {
    this._trip = value;
  }

  selectTrip() {
    this.router.navigate(['/twa/trip-details', this._trip.tripId]).then();
  }
}
