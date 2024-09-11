import {Component, Input, OnInit} from '@angular/core';
import {MatTooltip} from "@angular/material/tooltip";
import {MatDialog} from "@angular/material/dialog";
import {BackpackComponent} from "../backpack/backpack.component";
import {TripRequest} from "../../../../../../services/models/trip-request";
import {BudgetService} from "../../../../../../services/services/budget.service";
import {GetBudgetById$Params} from "../../../../../../services/fn/budget/get-budget-by-id";

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
  @Input()_trip: TripRequest = {};
  tripCurrency: string = '';

  constructor(public dialog: MatDialog,
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
        console.log(this.tripCurrency)
      },
      error: (err) => {
        console.error(err.error.errors);
      }
    });
  }

  openBackpack(event: Event) {
    event.preventDefault();
    const dialogRef = this.dialog.open(BackpackComponent, {
      width: '70vw',
      height: '90vh',
      maxWidth: '70vw',
      maxHeight: '90vh',
      panelClass: 'full-screen-dialog',
      id: 'backpack-dialog',
      data: {
        backpackId: this._trip.backpackId,
        tripId: this._trip.tripId,
        tripCurrency: this.tripCurrency
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('Dialog closed, result:', result);
    });
  }
}
