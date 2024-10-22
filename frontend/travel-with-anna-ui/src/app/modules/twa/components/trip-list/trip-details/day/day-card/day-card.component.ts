import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DayResponse} from "../../../../../../../services/models/day-response";
import {Router} from "@angular/router";
import {NgClass, NgForOf} from "@angular/common";
import {MatTooltip} from "@angular/material/tooltip";
import {MatDialog} from "@angular/material/dialog";
import {AddressDetail} from "../../../../../../../services/models/address-detail";
import {ActivityService} from "../../../../../../../services/services/activity.service";
import {SharedService} from "../../../../../../../services/shared/shared.service";

@Component({
  selector: 'app-day',
  standalone: true,
  imports: [
    NgClass,
    MatTooltip,
    NgForOf
  ],
  templateUrl: './day-card.component.html',
  styleUrl: './day-card.component.scss'
})
export class DayCardComponent implements OnInit {
  private _day: DayResponse = {};
  private _amountOfDays: number = 0;
  addressDetails: AddressDetail = {};
  @Output() afterDayDelete: EventEmitter<void> = new EventEmitter<void>();

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
    this.getAddressDetails();
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
}
