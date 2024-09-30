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
import {MatCheckbox} from "@angular/material/checkbox";
import {MatDivider} from "@angular/material/divider";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatSort, MatSortHeader, Sort} from "@angular/material/sort";
import {MatToolbarRow} from "@angular/material/toolbar";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {SelectionModel} from "@angular/cdk/collections";
import {MatDialog} from "@angular/material/dialog";
import {ExpanseComponent} from "../expanse/expanse.component";
import {BudgetExpensesRespond} from "../../../../../../services/models/budget-expenses-respond";
import {ExpanseResponse} from "../../../../../../services/models/expanse-response";
import {BudgetService} from "../../../../../../services/services/budget.service";
import {GetBudgetExpanses$Params} from "../../../../../../services/fn/budget/get-budget-expanses";
import {ActivatedRoute} from "@angular/router";
import {LogoComponent} from "../../../../../components/menu/logo/logo.component";
import {TripDetailsButtonsComponent} from "../trip-details-buttons/trip-details-buttons.component";
import {UserComponent} from "../../../../../components/menu/user/user.component";
import {BudgetButtonsComponent} from "./budget-buttons/budget-buttons.component";

@Component({
  selector: 'app-budget',
  standalone: true,
  imports: [
    MatTooltip,
    MatCell,
    MatCellDef,
    MatCheckbox,
    MatColumnDef,
    MatDivider,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatIconButton,
    MatInput,
    MatRow,
    MatRowDef,
    MatSort,
    MatSortHeader,
    MatTable,
    MatToolbarRow,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    FormsModule,
    MatHeaderCellDef,
    NgClass,
    LogoComponent,
    TripDetailsButtonsComponent,
    UserComponent,
    BudgetButtonsComponent
  ],
  templateUrl: './budget.component.html',
  styleUrl: './budget.component.scss'
})
export class BudgetComponent implements OnInit, AfterViewInit {
  errorMsg: Array<string> = [];
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

  private _liveAnnouncer = inject(LiveAnnouncer);
  displayedColumns: string[] = [
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
              private route: ActivatedRoute,) {
    this.tripId = this.route.snapshot.paramMap.get('trip_id') as unknown as number;
    this.budgetId = this.route.snapshot.paramMap.get('budget_id') as unknown as number;
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
    this.selection.clear();
  }

  ngOnInit() {
    this.selection.clear()
    this.getBudgetExpenses();

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

  getBudgetExpenses() {
    this.errorMsg = [];
    const params: GetBudgetExpanses$Params = {budgetId: this.budgetId, tripId: this.tripId};
    this.budgetService.getBudgetExpanses(params).subscribe({
      next: (budgetExpanse) => {
        this.budgetExpanse = budgetExpanse;
        this.dataSource = new MatTableDataSource(this.budgetExpanse.expanses);
        if (this.dataSource.data.length > 0) {
          this.selectRowByIndex(0)
          this.toggleRow(this.dataSource.data[0]);
        }
        this.dataSource.sort = this.sort;
      },
      error: (err) => {
        console.error(err.error.errors);
      }
    });
  }

  openExpanse(index: number) {
    this.selectRowByIndex(index)
    if (this.budgetExpanse && this.budgetExpanse.expanses && this.budgetExpanse.expanses[index]) {
      this.expanseId = this.budgetExpanse.expanses[index].expanseId;
    } else {
      this.expanseId = -1;
    }
    const dialogRef = this.dialog.open(ExpanseComponent, {
      maxWidth: '90vw',
      maxHeight: '90vh',
      width: 'auto',
      height: 'auto',
      id: 'expanse-dialog',
      data: {
        currency: this.budgetExpanse.budgetResponse!.currency,
        expanseId: this.expanseId,
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      this.getBudgetExpenses();
    });
  }

  getColorAmount(amount: number): string {
    if (amount < 0) {
      return 'negative';
    } else {
      return 'positive';
    }
  }
}
