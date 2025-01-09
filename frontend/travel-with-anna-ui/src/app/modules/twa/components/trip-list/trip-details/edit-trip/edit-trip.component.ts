import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {
  MatDatepickerModule,
  MatDateRangeInput,
  MatDateRangePicker,
  MatEndDate,
  MatStartDate
} from "@angular/material/datepicker";
import {MatFormField, MatFormFieldModule, MatLabel} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {provideNativeDateAdapter} from "@angular/material/core";
import {MatToolbarRow} from "@angular/material/toolbar";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {TripService} from "../../../../../../services/services/trip.service";
import {UpdateTrip$Params} from "../../../../../../services/fn/trip/update-trip";
import {ErrorService} from "../../../../../../services/error/error.service";
import {SharedService} from "../../../../../../services/shared/shared.service";

@Component({
  selector: 'app-edit-trip',
  standalone: true,
  imports: [
    FormsModule,
    MatDateRangeInput,
    MatDateRangePicker,
    MatEndDate,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatStartDate,
    MatToolbarRow,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatDatepickerModule,
  ],
  providers: [provideNativeDateAdapter(), DatePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './edit-trip.component.html',
  styleUrl: './edit-trip.component.scss'
})
export class EditTripComponent {
  errorMsg: Array<string> = [];
  tripId: number;
  tripName: string = '';
  startDate: Date;
  endDate: Date;

  constructor(public dialog: MatDialog,
              private tripService: TripService,
              private errorService: ErrorService,
              private sharedService: SharedService,
              private datePipe: DatePipe,
              private changeDetector: ChangeDetectorRef,
              @Inject(MAT_DIALOG_DATA) public data: {
                tripId: number,
                tripName: string,
                startDate: Date,
                endDate: Date}) {
    this.tripId = data.tripId;
    this.tripName = data.tripName;
    this.startDate = data.startDate;
    this.endDate = data.endDate;
  }

  updateTrip() {
    this.errorMsg = [];
    const formattedStartDate = this.formatDateToJson(this.startDate);
    const formattedEndDate = this.formatDateToJson(this.endDate);
    const params: UpdateTrip$Params = { body:{dayGeneratorRequest: {
          associatedTripId: this.tripId, startDate: formattedStartDate, endDate: formattedEndDate}, tripName: this.tripName }};
    this.tripService.updateTrip(params).subscribe({next: () => {
        this.sharedService.triggerGetDays();
        this.onClose();
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandlerWithJson(err);
        this.changeDetector.markForCheck();
      }
    });
  }

  private formatDateToJson(date: Date): string {
    return this.datePipe.transform(date, 'yyyy-MM-dd')!;
  }

  onClose() {
    this.dialog.getDialogById('edit-trip-dialog')?.close();
  }
}
