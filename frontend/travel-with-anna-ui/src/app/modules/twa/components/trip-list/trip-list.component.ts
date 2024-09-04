import {Component, OnInit} from '@angular/core';
import {TripService} from "../../../../services/services/trip.service";
import {PageResponseTripDto} from "../../../../services/models/page-response-trip-dto";
import {NgForOf} from "@angular/common";
import {TripCardComponent} from "./trip-card/trip-card.component";
import {EditComponent} from "../users-list/edit/edit.component";
import {LogoComponent} from "../menu/logo/logo.component";
import {UserComponent} from "../menu/user/user.component";
import {TripListButtons} from "../menu/trip-list-buttons/trip-list-buttons.component";

@Component({
  selector: 'app-trip-list',
  standalone: true,
  imports: [
    NgForOf,
    TripCardComponent,
    EditComponent,
    LogoComponent,
    UserComponent,
    TripListButtons,
  ],
  templateUrl: './trip-list.component.html',
  styleUrl: './trip-list.component.scss'
})
export class TripListComponent implements OnInit  {
  errorMsg: string[] = [];
  tripResponse: PageResponseTripDto = {};
  page: number = 0;
  size: number = 20;


  constructor(
    private tripService: TripService,
  ) {}

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
         console.log(trips);
         this.tripResponse = trips;
         console.log(this.tripResponse);
       },
         error: (err) => {
           console.log(err.error.errors);
           if (err.error.errors && err.error.errors.length > 0) {
             this.errorMsg = err.error.errors;
           } else {
             this.errorMsg.push('Unexpected error occurred');
           }
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
