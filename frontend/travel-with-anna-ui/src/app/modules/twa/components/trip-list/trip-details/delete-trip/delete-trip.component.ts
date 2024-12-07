import {Component, HostListener, Inject, signal} from '@angular/core';
import {MatDivider} from "@angular/material/divider";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {ErrorService} from "../../../../../../services/error/error.service";
import {TripService} from "../../../../../../services/services/trip.service";
import {DeleteTrip$Params} from "../../../../../../services/fn/trip/delete-trip";
import {Router} from "@angular/router";
import {TripRequest} from "../../../../../../services/models/trip-request";
import {MatCard, MatCardContent, MatCardHeader} from "@angular/material/card";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";

@Component({
  selector: 'app-delete-trip',
  standalone: true,
  imports: [
    MatDivider,
    MatIcon,
    MatIconButton,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    FormsModule,
    MatCardContent,
    MatSuffix,
    MatFormField,
    MatInput,
    MatLabel,
    MatCard,
    MatCardHeader,
  ],
  templateUrl: './delete-trip.component.html',
  styleUrl: './delete-trip.component.scss'
})
export class DeleteTripComponent {
  errorMsg: Array<string> = [];
  tripRequest: TripRequest = {};

  constructor(private tripService: TripService,
              public dialog: MatDialog,
              private errorService: ErrorService,
              private router: Router,
              @Inject(MAT_DIALOG_DATA) public data: {tripId: number}) {
    this.tripRequest.tripId = data.tripId;
  }

  hide = signal(true);
  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKeydownHandler(event: KeyboardEvent): void {
    this.onClose();
  }

  @HostListener('document:keydown.enter', ['$event'])
  onEnterKeydownHandler(event: KeyboardEvent): void {
    this.deleteTrip();
  }

  deleteTrip() {
    this.errorMsg = [];
    const params: DeleteTrip$Params = {body: this.tripRequest};
    this.tripService.deleteTrip(params).subscribe({
      next: () => {
        this.dialog.closeAll();
        this.router.navigate(['/twa/trip-list']).then(() => {});
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandlerWithJson(err);
      }
    });
  }

  onClose() {
    this.dialog.getDialogById('delete-trip-dialog')?.close();
  }
}
