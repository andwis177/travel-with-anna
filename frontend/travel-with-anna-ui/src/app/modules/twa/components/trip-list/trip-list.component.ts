import {Component, OnInit} from '@angular/core';
import {TripService} from "../../../../services/services/trip.service";
import {NgForOf} from "@angular/common";
import {TripCardComponent} from "./trip-card/trip-card.component";
import {LogoComponent} from "../../../components/menu/logo/logo.component";
import {UserComponent} from "../../../components/menu/user/user.component";
import {PageResponseTripResponse} from "../../../../services/models/page-response-trip-response";
import {ErrorService} from "../../../../services/error/error.service";
import {TripListButtonsComponent} from "./trip-list-buttons/trip-list-buttons.component";

@Component({
  selector: 'app-trip-list',
  standalone: true,
  imports: [
    NgForOf,
    TripCardComponent,
    LogoComponent,
    UserComponent,
    TripListButtonsComponent,
  ],
  templateUrl: './trip-list.component.html',
  styleUrl: './trip-list.component.scss'
})
export class TripListComponent implements OnInit  {
  errorMsg: string[] = [];
  tripResponse: PageResponseTripResponse = {};
  page: number = 0;
  size: number = 10;

  constructor(private tripService: TripService,
              private errorService: ErrorService) {}

  ngOnInit(): void {
    this.getAllTrips();
  }

  private getAllTrips() {
    this.errorMsg = [];
    this.tripService.getAllOwnersTrips({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (trips) => {
        this.tripResponse = trips;
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    })
  }

  goToFirstPage() {
    this.page = 0;
    this.getAllTrips();
  }

  goToPreviousPage() {
    this.page --;
    this.getAllTrips();
  }

  goToNextPage() {
    this.page++;
    this.getAllTrips();
  }

  goToLastPage() {
    this.page = this.tripResponse.totalPages as number - 1;
    this.getAllTrips();
  }

  get isLastPage() {
    return this.page === this.tripResponse.totalPages as number - 1;
  }
}
