import {AfterViewInit, Component, HostListener, inject, Inject, OnInit, ViewChild} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {FormsModule} from "@angular/forms";
import {MatCard, MatCardActions, MatCardHeader} from "@angular/material/card";
import {MatDivider} from "@angular/material/divider";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatToolbarRow} from "@angular/material/toolbar";
import {CdkFixedSizeVirtualScroll, CdkVirtualForOf, CdkVirtualScrollViewport} from "@angular/cdk/scrolling";
import {BackpackService} from "../../../../../../services/services/backpack.service";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatNoDataRow,
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
import {TripDetailsButtonsComponent} from "../trip-details-buttons/trip-details-buttons.component";
import {ItemService} from "../../../../../../services/services/item.service";
import {AddItemToBackpack$Params} from "../../../../../../services/fn/backpack/add-item-to-backpack";
import {MatSnackBar} from "@angular/material/snack-bar";
import {DeleteItem$Params} from "../../../../../../services/fn/backpack/delete-item";
import {ExpanseComponent} from "../expanse/expanse.component";
import {ErrorService} from "../../../../../../services/error/error.service";
import {LogoComponent} from "../../../../../components/menu/logo/logo.component";
import {UserComponent} from "../../../../../components/menu/user/user.component";
import {ItemResponse} from "../../../../../../services/models/item-response";
import {BackpackResponse} from "../../../../../../services/models/backpack-response";

@Component({
  selector: 'app-backpack',
  standalone: true,
  imports: [
    NgIf,
    FormsModule,
    MatCard,
    MatCardActions,
    MatCardHeader,
    MatDivider,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatToolbarRow,
    NgForOf,
    CdkVirtualScrollViewport,
    CdkFixedSizeVirtualScroll,
    CdkVirtualForOf,
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
    MatCheckbox,
    LogoComponent,
    TripDetailsButtonsComponent,
    UserComponent,
    MatNoDataRow
  ],
  templateUrl: './backpack.component.html',
  styleUrl: './backpack.component.scss'
})
export class BackpackComponent implements OnInit, AfterViewInit {
  errorMsg: Array<string> = [];
  backpackId: number;
  tripId: number;
  tripCurrency: string = '';
  backpackRequest: BackpackResponse = {};
  items: ItemResponse[] = [];
  itemRequest: ItemRequest = {itemId: -1, item: "", packed: false, qty: '1'};

  private _liveAnnouncer = inject(LiveAnnouncer);
  displayedColumns: string[] = [
    'isPacked',
    'item',
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
              @Inject(MAT_DIALOG_DATA) public data: { backpackId: number, tripId: number, tripCurrency: string }) {
    this.backpackId = data.backpackId;
    this.tripId = data.tripId;
    this.tripCurrency = data.tripCurrency;
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
    this.selection.clear();
  }

  @HostListener('document:keydown.ArrowDown', ['$event'])
  onArrowDownKeydownHandler(event: KeyboardEvent): void {
    if (this.currentRowIndex < this.dataSource.data.length - 1) {
      this.currentRowIndex++;
      this.selectRowByIndex(this.currentRowIndex);
    }
  }

  @HostListener('document:keydown.ArrowUp', ['$event'])
  onArrowUpKeydownHandler(event: KeyboardEvent): void {
    if (this.currentRowIndex > 0) {
      this.currentRowIndex--;
      this.selectRowByIndex(this.currentRowIndex);
    }
  }

  ngOnInit() {
    this.selection.clear()
    this.receiveBackpack();
    this.receiveItems();

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

  toggleRow(row: ItemRequest) {
    this.currentRowIndex = this.dataSource.data.indexOf(row);
    this.selection.clear();
    this.selection.select(row);
  }

  receiveBackpack(): void {
    this.backpackService.getBackpackById({ backpackId: this.backpackId }).subscribe((backpack) => {
      this.backpackRequest = backpack;
    });
  }

  receiveItems(): void {
    this.itemService.getAllItemsByBackpackId({ backpackId: this.backpackId }).subscribe((items) => {
      this.items = items;
      this.dataSource = new MatTableDataSource(items);
      if(this.dataSource.data.length > 0) {
        this.selectRowByIndex(0)
        this.toggleRow(this.dataSource.data[0]);
      }
      this.dataSource.sort = this.sort;
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
    const params: AddItemToBackpack$Params = {backpackId: this.backpackId, body: this.itemRequest};
    this.backpackService.addItemToBackpack(params).subscribe({
      next: (response) => {
        this.itemRequest = {itemId: -1, item: "", packed: false, qty: '1'}
        this.receiveItems();
        this.selectRowByIndex(0);
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    });
  }

  saveItems($event: MouseEvent) {
    $event.preventDefault();
    this.errorMsg = [];
    this.itemService.saveAllItemsFromTheList({ body: this.items}).subscribe({
      next: (response) => {
        this._liveAnnouncer.announce('Items were saved successfully').then(r =>(this._snackBar.open("All items were saved", 'Close')));
        console.log(response);
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

  openExpanse(item: ItemRequest, index: number) {
    this.selectRowByIndex(index)
    console.log('openExpanse', item.itemId);
    const dialogRef = this.dialog.open(ExpanseComponent, {
      width: '90vw',
      height: '90vh',
      maxWidth: '90vw',
      maxHeight: '90vh',
      id: 'expanse-dialog',
      data: {
        item: item.item,
        itemId: item.itemId,
        currency: this.tripCurrency,
        tripId: this.tripId,
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('Dialog closed, result:', result);
    });
  }
}
