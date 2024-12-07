import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {TripService} from "../../../../../services/services/trip.service";
import {GetTripById$Params} from "../../../../../services/fn/trip/get-trip-by-id";
import {TripDetailsButtonsComponent} from "./trip-details-buttons/trip-details-buttons.component";
import {NgForOf, NgIf} from "@angular/common";
import {UserComponent} from "../../../../components/menu/user/user.component";
import {LogoComponent} from "../../../../components/menu/logo/logo.component";
import {TripResponse} from "../../../../../services/models/trip-response";
import {DayService} from "../../../../../services/services/day.service";
import {DayResponse} from "../../../../../services/models/day-response";
import {GetDays$Params} from "../../../../../services/fn/day/get-days";
import {DayCardComponent} from "./day/day-card/day-card.component";
import {SharedService} from "../../../../../services/shared/shared.service";
import {BudgetService} from "../../../../../services/services/budget.service";

@Component({
  selector: 'app-trip-details',
  standalone: true,
  imports: [
    LogoComponent,
    UserComponent,
    TripDetailsButtonsComponent,
    NgIf,
    NgForOf,
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
    private dayService: DayService,
    private budgetService: BudgetService,
    private sharedService: SharedService
  ) {
  }

  ngOnInit(): void {
    this.getTrip();
    this.sharedService.getDayTriggerEvent$.subscribe(() => {
      this.getDays(this.tripId);
    });
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

  getTrip(): void {
    const param: GetTripById$Params = {tripId: this.tripId};
    this.tripService.getTripById(param).subscribe({
      next: (trip) => {
        this.trip = trip;
        this.getDays(trip.tripId!);
        this.sharedService.setTrip(trip);
        this.sharedService.setTripDays(this.days);
        this.setTripCurrency();
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
        this.sharedService.setTripDays(days);
      },
      error: (err) => {
        console.error(err.error.errors);
      }
    });
  }

  setTripCurrency(): void {
    this.budgetService.getBudgetById({budgetId: this.trip.budgetId!}).subscribe({
      next: (budget) => {
        this.sharedService.setTripCurrency(budget.currency!);
      },
      error: (err) => {
        console.error(err.error.errors);
      }
    });
  }
}
