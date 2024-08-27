import {AfterViewInit, Component, HostListener, inject, OnInit, ViewChild} from '@angular/core';
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
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {AdminService} from "../../../../services/services/admin.service";
import {PageResponseUserAdminView} from "../../../../services/models/page-response-user-admin-view";
import {SelectionModel} from "@angular/cdk/collections";
import {UserAdminView} from "../../../../services/models/user-admin-view";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatRadioButton} from "@angular/material/radio";
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatIconButton} from "@angular/material/button";
import {NgIf} from "@angular/common";
import {MatTooltip} from "@angular/material/tooltip";
import {MatDialog} from "@angular/material/dialog";
import {DeleteComponent} from "./delete/delete.component";
import {SharedService} from "../../../../services/shared/shared.service";
import {EditComponent} from "./edit/edit.component";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormsModule} from "@angular/forms";
import {MatCardContent} from "@angular/material/card";
import {GetUserAdminViewByIdentifier$Params} from "../../../../services/fn/admin/get-user-admin-view-by-identifier";
import {ImageComponent} from "./image/image.component";


@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [
    MatTable,
    MatSort,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCell,
    MatCellDef,
    MatSortHeader,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatCheckbox,
    MatRadioButton,
    MatIcon,
    MatIconButton,
    MatButton,
    NgIf,
    MatTooltip,
    MatFormField,
    MatInput,
    FormsModule,
    MatLabel,
    MatCardContent
  ],
  templateUrl: './users-list.component.html',
  styleUrl: './users-list.component.scss'
})
export class UsersListComponent implements OnInit, AfterViewInit {
  errorMsg: Array<string> = [];
  userAdminViewList: PageResponseUserAdminView = {
    content: [],
    first: false,
    last: false,
    number: 0,
    size: 0,
    totalElements: 0,
    totalPages: 0
  };
  page = 0;
  size = 1;
  pages: any = [];
  message = '';
  level: 'success' | 'error' = 'success';
  private _liveAnnouncer = inject(LiveAnnouncer);
  displayedColumns: string[] = [
    'avatar',
    'userId',
    'userName',
    'email',
    'accountLocked',
    'enabled',
    'createdDate',
    'lastModifiedDate',
    'roles',
    'action'
  ];
  dataSource = new MatTableDataSource<any>([]);
  @ViewChild(MatSort) sort!: MatSort;
  selection = new SelectionModel<UserAdminView>(false, []);
  identifier: string = '';
  currentRowIndex = -1;


  constructor(public dialog: MatDialog,
              private adminService: AdminService,
              private shareService: SharedService,
  ) {
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

  @HostListener('document:keydown.ArrowRight', ['$event'])
  onArrowRightKeydownHandler(event: KeyboardEvent): void {
    if (this.isLastPage){
      this.goToFirstPage();
    } else {
      this.goToNextPage();
    }
  }

  @HostListener('document:keydown.ArrowLeft', ['$event'])
  onArrowLeftKeydownHandler(event: KeyboardEvent): void {
    if (this.page === 0){
      this.goToLastPage();
    } else {
      this.goToPreviousPage();
    }
  }

  selectRowByIndex(index: number): void {
    const row = this.dataSource.data[index];
    if (row) {
      this.selection.clear();
      this.selection.select(row);
      this.shareService.setUserAdminEditId(row.userId as number);
    }
  }

  ngOnInit(): void {
    this.selection.clear()
    this.loadUsers()
    this.findUserData();

    if (this.dataSource.data.length > 0) {
      this.currentRowIndex = 0;
      this.selectRowByIndex(this.currentRowIndex);
    }
  }

  findUserData() {
    this.shareService.userAdminViewIdentifier$.subscribe((identifier: string) => {
      if (identifier !== '') {
        this.getUserAdminView(identifier);
      } else {
        this.loadUsers()
      }
    });
  }

  announceSortChange(sortState: Sort) {
    if (sortState.direction) {
      this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`).then(r => console.log(r));
    } else {
      this._liveAnnouncer.announce('Sorting cleared').then(r => console.log(r));
    }
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  loadUsers() {
    this.adminService.getAllUsers({
      page: this.page,
      size: this.size
    })
      .subscribe({
        next: (users) => {
          this.userAdminViewList = users;
          this.pages = Array(this.userAdminViewList.totalPages)
            .fill(0)
            .map((x, i) => i);
          if (this.userAdminViewList.content) {
            this.dataSource.data = this.userAdminViewList.content;
          }
        },
        error: (error) => {
          console.error('Failed to load users', error);
        }
      });
  }

  goToFirstPage() {
    this.page = 0;
    this.loadUsers();
  }

  goToPreviousPage() {
    this.page --;
    this.loadUsers();
  }

  goToLastPage() {
    this.page = this.userAdminViewList.totalPages as number - 1;
    this.loadUsers();
  }

  goToNextPage() {
    this.page++;
    this.loadUsers();
  }

  get isLastPage() {
    return this.page === this.userAdminViewList.totalPages as number - 1;
  }

  toggleRow(row: UserAdminView) {
    this.currentRowIndex = this.dataSource.data.indexOf(row);
    this.selection.clear();
    this.selection.select(row);
    this.shareService.setUserAdminEditId(row.userId as number);
  }

  onEdit() {
    const dialogRef = this.dialog.open(EditComponent, {})
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  onAvatar() {
    const dialogRef = this.dialog.open(ImageComponent, {})
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  onDelete() {
    const dialogRef = this.dialog.open(DeleteComponent, {})
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  getUserAdminView(identifier: string) {
    const params: GetUserAdminViewByIdentifier$Params = {identifier: identifier};
    this.adminService.getUserAdminViewByIdentifier(params).subscribe({
      next: (user) => {
        this.userAdminViewList.content = [user];
        this.userAdminViewList.last = true;
        this.userAdminViewList.first = true;
        this.userAdminViewList.number = 0;
        this.userAdminViewList.size = 1;
        this.userAdminViewList.totalElements = 1;
        this.userAdminViewList.totalPages = 1;
        this.dataSource.data = this.userAdminViewList.content;

      },
      error: (err) => {
        console.log(err.error.errors);
        if (err.error.errors && err.error.errors.length > 0) {
          this.errorMsg = err.error.errors;
        } else {
          this.errorMsg.push('Failed to load User details', err);
        }
      }
    })
  }
}
