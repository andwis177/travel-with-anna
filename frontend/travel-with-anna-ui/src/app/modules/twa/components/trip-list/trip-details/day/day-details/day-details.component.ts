import {Component, OnInit} from '@angular/core';
import {LogoComponent} from "../../../../../../components/menu/logo/logo.component";
import {NgForOf} from "@angular/common";
import {TripCardComponent} from "../../../trip-card/trip-card.component";
import {TripListButtons} from "../../../trip-list-buttons/trip-list-buttons.component";
import {UserComponent} from "../../../../../../components/menu/user/user.component";
import {DayDetailsButtonsComponent} from "./day-details-buttons/day-details-buttons.component";
import {ActivatedRoute} from "@angular/router";
import {DayResponse} from "../../../../../../../services/models/day-response";
import {TripResponse} from "../../../../../../../services/models/trip-response";
import {DayService} from "../../../../../../../services/services/day.service";
import {TripService} from "../../../../../../../services/services/trip.service";
import {ErrorService} from "../../../../../../../services/error/error.service";
import {GetTripById$Params} from "../../../../../../../services/fn/trip/get-trip-by-id";
import {GetDayById$Params} from "../../../../../../../services/fn/day/get-day-by-id";

@Component({
  selector: 'app-day-details',
  standalone: true,
  imports: [
    LogoComponent,
    NgForOf,
    TripCardComponent,
    TripListButtons,
    UserComponent,
    DayDetailsButtonsComponent
  ],
  templateUrl: './day-details.component.html',
  styleUrl: './day-details.component.scss'
})
export class DayDetailsComponent implements OnInit {
  errorMsg: Array<string> = [];
  day_id: number = -1;
  day: DayResponse = {};
  trip: TripResponse = {};


  constructor(private route: ActivatedRoute,
              private dayService: DayService,
              private tripService: TripService,
              private errorService: ErrorService) {
    this.day_id = this.route.snapshot.paramMap.get('day_id') as unknown as number;
  }

  ngOnInit(): void {
    this.getDay()

  }

  getDay(): void {
    this.errorMsg = [];
    const params: GetDayById$Params = {dayId: this.day_id};
    this.dayService.getDayById(params).subscribe({
      next: (day: DayResponse) => {
        this.day = day;
        this.getTrip(day.tripId!);
      },
      error: (error) => {
        this.errorMsg = this.errorService.errorHandler(error);
      }
    });
  }

  getTrip(tripId: number): void {
    this.errorMsg = [];
    const params: GetTripById$Params = {tripId: tripId};
    this.tripService.getTripById(params).subscribe({
      next: (trip: TripResponse) => {
        this.trip = trip;
      },
      error: (error) => {
        this.errorMsg = this.errorService.errorHandler(error);
      }
    });
  }
}
