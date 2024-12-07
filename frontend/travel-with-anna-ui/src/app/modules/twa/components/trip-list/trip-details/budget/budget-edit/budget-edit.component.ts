import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {FormsModule} from "@angular/forms";
import {MatDivider} from "@angular/material/divider";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";

import {MatToolbarRow} from "@angular/material/toolbar";
import {NgForOf, NgIf} from "@angular/common";
import {CountryCurrency} from "../../../../../../../services/models/country-currency";
import {MatOption, MatSelect, MatSelectTrigger} from "@angular/material/select";
import {CountryControllerService} from "../../../../../../../services/services/country-controller.service";
import {BudgetService} from "../../../../../../../services/services/budget.service";
import {BudgetRequest} from "../../../../../../../services/models/budget-request";
import {ErrorService} from "../../../../../../../services/error/error.service";
import {SharedService} from "../../../../../../../services/shared/shared.service";

@Component({
  selector: 'app-budget-edit',
  standalone: true,
  imports: [
    FormsModule,
    MatDivider,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatOption,
    MatSelect,
    MatToolbarRow,
    NgForOf,
    NgIf,
    MatSelectTrigger
  ],
  templateUrl: './budget-edit.component.html',
  styleUrl: './budget-edit.component.scss'
})
export class BudgetEditComponent {
  errorMsg: Array<string> = [];
  currency: CountryCurrency[] = [];
  budget: BudgetRequest = {currency: "", toSpend: 0};

  constructor(public dialog: MatDialog,
              private budgetService: BudgetService,
              private countryControllerService: CountryControllerService,
              private sharedService: SharedService,
              private errorService: ErrorService,
              @Inject(MAT_DIALOG_DATA) public data: {budget: {currency: "", toSpend: 0}}) {
    this.budget = data.budget;
    this.getCurrency();
  }

  onClose() {
    const dialogRef = this.dialog.getDialogById('budget-edit');
    if (dialogRef) {
      dialogRef.close();
    } else {
      console.warn('Dialog with ID "Budget Edit" is not open.');
    }
  }

  saveBudget() {
    this.errorMsg = [];
    this.budgetService.updateBudget({ body: this.budget }).subscribe({
      next: () => {
        this.sharedService.setTripCurrency(this.budget.currency!);
        this.onClose();
      },
      error: (error) => this.errorMsg = this.errorService.errorHandler(error)
    });
  }

  getCurrency() {
    this.countryControllerService.findAllCountryCurrencies().subscribe({
      next: (currency) => this.currency = currency,
      error: (error) => this.errorMsg = this.errorService.errorHandler(error)
    });
  }
}
