import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCard, MatCardActions, MatCardHeader} from "@angular/material/card";
import {MatDivider} from "@angular/material/divider";
import {MatFormField, MatFormFieldModule, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatToolbarRow} from "@angular/material/toolbar";
import {MatTooltip} from "@angular/material/tooltip";
import {DatePipe, JsonPipe, NgForOf, NgIf} from "@angular/common";
import {TripCreatorRequest} from "../../../../../services/models/trip-creator-request";
import {Router} from "@angular/router";
import {TripService} from "../../../../../services/services/trip.service";
import {MatDialog} from "@angular/material/dialog";
import {MatOption, MatSelect} from "@angular/material/select";
import {CountryCurrency} from "../../../../../services/models/country-currency";
import {CountryControllerService} from "../../../../../services/services/country-controller.service";
import {ErrorService} from "../../../../../services/error/error.service";
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerModule,
  MatDatepickerToggle,
  MatDateRangeInput,
  MatDateRangePicker
} from "@angular/material/datepicker";

import {provideNativeDateAdapter} from "@angular/material/core";
import {DayService} from "../../../../../services/services/day.service";
import {GenerateDays$Params} from "../../../../../services/fn/day/generate-days";

@Component({
  selector: 'app-trip-new',
  standalone: true,
  imports: [
    FormsModule,
    MatCard,
    MatCardActions,
    MatCardHeader,
    MatDivider,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatSuffix,
    MatToolbarRow,
    MatTooltip,
    NgForOf,
    NgIf,
    MatOption,
    MatSelect,
    MatDateRangeInput,
    ReactiveFormsModule,
    MatDatepickerToggle,
    MatDateRangePicker,
    JsonPipe,
    MatDatepickerInput,
    MatDatepicker,
    MatButton,
    MatFormFieldModule,
    MatDatepickerModule

  ],
  providers: [provideNativeDateAdapter(), DatePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './trip-new.component.html',
  styleUrl: './trip-new.component.scss'
})
export class TripNewComponent implements OnInit {
  errorMsg: Array<string> = [];
  tripCreatorRequest: TripCreatorRequest = {tripName:'', currency: '', toSpend: 0};
  currency: Array<CountryCurrency> = [];
  startDate: Date = new Date();
  endDate: Date = new Date();

  constructor(private router: Router,
              private tripService: TripService,
              private dayService: DayService,
              private countryControllerService: CountryControllerService,
              private errorService: ErrorService,
              public dialog: MatDialog,
              private datePipe: DatePipe) {
  }

  ngOnInit(): void {
    this.getCurrency()
  }

  createNewTrip() {
    this.errorMsg = [];
    if (this.dateVerify()) {
      this.tripService.createTrip({
        body: this.tripCreatorRequest
      }).subscribe({
        next: (tripId) => {
          this.generateDays(tripId);
        },
        error: (err) => {
          this.errorMsg = this.errorService.errorHandler(err);
        }
      });
    }
  }

  dateVerify():boolean {
    if (!this.startDate || !this.endDate) {
      this.errorMsg.push('Start Date and End Date are required.');
      return false;
    }
    return true;
  }

  generateDays(tripId: number) {
    this.errorMsg = [];

    if (!this.startDate || !this.endDate) {
      this.errorMsg.push('Start Date and End Date are required.');
      return;
    }

    const formattedStartDate = this.formatDateToJson(this.startDate);
    const formattedEndDate = this.formatDateToJson(this.endDate);
    const params: GenerateDays$Params = {body:
        {startDate: formattedStartDate,  endDate: formattedEndDate, tripId: tripId}};
    this.dayService.generateDays(params)
      .subscribe({
        next: () => {
          console.log('Days generated');
          this.onClose();
          this.router.navigate(['/twa/trip-details', tripId]).then();
        },
        error: (err) => {
          this.errorMsg = this.errorService.errorHandler(err);
        }
      });
  }

  private formatDateToJson(date: Date): string {
    return this.datePipe.transform(date, 'yyyy-MM-dd')!;
  }

  getCurrency() {
    this.countryControllerService.findAllCountryCurrencies().subscribe( {
      next: (currency) => {
        this.currency = currency;
        if(this.currency.length > 0) {
          this.tripCreatorRequest.currency = "PLN" || '';
        }
      },
      error: (error) => {
        this.errorMsg.push(error);
      }
    });
  }

  onClose() {
    this.dialog.closeAll();
  }
}
