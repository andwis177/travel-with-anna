import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MatTooltip} from "@angular/material/tooltip";
import {MatDialog} from "@angular/material/dialog";
import {BackpackComponent} from "../backpack/backpack.component";
import {TripResponse} from "../../../../../../services/models/trip-response";
import {Router} from "@angular/router";
import {DeleteTripComponent} from "../delete-trip/delete-trip.component";
import {AddDay$Params} from "../../../../../../services/fn/day/add-day";
import {DayService} from "../../../../../../services/services/day.service";
import {EditTripComponent} from "../edit-trip/edit-trip.component";
import {SharedService} from "../../../../../../services/shared/shared.service";
import {NgClass} from "@angular/common";
import {MatBadge} from "@angular/material/badge";
import {MatIcon} from "@angular/material/icon";
import {DayDeleteComponent} from "../day/day-card/day-delete/day-delete.component";

@Component({
  selector: 'app-trip-details-buttons',
  standalone: true,
  imports: [
    MatTooltip,
    NgClass,
    MatBadge,
    MatIcon
  ],
  templateUrl: './trip-details-buttons.component.html',
  styleUrl: './trip-details-buttons.component.scss'
})
export class TripDetailsButtonsComponent implements OnInit {
  private _trip: TripResponse = {};
  @Output() afterAddDay: EventEmitter<void> = new EventEmitter<void>();

  constructor(public dialog: MatDialog,
              private router: Router,
              private dayService: DayService,
              private sharedService: SharedService
  ) {
  }

  get trip(): TripResponse {
    return this._trip;
  }

  @Input()
  set trip(value: TripResponse) {
    this._trip = value;
  }

  ngOnInit(): void {
    this.getTrip();
  }

  getTrip() {
    this.sharedService.getTrip().subscribe({
      next: (trip) => {
        this._trip = trip!;
      },
      error: (err) => {
        console.error(err.error.errors);
      }
    });
  }

  openBackpack(event: Event) {
    event.preventDefault();
    const dialogRef = this.dialog.open(BackpackComponent, {
      maxWidth: '90vw',
      maxHeight: '90vh',
      width: '70%',
      height: 'auto',
      id: 'backpack-dialog',
      data: {
        backpackId: this._trip.backpackId,
        tripId: this._trip.tripId,
        budgetId: this._trip.budgetId,
      }
    });
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  openBudget(event: Event) {
    event.preventDefault();
    this.router.navigate(['/twa/budget']).then();
  }

  deleteTrip(event: Event) {
    event.preventDefault();
    const dialogRef = this.dialog.open(DeleteTripComponent, {
      maxWidth: '90vw',
      maxHeight: '90vh',
      width: 'auto',
      height: 'auto',
      id: 'delete-trip-dialog',
      data: {
        tripId: this._trip.tripId,
      }
    });
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  addDay(isFirst: boolean, event: Event) {
    event.preventDefault();
    const params: AddDay$Params = {body: {tripId: this._trip.tripId!, first: isFirst}};
    this.dayService.addDay(params).subscribe({
      next: () => {
        this.afterAddDay.emit();
      },
      error: (err) => {
        console.error(err.error.errors);
      }
    });
  }

  deleteDay(isFirst: boolean, event: Event) {
    event.preventDefault();
    console.log(this._trip.tripId)
    const dialogRef = this.dialog.open(DayDeleteComponent, {
      panelClass: 'custom-dialog-container',
      maxWidth: '50vw',
      maxHeight: '50vw',
      width: '50vw',
      height: 'auto',
      id: 'delete-day-dialog',
      data: {
        tripId: this._trip.tripId,
        isFirst: isFirst
      }
    });
    dialogRef.afterClosed().subscribe(() => {
      this.afterAddDay.emit();
    });
  }

  editTrip(event: Event) {
    event.preventDefault();
    const dialogRef = this.dialog.open(EditTripComponent, {
      maxWidth: '90vw',
      maxHeight: '90vh',
      width: 'auto',
      height: 'auto',
      id: 'edit-trip-dialog',
      data: {
        tripId: this._trip.tripId,
        tripName: this._trip.tripName,
        startDate: this._trip.startDate,
        endDate: this._trip.endDate
      }
    });
    dialogRef.afterClosed().subscribe(() => {
      this.afterAddDay.emit();
    });
  }
}
