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
    MatRowDef
  ],
  templateUrl: './users-list.component.html',
  styleUrl: './users-list.component.scss'
})
export class UsersListComponent implements OnInit, AfterViewInit {
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
  size = 5;
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
    'roles'];
  dataSource = new MatTableDataSource<any>([]);
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private adminService: AdminService) {
  }

  ngOnInit(): void {
    this.loadUsers()
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
}
