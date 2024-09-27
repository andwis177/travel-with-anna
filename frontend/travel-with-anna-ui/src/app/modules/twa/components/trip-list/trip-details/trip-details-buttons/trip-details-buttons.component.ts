import {Component, Input, OnInit} from '@angular/core';
import {MatTooltip} from "@angular/material/tooltip";
import {MatDialog} from "@angular/material/dialog";
import {BackpackComponent} from "../backpack/backpack.component";
import {BudgetService} from "../../../../../../services/services/budget.service";
import {GetBudgetById$Params} from "../../../../../../services/fn/budget/get-budget-by-id";
import {TripResponse} from "../../../../../../services/models/trip-response";
import {Router} from "@angular/router";
import {NoteComponent} from "../note/note.component";
import {DeleteTripComponent} from "../delete-trip/delete-trip.component";

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
  @Input()_trip: TripResponse = {};
  tripCurrency: string = '';

  constructor(public dialog: MatDialog,
              private router: Router,
              private budgetService: BudgetService) {
  }

  ngOnInit(): void {
    this.getTripCurrency()
  }

  getTripCurrency() {
    const params: GetBudgetById$Params = {budgetId: this._trip.budgetId!};
    this.budgetService.getBudgetById(params).subscribe({
      next: (budget) => {
        this.tripCurrency = budget.currency!;
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
        tripCurrency: this.tripCurrency
      }
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }

  openBudget(event: Event) {
    event.preventDefault();
    console.log('open budget: ' + this._trip.budgetId);
    this.router.navigate(['/twa/budget', this._trip.tripId, this._trip.budgetId]).then();
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
        tripId: this._trip.tripId,
      }
    });
    dialogRef.afterClosed().subscribe(result => {
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
        tripId: this._trip.tripId,
      }
    });
    dialogRef.afterClosed().subscribe(result => {
    });
  }
}
