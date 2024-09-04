import {Component, Input} from '@angular/core';
import {TripDto} from "../../../../../services/models/trip-dto";
import {MatDivider} from "@angular/material/divider";
import {Router} from "@angular/router";
import {NgIf} from "@angular/common";

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

  private _trip: TripDto = {};

  constructor(
    private router: Router,) {
  }

  get trip(): TripDto {
    return this._trip;
  }

  @Input()
  set trip(value: TripDto) {
    this._trip = value;
  }

  selectTrip() {
    this.router.navigate(['/twa/trip-details', this._trip.tripId]).then();
  }
}
