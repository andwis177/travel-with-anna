import {Component, Inject} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {DeleteDay$Params} from "../../../../../../../../services/fn/day/delete-day";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {DayService} from "../../../../../../../../services/services/day.service";
import {ErrorService} from "../../../../../../../../services/error/error.service";

@Component({
  selector: 'app-day-delete',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './day-delete.component.html',
  styleUrl: './day-delete.component.scss'
})
export class DayDeleteComponent {
  errorMsg: Array<string> = [];
  tripId: number;
  isFirst: boolean;

  constructor(public dialog: MatDialog,
              private dayService: DayService,
              private errorService: ErrorService,
              @Inject(MAT_DIALOG_DATA) public data: {tripId: number, isFirst: boolean}) {
    this.tripId = data.tripId;
    this.isFirst = data.isFirst;
  }

  onClose() {
    this.dialog.getDialogById('delete-day-dialog')?.close();
  }

  deleteDay() {
    const params: DeleteDay$Params = {tripId: this.tripId, isFirst: this.isFirst};
    this.dayService.deleteDay$Response(params).subscribe({
      next: () => {
        this.dialog.getDialogById('delete-day-dialog')?.close();
      },
      error: (err) => {
        this.errorMsg = this.errorService.errorHandlerWithJson(err);
      }
    });
  }
}
