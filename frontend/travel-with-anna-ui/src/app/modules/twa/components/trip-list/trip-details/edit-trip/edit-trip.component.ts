import {ChangeDetectionStrategy, Component, Inject} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCardActions} from "@angular/material/card";
import {
  MatDatepickerModule,
  MatDatepickerToggle,
  MatDateRangeInput,
  MatDateRangePicker,
  MatEndDate,
  MatStartDate
} from "@angular/material/datepicker";
import {MatDivider} from "@angular/material/divider";
import {MatFormField, MatFormFieldModule, MatLabel, MatPrefix, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatOption, provideNativeDateAdapter} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";
import {MatToolbarRow} from "@angular/material/toolbar";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {TripService} from "../../../../../../services/services/trip.service";
import {UpdateTrip$Params} from "../../../../../../services/fn/trip/update-trip";
import {ErrorService} from "../../../../../../services/error/error.service";

@Component({
  selector: 'app-edit-trip',
  standalone: true,
  imports: [
    FormsModule,
    MatCardActions,
    MatDateRangeInput,
    MatDateRangePicker,
    MatDatepickerToggle,
    MatDivider,
    MatEndDate,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatOption,
    MatPrefix,
    MatSelect,
    MatStartDate,
    MatSuffix,
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
              private datePipe: DatePipe,
              @Inject(MAT_DIALOG_DATA) public data: {tripId: number, tripName: string, startDate: Date, endDate: Date}) {
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
          tripId: this.tripId, startDate: formattedStartDate, endDate: formattedEndDate}, tripName: this.tripName }};
    this.tripService.updateTrip(params).subscribe({next: () => {
        this.onClose();
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandler(err);
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
