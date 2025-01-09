import {AfterViewInit, Component, HostListener, inject, OnInit, ViewChild} from '@angular/core';
import {MatTooltip} from "@angular/material/tooltip";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource
} from "@angular/material/table";
import {MatSort, MatSortHeader, Sort} from "@angular/material/sort";
import { CommonModule } from '@angular/common';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {SelectionModel} from "@angular/cdk/collections";
import {MatDialog} from "@angular/material/dialog";
import {ExpanseComponent} from "../expanse/expanse.component";
import {BudgetExpensesRespond} from "../../../../../../services/models/budget-expenses-respond";
import {ExpanseResponse} from "../../../../../../services/models/expanse-response";
import {BudgetService} from "../../../../../../services/services/budget.service";
import {GetBudgetExpanses$Params} from "../../../../../../services/fn/budget/get-budget-expanses";
import {LogoComponent} from "../../../../../components/menu/logo/logo.component";
import {UserComponent} from "../../../../../components/menu/user/user.component";
import {BudgetButtonsComponent} from "./budget-buttons/budget-buttons.component";
import {SharedService} from "../../../../../../services/shared/shared.service";
import {TripResponse} from "../../../../../../services/models/trip-response";
import {firstValueFrom} from "rxjs";

@Component({
  selector: 'app-budget',
  standalone: true,
  imports: [
    MatTooltip,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatSort,
    MatSortHeader,
    MatTable,
    ReactiveFormsModule,
    FormsModule,
    MatHeaderCellDef,
    LogoComponent,
    UserComponent,
    BudgetButtonsComponent,
    CommonModule
  ],
  templateUrl: './budget.component.html',
  styleUrl: './budget.component.scss'
})
export class BudgetComponent implements OnInit, AfterViewInit {
  errorMsg: Array<string> = [];
  trip: TripResponse = {};
  tripId: number;
  budgetId: number;
  expanseId: number | undefined;
  budgetExpanse: BudgetExpensesRespond = {
    budgetResponse: {},
    expanses: [],
    overallPaidInTripCurrency: 0,
    overallPriceInTripCurrency: 0,
    sumsByCurrency: [],
    totalDebtInTripCurrency: 0
  };
  expanses: Array<ExpanseResponse> = [];

  private _liveAnnouncer = inject(LiveAnnouncer);
  displayedColumns: string[] = [
    'date',
    'expanseCategory',
    'expanseName',
    'currency',
    'price',
    'paid',
    'exchangeRate',
    'priceInTripCurrency',
    'paidInTripCurrency',
    'action'
  ];
  dataSource = new MatTableDataSource(this.budgetExpanse.expanses);
  @ViewChild(MatSort) sort!: MatSort;
  selection = new SelectionModel<ExpanseResponse>(false, []);
  currentRowIndex = -1;

  constructor(private budgetService: BudgetService,
              public dialog: MatDialog,
              private sharedService: SharedService) {

    this.sharedService.getTrip().subscribe({
      next: (trip) => {
        this.trip = trip!;
      }
    });

    this.tripId = this.trip.tripId!;
    this.budgetId = this.trip.budgetId!;

    if (this.dataSource.filteredData.length > 0) {
      this.currentRowIndex = 0;
      this.selectRowByIndex(this.currentRowIndex);
    }
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
    this.selection.clear();
  }

  ngOnInit() {
    this.selection.clear()
    this.getBudgetExpenses().then();
    if (this.dataSource.filteredData.length > 0) {
      this.currentRowIndex = 0;
      this.selectRowByIndex(this.currentRowIndex);
    }
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  selectRowByIndex(index: number): void {
    const row = this.dataSource.filteredData[index];
    if (row) {
      this.selection.clear();
      this.selection.select(row);
    }
  }

  toggleRow(row: ExpanseResponse) {
    this.currentRowIndex = this.dataSource.filteredData.indexOf(row);
    this.selection.clear();
    this.selection.select(row);
  }

  announceSortChange(sortState: Sort) {
    if (sortState.direction) {
      this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`).then();
    } else {
      this._liveAnnouncer.announce('Sorting cleared').then();
    }
  }

  async getBudgetExpenses(): Promise<void> {
    this.errorMsg = [];
    const params: GetBudgetExpanses$Params = {budgetId: this.budgetId, tripId: this.tripId};
    try {
      this.budgetExpanse = await firstValueFrom(this.budgetService.getBudgetExpanses(params));
      if (this.budgetExpanse) {
        this.dataSource = new MatTableDataSource(this.budgetExpanse.expanses);
        if (this.dataSource.data.length > 0) {
          this.selectRowByIndex(0)
          this.toggleRow(this.dataSource.data[0]);
        }
        this.dataSource.sort = this.sort;
      }
    } catch (error) {
      console.error(error);
    }
  }

  openExpanse(index: number): void {
    this.selectRowByIndex(index)
    if (this.budgetExpanse && this.budgetExpanse.expanses && this.budgetExpanse.expanses[index]) {
      this.expanseId = this.budgetExpanse.expanses[index].expanseId;
    } else {
      this.expanseId = -1;
    }
    const dialogRef = this.dialog.open(ExpanseComponent, {
      maxWidth: '90vw',
      maxHeight: '100vh',
      width: 'auto',
      height: 'auto',
      id: 'expanse-dialog',
      data: {
        expanse: this.budgetExpanse.expanses![index],
      }
    });
    dialogRef.afterClosed().subscribe(() => {
      this.getBudgetExpenses().then(() => {});
    });
  }

  getColorAmount(amount: number): string {
    if (amount < 0) {
      return 'negative-color';
    } else {
      return 'positive-color';
    }
  }

  formatAmount(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'decimal',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(amount)
  }

  formatExpanseCategory(category: string): string {
    return category.replace(/\n/g, '<br>');
  }
}
