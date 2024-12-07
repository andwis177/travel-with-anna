import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CurrencyPipe, NgClass, NgIf} from "@angular/common";
import {ActivityResponse} from "../../../../../../../../services/models/activity-response";
import {FormsModule} from "@angular/forms";
import {MatTooltip} from "@angular/material/tooltip";
import {MatDialog} from "@angular/material/dialog";
import {ContactInformationComponent} from "./contact-information/contact-information.component";
import {NoteComponent} from "../../../note/note.component";
import {ExpanseComponent} from "../../../expanse/expanse.component";
import {SharedService} from "../../../../../../../../services/shared/shared.service";
import {ActivityEditComponent} from "../activity/activity-edit/activity-edit.component";
import {ActivityDeleteComponent} from "./activity-delete/activity-delete.component";
import {MatBadge} from "@angular/material/badge";

@Component({
  selector: 'app-activity-details',
  standalone: true,
  imports: [
    NgClass,
    FormsModule,
    MatTooltip,
    NgIf,
    MatBadge
  ],
  templateUrl: './activity-details.component.html',
  styleUrl: './activity-details.component.scss',
  providers: [CurrencyPipe]
})
export class ActivityDetailsComponent implements OnInit {
  @Input()_activity: ActivityResponse = {};
  @Input()_date: string = '';
  tripId: number = -1;
  isBadgeVisible: boolean = false;
  @Output() _afterActivityEditClosed: EventEmitter<void> = new EventEmitter<void>();

  constructor(public dialog: MatDialog,
              private sharedService: SharedService) {
  }

  ngOnInit(): void {
    this.getTrip();
    if (this._activity) {
      this.checkIfNoteExists();
    }
  }

  getTrip(){
    this.sharedService.getTrip().subscribe({
      next: (trip) => {
        if (trip) {
          this.tripId = trip.tripId!;
        }
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  formatAmount(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'decimal',  // No currency styling
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(amount)
  }

  getContactInformation() {
    const dialogRef = this.dialog.open(ContactInformationComponent, {
      panelClass: 'custom-dialog-container',
      maxWidth: '50vw',
      maxHeight: '50vw',
      width: '30vw',
      height: 'auto',
      id: 'contact-information-dialog',
      data: {
        phone: this._activity.address!.phone,
        email: this._activity.address!.email,
        website: this._activity.address!.website,
      }
    });
    dialogRef.afterClosed().subscribe(() => {
    });
  }

  openNote() {
    const dialogRef = this.dialog.open(NoteComponent, {
      maxWidth: '90vw',
      maxHeight: '90vh',
      width: 'auto',
      height: 'auto',
      id: 'note-dialog',
      data: {
        entityId: this._activity.activityId!,
        entityType: 'activity'
      }
    });
    dialogRef.afterClosed().subscribe(() => {
      this.sharedService.triggerGetActivity();
    });
  }

  async openExpanse() {
    try {
      const dialogRef = this.dialog.open(ExpanseComponent, {
        maxWidth: '90vw',
        maxHeight: '100vh',
        width: 'auto',
        height: 'auto',
        id: 'expanse-dialog',
        data: {
          expanse: this._activity.expanse,
          entityId: this._activity.activityId,
          tripId: this.tripId,
          currency: this._activity.address?.currency,
          entityType: 'activity',
          expanseCategory: this._activity.badge?.toUpperCase()! + ": " + this._activity.type +
           + "\n" + this._activity.address!.place + "\n[" +
            this._activity.address!.city!.toUpperCase() + "]"
        }
      });
      dialogRef.afterClosed().subscribe(() => {
        this._afterActivityEditClosed.emit();
      });
    } catch (error) {
      console.error(error);}
  }

  openEditActivity() {
    this.sharedService.setActivity(this._activity);

    const dialogRef = this.dialog.open(ActivityEditComponent, {
      panelClass: 'custom-dialog-container',
      maxWidth: '90vw',
      maxHeight: '90vw',
      width: '40vw',
      height: 'auto',
      id: 'activity-edit-dialog',
    });
    dialogRef.afterClosed().subscribe(()=> {
      this._afterActivityEditClosed.emit();
    });
  }

  deleteActivity() {
    const dialogRef = this.dialog.open(ActivityDeleteComponent, {
      panelClass: 'custom-dialog-container',
      maxWidth: '50vw',
      maxHeight: '50vw',
      width: '50vw',
      height: 'auto',
      id: 'activity-delete-dialog',
      data: {
        activityId: this._activity.activityId,
      }
    });
    dialogRef.afterClosed().subscribe(() => {
      this._afterActivityEditClosed.emit();
    });
  }

  calculateLeftToPay():number {
    return this._activity.expanse?.price! - this._activity.expanse?.paid!;
  }

  getToPayClass(amount: number): string {
    switch (amount >= 0) {
      case false:
        return 'negative';
      default:
        return 'default-activity';
    }
  }

  checkIfNoteExists() {
    if (this._activity.note !== null) {
      const noSpacesStr = this._activity.note?.note!.replace(/\s+/g, "");
      if (noSpacesStr !== '' && noSpacesStr!.length > 0)
        this.isBadgeVisible = true;
    }
  }
}
