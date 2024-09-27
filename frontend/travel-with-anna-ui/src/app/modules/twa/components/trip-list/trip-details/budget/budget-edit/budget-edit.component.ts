import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {FormsModule} from "@angular/forms";
import {MatCard, MatCardActions} from "@angular/material/card";
import {MatDivider} from "@angular/material/divider";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";

import {MatToolbarRow} from "@angular/material/toolbar";
import {NgForOf, NgIf} from "@angular/common";
import {BudgetResponse} from "../../../../../../../services/models/budget-response";
import {CountryCurrency} from "../../../../../../../services/models/country-currency";
import {MatOption, MatSelect} from "@angular/material/select";
import {CountryControllerService} from "../../../../../../../services/services/country-controller.service";
import {BudgetService} from "../../../../../../../services/services/budget.service";
import {BudgetRequest} from "../../../../../../../services/models/budget-request";
import {ErrorService} from "../../../../../../../services/error/error.service";

@Component({
  selector: 'app-budget-edit',
  standalone: true,
  imports: [
    FormsModule,
    MatCard,
    MatCardActions,
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
    NgIf
  ],
  templateUrl: './budget-edit.component.html',
  styleUrl: './budget-edit.component.scss'
})
export class BudgetEditComponent implements OnInit {
  errorMsg: Array<string> = [];
  currency: CountryCurrency[] = [];
  budget: BudgetRequest = {};


  constructor(public dialog: MatDialog,
              private budgetService: BudgetService,
              private countryControllerService: CountryControllerService,
              private errorService: ErrorService,
              @Inject(MAT_DIALOG_DATA) public data: {budget: BudgetResponse}) {
    this.budget = data.budget;
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
        this.onClose();
      },
      error: (error) => this.errorMsg = this.errorService.errorHandler(error)
    });
  }

  ngOnInit(): void {
    this.getCurrency();
  }

  getCurrency() {
    this.countryControllerService.findAllCountryCurrencies().subscribe({
      next: (currency) => this.currency = currency,
      error: (error) => this.errorMsg = this.errorService.errorHandler(error)
    });
  }
}
