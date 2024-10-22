import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {MatTooltip} from "@angular/material/tooltip";
import {MatDialog} from "@angular/material/dialog";
import {BackpackComponent} from "../backpack/backpack.component";
import {TripResponse} from "../../../../../../services/models/trip-response";
import {Router} from "@angular/router";
import {NoteComponent} from "../note/note.component";
import {DeleteTripComponent} from "../delete-trip/delete-trip.component";
import {AddDay$Params} from "../../../../../../services/fn/day/add-day";
import {DayService} from "../../../../../../services/services/day.service";
import {EditTripComponent} from "../edit-trip/edit-trip.component";
import {SharedService} from "../../../../../../services/shared/shared.service";

@Component({
  selector: 'app-trip-details-buttons',
  standalone: true,
  imports: [
    MatTooltip
  ],
  templateUrl: './trip-details-buttons.component.html',
  styleUrl: './trip-details-buttons.component.scss'
})
export class TripDetailsButtonsComponent implements OnInit {
  trip: TripResponse = {};
  @Output() afterAddDay: EventEmitter<void> = new EventEmitter<void>();

  constructor(public dialog: MatDialog,
              private router: Router,
              private dayService: DayService,
              private sharedService: SharedService
  ) {
  }

  ngOnInit(): void {
    this.getTrip();
  }

  getTrip() {
    this.sharedService.getTrip().subscribe({
      next: (trip) => {
        this.trip = trip!;
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
        backpackId: this.trip.backpackId,
        tripId: this.trip.tripId,
        budgetId: this.trip.budgetId,
      }
    });
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  openBudget(event: Event) {
    event.preventDefault();
    this.router.navigate(['/twa/budget']).then();
  }

  openNote(event: Event) {
    event.preventDefault();
    const dialogRef = this.dialog.open(NoteComponent, {
      maxWidth: '90vw',
      maxHeight: '90vh',
      width: 'auto',
      height: 'auto',
      id: 'note-dialog',
      data: {
        id: this.trip.tripId,
        relatedTo: 'trip'
      }
    });
    dialogRef.afterClosed().subscribe(() => {
    });
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
        tripId: this.trip.tripId,
      }
    });
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  addDay(isFirst: boolean, event: Event) {
    event.preventDefault();
    const params: AddDay$Params = {body: {tripId: this.trip.tripId!, first: isFirst}};
    this.dayService.addDay(params).subscribe({
      next: () => {
        this.afterAddDay.emit();
      },
      error: (err) => {
        console.error(err.error.errors);
      }
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
        tripId: this.trip.tripId,
        tripName: this.trip.tripName,
        startDate: this.trip.startDate,
        endDate: this.trip.endDate
      }
    });
    dialogRef.afterClosed().subscribe(() => {
      this.afterAddDay.emit();
    });
  }

  addLastDay(event: Event) {
    this.addDay(true, event);
  }

  addFirstDay(event: Event) {
    this.addDay(false, event);
  }
}
