import {AfterViewInit, Component, inject, OnInit, ViewChild} from '@angular/core';
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
  size = 2;
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

  constructor(public dialog: MatDialog,
              private adminService: AdminService,
              private shareService: SharedService,
  ) {
  }

  ngOnInit(): void {
    this.loadUsers()
    this.findUserData();
  }

  findUserData() {
    this.shareService.userAdminViewIdentifier$.subscribe((identifier: string) => {
      if (identifier !== '') {
        this.getUserAdminView(identifier);
        // Perform any additional actions, like refreshing the view
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
    this.selection.clear();
    this.selection.select(row);
  }

  onEdit(element: UserAdminView) {
    this.shareService.setUserAdminEditId(element.userId as number);
    const dialogRef = this.dialog.open(EditComponent, {})
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  onDelete(element: UserAdminView) {
    this.shareService.setUserAdminEditId(element.userId as number);
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
