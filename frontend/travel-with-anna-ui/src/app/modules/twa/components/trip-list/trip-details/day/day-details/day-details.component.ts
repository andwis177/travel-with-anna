import {Component, OnInit} from '@angular/core';
import {LogoComponent} from "../../../../../../components/menu/logo/logo.component";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {UserComponent} from "../../../../../../components/menu/user/user.component";
import {DayDetailsButtonsComponent} from "./day-details-buttons/day-details-buttons.component";
import {DayResponse} from "../../../../../../../services/models/day-response";
import {TripResponse} from "../../../../../../../services/models/trip-response";
import {ErrorService} from "../../../../../../../services/error/error.service";
import {ActivityService} from "../../../../../../../services/services/activity.service";
import {FetchActivitiesByDayId$Params} from "../../../../../../../services/fn/activity/fetch-activities-by-day-id";
import {ActivityDetailsComponent} from "./activity-details/activity-details.component";
import {ActivityDetailedResponse} from "../../../../../../../services/models/activity-detailed-response";
import {SharedService} from "../../../../../../../services/shared/shared.service";
import {MatTooltip} from "@angular/material/tooltip";

@Component({
  selector: 'app-day-details',
  standalone: true,
  imports: [
    LogoComponent,
    NgForOf,
    UserComponent,
    DayDetailsButtonsComponent,
    NgIf,
    ActivityDetailsComponent,
    MatTooltip,
    NgClass
  ],
  templateUrl: './day-details.component.html',
  styleUrl: './day-details.component.scss'
})
export class DayDetailsComponent implements OnInit {
  errorMsg: Array<string> = [];
  days: Array<DayResponse> = [];
  amountOfDays: number = 0;
  day: DayResponse = {};
  dayNumber: number = 0;
  trip: TripResponse = {};
  activities: ActivityDetailedResponse = {};
  tripCurrency: string = "";

  constructor(private activityService: ActivityService,
              private errorService: ErrorService,
              private sharedService: SharedService) {

    this.sharedService.getTrip().subscribe({
      next: (trip) => {
        this.trip = trip!;
      }
    });

    this.sharedService.getTripDays().subscribe({
      next: (days) => {
        this.days = days!;
      }
    });

    this.sharedService.getDay().subscribe({
      next: (day) => {
        this.day = day!;
      }
    });

    this.dayNumber = this.day.dayNumber!;
    this.amountOfDays = this.days.length;
  }

  ngOnInit(): void {
    this.getActivities();
    this.getTripCurrency();
    this.sharedService.getActivityTriggerEvent$.subscribe(()=> {
      this.getActivities()
    })
  }

  formatAmount(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'decimal',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(amount)
  }

  getTripCurrency(){
    this.sharedService.getTripCurrency().subscribe({
      next: (currency) => {
        this.tripCurrency = currency!;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getNextDay() {
    if (this.dayNumber <= this.amountOfDays) {
      this.dayNumber++;
      this.day = this.days.find(day => day.dayNumber === this.dayNumber)!;
      this.sharedService.setDay(this.day);
      this.getActivities()
    }
  }

  getPreviousDay() {
    if (this.dayNumber > 1) {
      this.dayNumber--;
      this.day = this.days.find(day => day.dayNumber === this.dayNumber)!;
      this.sharedService.setDay(this.day);
      this.getActivities()
    }
  }

  getActivities(){
    this.errorMsg = [];
    const params: FetchActivitiesByDayId$Params = {dayId: this.day.dayId!};
    this.activityService.fetchActivitiesByDayId(params).subscribe({
      next: (activities: any) => {
        this.activities = activities
      },
      error: (error) => {
        this.errorMsg = this.errorService.errorHandler(error);
      }
    });
  }

  getToPayClass(firstAmount: number, secondAmount: number): string {
    switch (firstAmount >= secondAmount) {
      case false:
        return 'negative-color';
      default:
        return 'font-color-light';
    }
  }
}
