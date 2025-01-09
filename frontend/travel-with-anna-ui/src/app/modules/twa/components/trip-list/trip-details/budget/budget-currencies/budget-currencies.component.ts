import {AfterViewInit, Component, HostListener, Inject, inject, OnInit, ViewChild} from '@angular/core';
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
import {MatDivider} from "@angular/material/divider";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatSort, MatSortHeader, Sort} from "@angular/material/sort";
import {MatToolbarRow} from "@angular/material/toolbar";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {SelectionModel} from "@angular/cdk/collections";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {ExpanseByCurrency} from "../../../../../../../services/models/expanse-by-currency";

@Component({
  selector: 'app-budget-currencies',
  standalone: true,
  imports: [
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatDivider,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatIconButton,
    MatRow,
    MatRowDef,
    MatSort,
    MatSortHeader,
    MatTable,
    MatToolbarRow,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    MatHeaderCellDef,
    FormsModule,
    NgClass
  ],
  templateUrl: './budget-currencies.component.html',
  styleUrl: './budget-currencies.component.scss'
})
export class BudgetCurrenciesComponent implements OnInit, AfterViewInit {
  errorMsg: Array<string> = [];
  sumsByCurrency: Array<ExpanseByCurrency> = [];
  tripCurrency: string = '';
  private _liveAnnouncer = inject(LiveAnnouncer);
  displayedColumns: string[] = [
    'currency',
    'totalDebt',
    'totalPrice',
    'totalPaid',
    'totalPriceInTripCurrency',
    'totalPaidInTripCurrency',
  ];

  dataSource = new MatTableDataSource(this.sumsByCurrency);
  @ViewChild(MatSort) sort!: MatSort;
  selection = new SelectionModel<ExpanseByCurrency>(false, []);
  currentRowIndex = -1;

  constructor(public dialog: MatDialog,
              @Inject(MAT_DIALOG_DATA) public data: { sumsByCurrency: Array<ExpanseByCurrency>,
                currency: string }) {
    this.sumsByCurrency = data.sumsByCurrency;
    this.tripCurrency = data.currency;
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
    this.selection.clear();
  }

  ngOnInit() {
    this.selection.clear()
    this.dataSource.data = this.sumsByCurrency;
    this.selection.clear()

    if (this.dataSource.data.length > 0) {
      this.currentRowIndex = 0;
      this.selectRowByIndex(this.currentRowIndex);
    }
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  selectRowByIndex(index: number): void {
    const row = this.dataSource.data[index];
    if (row) {
      this.selection.clear();
      this.selection.select(row);
    }
  }

  toggleRow(row: ExpanseByCurrency) {
    this.currentRowIndex = this.dataSource.data.indexOf(row);
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

  formatAmount(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'decimal',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(amount)
  }

  getColorAmount(amount: number): string {
    if (amount < 0) {
      return 'negative-color';
    } else {
      return 'default';
    }
  }

  onClose() {
    this.dialog.closeAll();
  }
}
