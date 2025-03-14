import {Component, Inject} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {ActivityService} from "../../../../../../../../../services/services/activity.service";
import {ErrorService} from "../../../../../../../../../services/error/error.service";

@Component({
  selector: 'app-activity-delete',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './activity-delete.component.html',
  styleUrl: './activity-delete.component.scss'
})
export class ActivityDeleteComponent {
  errorMsg: Array<string> = [];
  activityId: number = -1;

  constructor(public dialog: MatDialog,
              private activityService: ActivityService,
              private errorService: ErrorService,
              @Inject(MAT_DIALOG_DATA) public data: {activityId: number}) {
    this.activityId = data.activityId;
  }

  deleteActivity() {
    this.errorMsg = [];
    this.activityService.deleteActivityById({activityId: this.activityId}).subscribe({
      next: () => {
        this.onClose();
      },
      error: (error) => {
        this.errorMsg = this.errorService.errorHandlerWithJson(error);
      }
    });
  }

  onClose() {
    this.dialog.getDialogById('activity-delete-dialog')?.close();
  }
}
