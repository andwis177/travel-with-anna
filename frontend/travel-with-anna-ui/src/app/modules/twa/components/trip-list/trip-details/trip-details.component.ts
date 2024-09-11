import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {TripService} from "../../../../../services/services/trip.service";
import {GetTripById$Params} from "../../../../../services/fn/trip/get-trip-by-id";
import {TripListButtons} from "../trip-list-buttons/trip-list-buttons.component";
import {MatFormField} from "@angular/material/form-field";
import {TripDetailsButtonsComponent} from "./trip-details-buttons/trip-details-buttons.component";
import {NgIf} from "@angular/common";
import {TripRequest} from "../../../../../services/models/trip-request";
import {BudgetRequest} from "../../../../../services/models/budget-request";
import {UserComponent} from "../../../../components/menu/user/user.component";
import {LogoComponent} from "../../../../components/menu/logo/logo.component";

@Component({
  selector: 'app-trip-details',
  standalone: true,
  imports: [
    LogoComponent,
    TripListButtons,
    UserComponent,
    MatFormField,
    TripDetailsButtonsComponent,
    NgIf
  ],
  templateUrl: './trip-details.component.html',
  styleUrl: './trip-details.component.scss'
})
export class TripDetailsComponent implements OnInit {
  trip: TripRequest = {};
  budget: BudgetRequest = {};

  constructor(
    private route: ActivatedRoute,
    private tripService: TripService,
  ) {}

  ngOnInit(): void {
    this.receiveTrip();
  }

  receiveTrip(): void {
    const idString: string | null = this.route.snapshot.paramMap.get('id');
    if (idString) {
      const id = Number(idString);
      if (!isNaN(id)) {
        const param: GetTripById$Params = {tripId: id};
        this.tripService.getTripById(param).subscribe({
          next: (trip) => {
            this.trip = trip;
          },
          error: (err) => {
            console.error(err.error.errors);
          }
        });
      }
    }
  }
}
