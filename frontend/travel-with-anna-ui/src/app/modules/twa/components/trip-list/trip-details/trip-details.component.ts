import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {TripService} from "../../../../../services/services/trip.service";
import {GetTripById$Params} from "../../../../../services/fn/trip/get-trip-by-id";
import {TripListButtons} from "../trip-list-buttons/trip-list-buttons.component";
import {MatFormField} from "@angular/material/form-field";
import {TripDetailsButtonsComponent} from "./trip-details-buttons/trip-details-buttons.component";
import {NgForOf, NgIf} from "@angular/common";
import {UserComponent} from "../../../../components/menu/user/user.component";
import {LogoComponent} from "../../../../components/menu/logo/logo.component";
import {TripResponse} from "../../../../../services/models/trip-response";
import {DayService} from "../../../../../services/services/day.service";
import {DayResponse} from "../../../../../services/models/day-response";
import {GetDays$Params} from "../../../../../services/fn/day/get-days";
import {TripCardComponent} from "../trip-card/trip-card.component";
import {DayCardComponent} from "./day/day-card/day-card.component";

@Component({
  selector: 'app-trip-details',
  standalone: true,
  imports: [
    LogoComponent,
    TripListButtons,
    UserComponent,
    MatFormField,
    TripDetailsButtonsComponent,
    NgIf,
    NgForOf,
    TripCardComponent,
    DayCardComponent
  ],
  templateUrl: './trip-details.component.html',
  styleUrl: './trip-details.component.scss'
})
export class TripDetailsComponent implements OnInit {
  tripId: number = this.grabTripId();
  trip: TripResponse = {};
  days: Array<DayResponse> = [];

  constructor(
    private route: ActivatedRoute,
    private tripService: TripService,
    private dayService: DayService
  ) {}

  ngOnInit(): void {
    this.receiveTrip();
  }

  grabTripId(): number {
    const idString: string | null = this.route.snapshot.paramMap.get('id');
    if (idString) {
      const id = Number(idString);
      if (!isNaN(id)) {
        return id;
      }
    }
    return -1;
  }

  receiveTrip(): void {
    const param: GetTripById$Params = {tripId: this.tripId};
    this.tripService.getTripById(param).subscribe({
      next: (trip) => {
        this.trip = trip;
        this.getDays(trip.tripId!);
      },
      error: (err) => {
        console.error(err.error.errors);
      }
    });
  }

  getDays(tripId: number): void {
    const params: GetDays$Params = {tripId: tripId};
    this.dayService.getDays(params).subscribe({
      next: (days) => {
        this.days = days;
      },
      error: (err) => {
        console.error(err.error.errors);
      }
    });
  }
}
