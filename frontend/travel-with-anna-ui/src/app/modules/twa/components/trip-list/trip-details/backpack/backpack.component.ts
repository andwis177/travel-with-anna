import {AfterViewInit, Component, HostListener, inject, Inject, OnInit, ViewChild} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {FormsModule} from "@angular/forms";
import {MatDivider} from "@angular/material/divider";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatToolbarRow} from "@angular/material/toolbar";
import {BackpackService} from "../../../../../../services/services/backpack.service";
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
import {SelectionModel} from "@angular/cdk/collections";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {MatTooltip} from "@angular/material/tooltip";
import {ItemRequest} from "../../../../../../services/models/item-request";
import {MatCheckbox} from "@angular/material/checkbox";
import {ItemService} from "../../../../../../services/services/item.service";
import {AddItemToBackpack$Params} from "../../../../../../services/fn/backpack/add-item-to-backpack";
import {MatSnackBar} from "@angular/material/snack-bar";
import {DeleteItem$Params} from "../../../../../../services/fn/backpack/delete-item";
import {ExpanseComponent} from "../expanse/expanse.component";
import {ItemResponse} from "../../../../../../services/models/item-response";
import {BackpackResponse} from "../../../../../../services/models/backpack-response";
import {ErrorService} from "../../../../../../services/error/error.service";
import {GetBudgetById$Params} from "../../../../../../services/fn/budget/get-budget-by-id";
import {BudgetService} from "../../../../../../services/services/budget.service";

@Component({
  selector: 'app-backpack',
  standalone: true,
  imports: [
    NgIf,
    FormsModule,
    MatDivider,
    MatIcon,
    MatIconButton,
    MatInput,
    MatToolbarRow,
    NgForOf,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatSort,
    MatSortHeader,
    MatTable,
    MatTooltip,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatHeaderCellDef,
    MatCheckbox
  ],
  templateUrl: './backpack.component.html',
  styleUrl: './backpack.component.scss'
})
export class BackpackComponent implements OnInit, AfterViewInit {
  errorMsg: Array<string> = [];
  backpackId: number;
  tripId: number;
  tripCurrency: string = '';
  budgetId: number;
  backpackResponse: BackpackResponse = {};
  items: ItemResponse[] = [];
  itemRequest: ItemRequest = {itemName: "", packed: false, quantity: ""};

  private _liveAnnouncer = inject(LiveAnnouncer);
  displayedColumns: string[] = [
    'isPacked',
    'itemName',
    'qty',
    'action'
  ];
  dataSource = new MatTableDataSource(this.items);
  @ViewChild(MatSort) sort!: MatSort;
  selection = new SelectionModel<ItemRequest>(false, []);
  currentRowIndex = -1;

  constructor(private backpackService: BackpackService,
              private itemService: ItemService,
              public dialog: MatDialog,
              private _snackBar: MatSnackBar,
              private errorService: ErrorService,
              private budgetService: BudgetService,
              @Inject(MAT_DIALOG_DATA) public data: { backpackId: number, tripId: number, budgetId: number }) {
    this.backpackId = data.backpackId;
    this.tripId = data.tripId;
    this.budgetId = data.budgetId;
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
    this.selection.clear();
  }

  ngOnInit() {
    this.selection.clear()
    this.receiveBackpack();
    this.receiveItems();
    if (this.dataSource.filteredData.length > 0) {
      this.currentRowIndex = 0;
      this.selectRowByIndex(this.currentRowIndex);
    }
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  getTripCurrency(): Promise<void> {
    return new Promise((resolve, reject) => {
      const params: GetBudgetById$Params = {budgetId: this.budgetId!};
      this.budgetService.getBudgetById(params).subscribe({
        next: (budget) => {
          this.tripCurrency = budget.currency!;
          resolve();
        },
        error: (err) => {
          console.error(err.error.errors);
          reject(err);
        }
      });
    });
  }

  selectRowByIndex(index: number): void {
    const row = this.dataSource.filteredData[index];
    if (row) {
      this.selection.clear();
      this.selection.select(row);
    }
  }

  toggleRow(row: ItemRequest) {
    this.currentRowIndex = this.dataSource.filteredData.indexOf(row);
    this.selection.clear();
    this.selection.select(row);
  }

  receiveBackpack(): void {
    this.backpackService.getBackpackById({ backpackId: this.backpackId }).subscribe((backpack) => {
      this.backpackResponse = backpack;
    });
  }

  receiveItems(): void {
    this.itemService.getAllItemsByBackpackId({ backpackId: this.backpackId })
      .subscribe({
        next:(items) => {
          this.items = items;
          this.dataSource = new MatTableDataSource(items);
          if (this.dataSource.filteredData.length > 0) {
            this.selectRowByIndex(0)
            this.toggleRow(this.dataSource.filteredData[0]);
          }
          this.dataSource.sort = this.sort;
        },
        error: (err) => {
          console.error(err);
        }
      });
  }

  announceSortChange(sortState: Sort) {
    if (sortState.direction) {
      this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`).then();
    } else {
      this._liveAnnouncer.announce('Sorting cleared').then();
    }
  }

  onClose() {
    this.dialog.closeAll();
  }

  addItem() {
    this.errorMsg = [];
    this.saveItems();
    const params: AddItemToBackpack$Params = {backpackId: this.backpackId, body: this.itemRequest};
    this.backpackService.addItemToBackpack(params).subscribe({
      next: () => {
        this.itemRequest = {itemName: "", packed: false, quantity: ""}
        this.receiveItems();
        this.selectRowByIndex(0);
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    });
  }

  saveItems() {
    this.errorMsg = [];
    this.itemService.saveAllItemsFromTheList({ body: this.items}).subscribe({
      next: () => {
        this.receiveItems();
        this._liveAnnouncer.announce('Items were saved successfully').then(() =>(this._snackBar.open("All items were saved", 'Close')));
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    });
  }

  onDelete(itemId: number, index: number) {
    this.errorMsg = [];
    this.selectRowByIndex(index);
    const params: DeleteItem$Params = {itemId: itemId};
    this.backpackService.deleteItem(params).subscribe({
      next: () => {
        this.receiveItems();
        this.selectRowByIndex(0);
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    });
  }

  async openExpanse(item: ItemResponse, index: number) {
    try {
      await this.getTripCurrency()
      this.selectRowByIndex(index)
      const dialogRef = this.dialog.open(ExpanseComponent, {
        maxWidth: '90vw',
        maxHeight: '100vh',
        width: 'auto',
        height: 'auto',
        id: 'expanse-dialog',
        data: {
          expanse: item.expanse,
          entityId: item.itemId,
          currency: this.tripCurrency,
          tripId: this.tripId,
          entityType: 'item',
          expanseCategory: 'BACKPACK: ' + item.itemName
        }
      });
      dialogRef.afterClosed().subscribe(() => {
        this.saveItems();
      });
    } catch (error) {
      console.error(error);}
  }
}
