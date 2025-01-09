import {Component, HostListener} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatInput} from "@angular/material/input";
import {MatTooltip} from "@angular/material/tooltip";
import {MatDialog} from "@angular/material/dialog";
import {SharedService} from "../../../../services/shared/shared.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-manager-buttons',
  imports: [
    FormsModule,
    MatInput,
    MatTooltip,
    ReactiveFormsModule
  ],
  templateUrl: './manager-buttons.component.html',
  styleUrl: './manager-buttons.component.scss'
})
export class ManagerButtonsComponent {
  identifier: string = '';

  constructor(
    private router: Router,
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

  goToTrips() {
    this.router.navigate(['twa']).then();
  }
}
