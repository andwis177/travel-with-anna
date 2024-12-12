import {Component, Inject, OnInit} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatToolbarRow} from "@angular/material/toolbar";
import {NgClass, NgForOf} from "@angular/common";
import {ExpanseTotalByBadge} from "../../../../../../../services/models/expanse-total-by-badge";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {BudgetService} from "../../../../../../../services/services/budget.service";

@Component({
  selector: 'app-budget-badges',
  standalone: true,
  imports: [
    MatIcon,
    MatIconButton,
    MatToolbarRow,
    NgForOf,
    NgClass
  ],
  templateUrl: './budget-badges.component.html',
  styleUrl: './budget-badges.component.scss'
})
export class BudgetBadgesComponent implements OnInit {
  errorMsg: Array<string> = [];
  totalExpanses: Array<ExpanseTotalByBadge> = [];
  tripId: number = -1;
  currency: string = '';

  constructor(public dialog: MatDialog,
              private budgetService: BudgetService,
              @Inject(MAT_DIALOG_DATA) public data: { tripId: number, currency: string }) {
    this.tripId = data.tripId;
    this.currency = data.currency;
  }

  ngOnInit(): void {
    if (this.tripId !== -1) {
      this.getTotalExpanses();
    }
  }

  getTotalExpanses(): void {
    this.budgetService.getExpansesByBadgeByTripId({tripId: this.tripId}).subscribe({
      next: (totalExpanses) => {
        this.totalExpanses = totalExpanses;
      },
      error: (error) => {
        console.error(error.error.errors);
      }
    });
  }

  getPaidToMuchClass(totalExpanse: ExpanseTotalByBadge) : string {
    let isPaidToMuch: boolean = totalExpanse.totalPriceInTripCurrency! < totalExpanse.totalPaidInTripCurrency!;
    switch(isPaidToMuch) {
      case true:
        return 'paid-to-much';
      case false:
        return '';
    }
  }

  formatAmount(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'decimal',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(amount)
  }

  onClose() {
    this.dialog.getDialogById('budget-badges')?.close();
  }
}
