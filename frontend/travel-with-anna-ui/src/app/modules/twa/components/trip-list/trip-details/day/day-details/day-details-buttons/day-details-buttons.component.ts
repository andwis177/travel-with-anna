import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatTooltip} from "@angular/material/tooltip";
import {DayResponse} from "../../../../../../../../services/models/day-response";
import {TripResponse} from "../../../../../../../../services/models/trip-response";
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";

import {ActivityComponent} from "../activity/activity/activity.component";
import {AddressDetail} from "../../../../../../../../services/models/address-detail";

@Component({
  selector: 'app-day-details-buttons',
  standalone: true,
  imports: [
    MatTooltip
  ],
  templateUrl: './day-details-buttons.component.html',
  styleUrl: './day-details-buttons.component.scss'
})
export class DayDetailsButtonsComponent  {
  @Input()_trip: TripResponse = {};
  @Input()_day: DayResponse = {};
  @Input()_addressDetail: AddressDetail = {};
  @Input()_dayOfWeek: string = '';
  @Input()_date: string = '';
  @Input()_dayNumber: number = 0;
  @Input()_amountOfDays: number = 0;
  @Output() _afterActivityClosed: EventEmitter<void> = new EventEmitter<void>();

  constructor(private router: Router,
              public dialog: MatDialog
  ) {
  }

  backToDays($event: Event) {
    $event.preventDefault();
    this.router.navigate(['/twa/trip-details', this._trip.tripId]).then();
  }

  addActivity(badge: string, associated: boolean, dayTag:boolean, isAddressSeparated: boolean, event: Event) {
    event.preventDefault();
    const dialogRef = this.dialog.open(ActivityComponent, {
      panelClass: 'custom-dialog-container',
      maxWidth: '90vw',
      maxHeight: '90vw',
      width: '40vw',
      height: 'auto',
      id: 'activity-dialog',
      data: {
        tripId: this._trip.tripId,
        startDate: this._day.date,
        lastTripDay: this._trip.endDate,
        badge: badge,
        associated: associated,
        dayTag: dayTag,
        isAddressSeparated: isAddressSeparated,
        lastCountry: this._addressDetail.lastCountry?.name,
        lastCountryCode: this._addressDetail.lastCountry?.iso2,
        lastCity: this._addressDetail.lastCity?.city,
        lastCountryCurrency: this._addressDetail.lastCountry?.currency,
      }
    });
    dialogRef.afterClosed().subscribe(() => {
      this._afterActivityClosed.emit();
    });
  }
}
