import { Component } from '@angular/core';
import {TripNewComponent} from "../trip-new/trip-new.component";
import {MatDialog} from "@angular/material/dialog";
import {MatTooltip} from "@angular/material/tooltip";

@Component({
  selector: 'app-trip-list-buttons',
  standalone: true,
  imports: [
    MatTooltip
  ],
  templateUrl: './trip-list-buttons.component.html',
  styleUrl: './trip-list-buttons.component.scss'
})
export class TripListButtons {

  constructor(public dialog: MatDialog) {
  }

  newTrip(event: Event) {
    event.preventDefault();
    const dialogRef = this.dialog.open(TripNewComponent, {})
    dialogRef.afterClosed().subscribe(() => {
    });
  }
}
