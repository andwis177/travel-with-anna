import {Component} from '@angular/core';
import {MatTooltip} from "@angular/material/tooltip";
import {MatDialog} from "@angular/material/dialog";
import {TripNewComponent} from "../trip-new/trip-new.component";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-trip-list-buttons',
  standalone: true,
  imports: [
    MatTooltip,
    FormsModule
  ],
  templateUrl: './trip-list-buttons.component.html',
  styleUrl: './trip-list-buttons.component.scss'
})
export class TripListButtonsComponent {
  constructor(public dialog: MatDialog) {
  }

  newTrip(event: Event) {
    event.preventDefault();
    const dialogRef = this.dialog.open(TripNewComponent, {})
    dialogRef.afterClosed().subscribe(() => {
    });
  }
}
