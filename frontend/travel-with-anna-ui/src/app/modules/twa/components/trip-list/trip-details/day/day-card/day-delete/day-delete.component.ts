import {Component, EventEmitter, Inject, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCard, MatCardContent, MatCardHeader} from "@angular/material/card";
import {MatDivider} from "@angular/material/divider";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {NgForOf, NgIf} from "@angular/common";
import {deleteDay, DeleteDay$Params} from "../../../../../../../../services/fn/day/delete-day";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {DayService} from "../../../../../../../../services/services/day.service";

@Component({
  selector: 'app-day-delete',
  standalone: true,
    imports: [
        FormsModule,
        MatCard,
        MatCardContent,
        MatCardHeader,
        MatDivider,
        MatFormField,
        MatIcon,
        MatIconButton,
        MatInput,
        MatLabel,
        MatSuffix,
        NgForOf,
        NgIf,
        ReactiveFormsModule
    ],
  templateUrl: './day-delete.component.html',
  styleUrl: './day-delete.component.scss'
})
export class DayDeleteComponent {
  errorMsg: Array<string> = [];
  dayId: number;
  date: string;


  constructor(public dialog: MatDialog,
              private dayService: DayService,
              @Inject(MAT_DIALOG_DATA) public data: {dayId: number, date: string}) {
  this.dayId = data.dayId;
  this.date = data.date;

}

  onClose() {
    this.dialog.getDialogById('delete-day-dialog')?.close();
  }

  dayDelete() {
   this.errorMsg = [];
    const params: DeleteDay$Params = {dayId: this.dayId};
    this.dayService.deleteDay(params).subscribe({
      next: () => {
        this.onClose();
      },
      error: (err) => {
        this.errorMsg = err.error.errors;
      }
    });
  }
}
