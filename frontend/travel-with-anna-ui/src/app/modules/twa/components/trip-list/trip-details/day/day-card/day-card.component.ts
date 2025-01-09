import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DayResponse} from "../../../../../../../services/models/day-response";
import {Router} from "@angular/router";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {MatTooltip} from "@angular/material/tooltip";
import {MatDialog} from "@angular/material/dialog";
import {AddressDetail} from "../../../../../../../services/models/address-detail";
import {ActivityService} from "../../../../../../../services/services/activity.service";
import {SharedService} from "../../../../../../../services/shared/shared.service";
import {ActivityResponse} from "../../../../../../../services/models/activity-response";
import {NoteComponent} from "../../note/note.component";
import {MatBadge} from "@angular/material/badge";

@Component({
  selector: 'app-day',
  standalone: true,
  imports: [
    NgClass,
    MatTooltip,
    NgForOf,
    NgIf,
    MatBadge,

  ],
  templateUrl: './day-card.component.html',
  styleUrl: './day-card.component.scss'
})
export class DayCardComponent implements OnInit {
  private _day: DayResponse = {};
  private _amountOfDays: number = 0;
  addressDetails: AddressDetail = {};
  taggedActivities: Array<ActivityResponse> = [];
  isBadgeVisible = false;
  @Output() afterDayChange: EventEmitter<void> = new EventEmitter<void>();

  constructor(private router: Router,
              public dialog: MatDialog,
              private activityService: ActivityService,
              private sharedService: SharedService) {
  }

  get day(): DayResponse {
    return this._day;
  }

  get amountOfDays(): number {
    return this._amountOfDays;
  }

  @Input()
  set day(value: DayResponse) {
    this._day = value;
  }

  @Input()
  set amountOfDays(value: number) {
    this._amountOfDays = value;
  }

  ngOnInit() {
    if (this._day){
      this.getAddressDetails();
      this.getTaggedActivities();
      this.checkIfNoteExists();
    }
  }

  openDay(event: Event) {
    event.preventDefault();
    this.sharedService.setDay(this._day);
    this.router.navigate(['/twa/day']).then();
  }

  getTodayClass(isToday: boolean): string {
    switch (isToday) {
      case true:
        return 'today';
      default:
        return 'default';
    }
  }

  getAddressDetails() {
    this.activityService.fetchAddressDetailsByDayId({
      dayId: this._day.dayId!
    }).subscribe({
      next: (response) => {
        this.addressDetails = response;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getTaggedActivities() {
    for (let activity of this._day.activities!.activity!) {
      if (activity.dayTag) {
        this.taggedActivities.push(activity);
      }
    }
  }

  getTooltipContent(activity: any): string {
    let activityTip: string[] = [];
    if (activity.activityTitle) {
      activityTip.push(`${activity.activityTitle}`);
    }
    if (activity.address?.place) {
      activityTip.push(`${activity.address.place}`);
    }
    if (activity.address?.city) {
      activityTip.push(`${activity.address.city}`);
    }
    if (activity.status) {
      activityTip.push(`${activity.status}`);
    }
    if (activity.startTime && !activity.endTime) {
      activityTip.push(`${activity.startTime}`);
    }
    if (activity.startTime && activity.endTime) {
      activityTip.push(`${activity.startTime} - ${activity.endTime}`);
    }
    return activityTip.join(' | ');
  }

  openNote(event: Event) {
    event.preventDefault();
    event.stopPropagation();
    const dialogRef = this.dialog.open(NoteComponent, {
      maxWidth: '90vw',
      maxHeight: '90vh',
      width: 'auto',
      height: 'auto',
      id: 'note-dialog',
      data: {
        entityId: this._day.dayId,
        entityType: 'day'
      }
    });
    dialogRef.afterClosed().subscribe(() => {
      this.sharedService.triggerGetDays();
    });
  }

  iconProvider(activity: ActivityResponse): string {
    if (activity.status) {
      return activity.status;
    } else {
      return activity.type!;
    }
  }

  checkIfNoteExists() {
    if (this._day.note !== null) {
      const noSpacesStr = this._day.note?.note!.replace(/\s+/g, "");
      if (noSpacesStr!.length > 0)
        this.isBadgeVisible = true;
    }
  }
}
