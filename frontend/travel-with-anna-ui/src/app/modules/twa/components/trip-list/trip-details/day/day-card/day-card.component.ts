import {Component, EventEmitter, Input, Output} from '@angular/core';
import {DayResponse} from "../../../../../../../services/models/day-response";
import {Router} from "@angular/router";
import {DatePipe, NgClass} from "@angular/common";
import {MatTooltip} from "@angular/material/tooltip";
import {DayDeleteComponent} from "./day-delete/day-delete.component";
import {MatDialog} from "@angular/material/dialog";
import {provideNativeDateAdapter} from "@angular/material/core";

@Component({
  selector: 'app-day',
  standalone: true,
  imports: [
    NgClass,
    MatTooltip
  ],
  providers: [provideNativeDateAdapter(), DatePipe],
  templateUrl: './day-card.component.html',
  styleUrl: './day-card.component.scss'
})
export class DayCardComponent {
  private _day: DayResponse = {};
  @Output() afterDayDelete: EventEmitter<void> = new EventEmitter<void>();

  constructor(private router: Router,
              public dialog: MatDialog,
              private datePipe: DatePipe,) {
  }

  get day(): DayResponse {
    return this._day;
  }

  @Input()
  set day(value: DayResponse) {
    this._day = value;
  }

  openDay(event: Event) {
    event.preventDefault();
    this.router.navigate(['/twa/day', this._day.dayId]).then();
  }

  getTodayClass(isToday: boolean): string {
    switch (isToday) {
      case true:
        return 'today';
      default:
        return 'default';
    }
  }

  onDelete(event: Event) {
    event.preventDefault();
    const dialogRef = this.dialog.open(DayDeleteComponent, {
      maxWidth: '90vw',
      maxHeight: '90vh',
      width: 'auto',
      height: 'auto',
      id: 'delete-day-dialog',
      data: {
        dayId: this._day.dayId,
        date: this.formatDateToJson()
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      this.afterDayDelete.emit();
    });
  }

  private formatDateToJson(): string {
    const formattedDate = this.datePipe.transform(this._day.date, 'yyyy-MM-dd');
    return formattedDate ? formattedDate : '';
  }
}
