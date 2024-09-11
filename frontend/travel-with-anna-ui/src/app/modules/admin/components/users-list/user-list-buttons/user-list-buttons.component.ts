import {Component, HostListener} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {SharedService} from "../../../../../services/shared/shared.service";
import {MatInput} from "@angular/material/input";
import {FormsModule} from "@angular/forms";
import {MatTooltip} from "@angular/material/tooltip";

@Component({
  selector: 'app-user-list-buttons',
  standalone: true,
  imports: [
    MatInput,
    FormsModule,
    MatTooltip
  ],
  templateUrl: './user-list-buttons.component.html',
  styleUrl: './user-list-buttons.component.scss'
})
export class UserListButtonsComponent {

  identifier: string = '';

  constructor(
    public dialog: MatDialog,
    private sharedService: SharedService,
  )
  {}

  @HostListener('document:keydown.enter', ['$event'])
  onEnterKeydownHandler(event: KeyboardEvent): void {
    if (this.identifier.length > 0) {
      this.findUser();
    } else {
      if (this.identifier.length === 0) {
        this.AllUsers();
      }
    }
  }

  findUser() {
    this.sharedService.setUserAdminViewIdentifier(this.identifier);
    this.identifier = '';
  }

  AllUsers() {
    this.identifier = '';
    this.sharedService.setUserAdminViewIdentifier('');
  }

}

