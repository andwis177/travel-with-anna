import {Component, Inject} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {MatCardActions} from "@angular/material/card";
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerToggle,
  MatDateRangeInput,
  MatDateRangePicker,
  MatEndDate,
  MatStartDate
} from "@angular/material/datepicker";
import {MatDivider} from "@angular/material/divider";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatToolbarRow} from "@angular/material/toolbar";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {ErrorService} from "../../../../../../../../services/error/error.service";
import {SharedService} from "../../../../../../../../services/shared/shared.service";
import {DayService} from "../../../../../../../../services/services/day.service";
import {provideNativeDateAdapter} from "@angular/material/core";

@Component({
  selector: 'app-day-edit',
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
    MatStartDate,
    MatSuffix,
    MatToolbarRow,
    NgForOf,
    NgIf,
    MatDatepickerInput,
    MatDatepicker,
    DatePipe
  ],
  providers: [provideNativeDateAdapter(), DatePipe],
  templateUrl: './day-edit.component.html',
  styleUrl: './day-edit.component.scss'
})
export class DayEditComponent {
  errorMsg: Array<string> = [];
  dayId: number = -1;
  date: Date;
  originalDate: string = '';

  constructor(public dialog: MatDialog,
              private dayService: DayService,
              private errorService: ErrorService,
              private sharedService: SharedService,
              private datePipe: DatePipe,
              @Inject(MAT_DIALOG_DATA) public data: {
                entityId: number,
                date: Date
              }) {
    this.dayId = data.entityId;
    this.date = data.date;
    this.originalDate = this.formatDateToJson(data.date);
  }

  private formatDateToJson(date: Date): string {
    return this.datePipe.transform(date, 'MM-dd-yyyy')!;
  }

  updateDay() {
    this.errorMsg = [];
    const formatedDate = this.formatDateToJson(this.date);
    console.log(formatedDate);
    this.dayService.changeDayDate({body: {entityId: this.dayId, date: formatedDate}}).subscribe({
      next: () => {
        this.sharedService.triggerGetDays();
        this.onClose();
      },
      error: (error) => {
        this.errorMsg = this.errorService.errorHandlerWithJson(error);
      }
    })
  }

  onClose() {
    this.dialog.getDialogById('day-edit-dialog')?.close();
  }
}
