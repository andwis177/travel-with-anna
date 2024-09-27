import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatTooltip} from "@angular/material/tooltip";
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {BudgetResponse} from "../../../../../../../services/models/budget-response";
import {BudgetEditComponent} from "../budget-edit/budget-edit.component";
import {ExpanseByCurrency} from "../../../../../../../services/models/expanse-by-currency";
import {BudgetCurrenciesComponent} from "../budget-currencies/budget-currencies.component";

@Component({
  selector: 'app-budget-buttons',
  standalone: true,
  imports: [
    MatTooltip
  ],
  templateUrl: './budget-buttons.component.html',
  styleUrl: './budget-buttons.component.scss'
})
export class BudgetButtonsComponent {
@Input()_tripId: number = -1;
@Input()_budget: BudgetResponse = {};
@Input()_sumsByCurrency: Array<ExpanseByCurrency> = [];
@Output() afterSave: EventEmitter<void> = new EventEmitter<void>();

constructor(private router: Router,
            public dialog: MatDialog) {
}

  backToTrip($event: Event)  {
    $event.preventDefault();
    this.router.navigate(['/twa/trip-details', this._tripId]).then();

  }

  editBudget($event: Event) {
    $event.preventDefault();
    const dialogRef = this.dialog.open(BudgetEditComponent, {
      maxWidth: '90vw',
      maxHeight: '90vh',
      width: 'auto',
      height: 'auto',
      id: 'budget-edit',
      data: {
        budget: this._budget
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      this.afterSave.emit();
    });
  }

  openBudgetCurrencies($event: Event) {
    $event.preventDefault();
    const dialogRef = this.dialog.open(BudgetCurrenciesComponent, {
      maxWidth: '90vw',
      maxHeight: '90vh',
      width: 'auto',
      height: 'auto',
      id: 'budget-currencies',
      data: {
        sumsByCurrency: this._sumsByCurrency,
        currency: this._budget.currency
      }
    });
    dialogRef.afterClosed().subscribe(result => {
    });
  }
}
