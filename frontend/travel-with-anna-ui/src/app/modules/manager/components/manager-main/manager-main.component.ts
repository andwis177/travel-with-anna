import {AfterViewInit, Component, HostListener, inject, OnInit, ViewChild} from '@angular/core';
import {LogoComponent} from "../../../components/menu/logo/logo.component";
import {ManagerButtonsComponent} from "../manager-buttons/manager-buttons.component";
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
import {MatTooltip} from "@angular/material/tooltip";
import {UserComponent} from "../../../components/menu/user/user.component";
import {PageResponseUserAdminResponse} from "../../../../services/models/page-response-user-admin-response";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {SelectionModel} from "@angular/cdk/collections";
import {UserAdminResponse} from "../../../../services/models/user-admin-response";
import {MatDialog} from "@angular/material/dialog";
import {AdminService} from "../../../../services/services/admin.service";
import {SharedService} from "../../../../services/shared/shared.service";
import {ErrorService} from "../../../../services/error/error.service";
import {EditComponent} from "./edit/edit.component";
import {ImageComponent} from "./image/image.component";
import {DeleteComponent} from "./delete/delete.component";
import {GetUserByIdentifier$Params} from "../../../../services/fn/admin/get-user-by-identifier";

@Component({
  selector: 'app-manager-main',
  imports: [
    LogoComponent,
    ManagerButtonsComponent,
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
    MatHeaderCellDef
  ],
  templateUrl: './manager-main.component.html',
  styleUrl: './manager-main.component.scss'
})
export class ManagerMainComponent implements OnInit, AfterViewInit {
  errorMsg: Array<string> = [];
  usersList: PageResponseUserAdminResponse = {
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
          this.usersList = users;
          this.pages = Array(this.usersList.totalPages)
            .fill(0)
            .map((x, i) => i);
          if (this.usersList.content) {
            this.dataSource.data = this.usersList.content;
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
    this.page = this.usersList.totalPages as number - 1;
    this.loadUsers();
  }

  goToNextPage() {
    this.page++;
    this.loadUsers();
  }

  get isLastPage() {
    return this.page === this.usersList.totalPages as number - 1;
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
    this.cleanUserListContent()
    const params: GetUserByIdentifier$Params = {identifier: identifier};
    this.adminService.getUserByIdentifier(params).subscribe({
      next: (user) => {
        this.usersList.content = [user];
        this.usersList.last = true;
        this.usersList.first = true;
        this.usersList.number = 0;
        this.usersList.size = 1;
        this.usersList.totalElements = 1;
        this.usersList.totalPages = 1;
        this.dataSource.data = this.usersList.content;
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
      }
    })
  }

  cleanUserListContent() {
    this.usersList.content = [];
    this.usersList.last = true;
    this.usersList.first = true;
    this.usersList.number = 0;
    this.usersList.size = 0;
    this.usersList.totalElements = 0;
    this.usersList.totalPages = 0;
    this.dataSource.data = this.usersList.content;
  }
}
