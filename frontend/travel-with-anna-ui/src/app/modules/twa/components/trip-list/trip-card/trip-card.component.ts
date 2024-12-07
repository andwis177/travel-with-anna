import {Component, Input} from '@angular/core';
import {Router} from "@angular/router";
import {NgForOf} from "@angular/common";
import {TripResponse} from "../../../../../services/models/trip-response";
import {AddressDetail} from "../../../../../services/models/address-detail";
import {ActivityService} from "../../../../../services/services/activity.service";

@Component({
  selector: 'app-trip-card',
  standalone: true,
  imports: [
    NgForOf
  ],
  templateUrl: './trip-card.component.html',
  styleUrl: './trip-card.component.scss'
})
export class TripCardComponent  {
  private _trip: TripResponse = {};
  dayOrDays: string = 'day';
  addressDetail: AddressDetail = {};

  constructor(private router: Router,
              public activityService: ActivityService,
  ) {
  }

  get trip(): TripResponse {
    return this._trip;
  }

  setDayOrDays(amountOfDays: number): void {
    if (amountOfDays > 1) {
      this.dayOrDays = 'days';
    }
  }

  @Input()
  set trip(value: TripResponse) {
    this._trip = value;
    this.setDayOrDays(this._trip.amountOfDays!);
    this.getCountries();
  }

  selectTrip() {
    this.router.navigate(['/twa/trip-details', this._trip.tripId]).then();
  }

  getCountries()  {
    this.activityService.fetchAddressDetailsByTripId({tripId: this._trip.tripId!})
      .subscribe({
        next: (addressDetail: AddressDetail) => {
          this.addressDetail = addressDetail;
        },
        error: (error: any) => {
          console.error(error);
        }
      })
  }
}
