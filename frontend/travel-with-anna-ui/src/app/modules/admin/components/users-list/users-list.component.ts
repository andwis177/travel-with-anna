import {AfterViewInit, Component, HostListener, inject, OnInit, ViewChild} from '@angular/core';
import {LiveAnnouncer} from "@angular/cdk/a11y";
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
import {MatDialog} from "@angular/material/dialog";
import {AdminService} from "../../../../services/services/admin.service";
import {SharedService} from "../../../../services/shared/shared.service";
import {GetUserAdminViewByIdentifier$Params} from "../../../../services/fn/admin/get-user-admin-view-by-identifier";
import {MatTooltip} from "@angular/material/tooltip";
import {EditComponent} from "./edit/edit.component";
import {ImageComponent} from "./image/image.component";
import {DeleteComponent} from "./delete/delete.component";
import {MatIconButton} from "@angular/material/button";
import {NgOptimizedImage} from "@angular/common";
import {MatInput} from "@angular/material/input";
import {FormsModule} from "@angular/forms";
import {UserListButtonsComponent} from "./user-list-buttons/user-list-buttons.component";
import {LogoComponent} from "../../../components/menu/logo/logo.component";
import {UserComponent} from "../../../components/menu/user/user.component";
import {PageResponseUserAdminResponse} from "../../../../services/models/page-response-user-admin-response";
import {UserAdminResponse} from "../../../../services/models/user-admin-response";
import {ErrorService} from "../../../../services/error/error.service";

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [
    LogoComponent,
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
    MatTooltip,
    UserComponent,
    MatHeaderCellDef,
    MatIconButton,
    NgOptimizedImage,
    MatInput,
    FormsModule,
    UserListButtonsComponent,
    UserComponent,
    LogoComponent
  ],
  templateUrl: './users-list.component.html',
  styleUrl: './users-list.component.scss'
})
export class UsersListComponent implements OnInit, AfterViewInit {

  errorMsg: Array<string> = [];
  userAdminViewList: PageResponseUserAdminResponse = {
    content: [],
    first: false,
    last: false,
    number: 0,
    size: 0,
    totalElements: 0,
    totalPages: 0
  };
  page = 0;
  size = 10;
  pages: any = [];
  message = '';
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
  selection = new SelectionModel<UserAdminResponse>(false, []);
  identifier: string = '';
  currentRowIndex = -1;

  constructor(public dialog: MatDialog,
              private adminService: AdminService,
              private shareService: SharedService,
              private errorService: ErrorService,
  ) {
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
    this.selection.clear();
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

  ngOnInit(): void {
    this.selection.clear()
    this.loadUsers()
    this.findUserData();

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
      this.shareService.setUserAdminEditId(row.userId as number);
    }
  }

  findUserData() {
    this.shareService.getUserAdminViewIdentifier().subscribe((identifier: string) => {
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

  toggleRow(row: UserAdminResponse) {
    this.currentRowIndex = this.dataSource.data.indexOf(row);
    this.selection.clear();
    this.selection.select(row);
    this.shareService.setUserAdminEditId(row.userId as number);
  }

  loadUsers() {
    this.errorMsg = [];
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

  onEdit() {
    const dialogRef = this.dialog.open(EditComponent, {
      maxWidth: '90vw',
      maxHeight: '90vh',
      width: 'auto',
      height: 'auto',
    })
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  onAvatar() {
    const dialogRef = this.dialog.open(ImageComponent, {
      maxWidth: '90vw',
      maxHeight: '90vh',
      width: 'auto',
      height: 'auto',
    })
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  onDelete() {
    const dialogRef = this.dialog.open(DeleteComponent, {})
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  getUserAdminView(identifier: string) {
    this.errorMsg = [];
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
        this.errorMsg = this.errorService.errorHandler(err);
      }
    })
  }
}

