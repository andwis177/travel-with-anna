import {Component, OnInit} from '@angular/core';
import {TripDto} from "../../../../../services/models/trip-dto";
import {ActivatedRoute} from "@angular/router";
import {TripService} from "../../../../../services/services/trip.service";
import {GetTripById$Params} from "../../../../../services/fn/trip/get-trip-by-id";
import {LogoComponent} from "../../menu/logo/logo.component";
import {TripListButtons} from "../../menu/trip-list-buttons/trip-list-buttons.component";
import {UserComponent} from "../../menu/user/user.component";
import {MatFormField} from "@angular/material/form-field";

@Component({
  selector: 'app-trip-details',
  standalone: true,
  imports: [
    LogoComponent,
    TripListButtons,
    UserComponent,
    MatFormField
  ],
  templateUrl: './trip-details.component.html',
  styleUrl: './trip-details.component.scss'
})
export class TripDetailsComponent implements OnInit {
  trip: TripDto = {};
  constructor(
    private route: ActivatedRoute,
    private tripService: TripService
  ) {}

  ngOnInit(): void {
    this.receiveTrip()
  }

  receiveTrip(): void {
    const idString: string | null = this.route.snapshot.paramMap.get('id');
    if (idString) {
      const id = Number(idString);
      if (!isNaN(id)) {
        const param: GetTripById$Params = {tripId: id};
        this.tripService.getTripById(param).subscribe(trip => this.trip = trip);
      }
    }
  }



}
