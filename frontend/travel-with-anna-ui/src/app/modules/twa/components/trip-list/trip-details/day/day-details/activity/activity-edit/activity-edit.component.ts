import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {EatComponent} from "../buttons/eat/eat.component";
import {EventComponent} from "../buttons/event/event.component";
import {FormsModule} from "@angular/forms";
import {MatCardContent} from "@angular/material/card";
import {MatDivider} from "@angular/material/divider";
import {MatError, MatLabel} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatOption, provideNativeDateAdapter} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";
import {MatToolbarRow} from "@angular/material/toolbar";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {RentComponent} from "../buttons/rent/rent.component";
import {StayComponent} from "../buttons/stay/stay.component";
import {TravelComponent} from "../buttons/travel/travel.component";
import {TrekComponent} from "../buttons/trek/trek.component";
import {TripResponse} from "../../../../../../../../../services/models/trip-response";
import {MatDialog} from "@angular/material/dialog";
import {ActivityService} from "../../../../../../../../../services/services/activity.service";
import {CountryControllerService} from "../../../../../../../../../services/services/country-controller.service";
import {ErrorService} from "../../../../../../../../../services/error/error.service";
import {SharedService} from "../../../../../../../../../services/shared/shared.service";
import {ActivityResponse} from "../../../../../../../../../services/models/activity-response";
import {DayResponse} from "../../../../../../../../../services/models/day-response";
import {AddressResponse} from "../../../../../../../../../services/models/address-response";
import {Country} from "../../../../../../../../../services/models/country";
import {City} from "../../../../../../../../../services/models/city";
import {ActivityUpdateRequest} from "../../../../../../../../../services/models/activity-update-request";
import {UpdateActivity$Params} from "../../../../../../../../../services/fn/activity/update-activity";
import {DayDetailsButtonsComponent} from "../../day-details-buttons/day-details-buttons.component";
import {ShopComponent} from "../buttons/shop/shop.component";
import {MatCheckbox} from "@angular/material/checkbox";

@Component({
  selector: 'app-activity-edit',
  standalone: true,
  imports: [
    EatComponent,
    EventComponent,
    FormsModule,
    MatCardContent,
    MatDivider,
    MatError,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatOption,
    MatSelect,
    MatToolbarRow,
    NgForOf,
    NgIf,
    RentComponent,
    StayComponent,
    TravelComponent,
    TrekComponent,
    DayDetailsButtonsComponent,
    ShopComponent,
    MatCheckbox
  ],
  providers: [provideNativeDateAdapter(), DatePipe],
  templateUrl: './activity-edit.component.html',
  styleUrl: './activity-edit.component.scss'
})
export class ActivityEditComponent implements OnInit {
  errorMsg: Array<string> = [];
  activity : ActivityResponse = {};
  day: DayResponse = {};
  activityUpdateRequest: ActivityUpdateRequest = {
    activityId: -1,
    dayId: -1,
    newDate: "",
    oldDate: "",
    startTime: "",
    dayTag: false
  };
  address: AddressResponse = {};
  trip: TripResponse = {};
  startTripDate = '';
  lastTripDay = '';
  startTime: string = '';
  endTime: string = '';
  dates: Array<string> = [];
  date: string = '';
  country: Country = {};
  countries: Array<Country> = [];
  cities: Array<City> = [];
  city: City = {};
  @Output() provideType: EventEmitter<string> = new EventEmitter<string>();

  constructor(public dialog: MatDialog,
              private datePipe: DatePipe,
              private activityService: ActivityService,
              private countryService: CountryControllerService,
              private errorService: ErrorService,
              private sharedService: SharedService,
  ) {
    this.sharedService.getTrip().subscribe({
      next: (trip) => {
        this.trip = trip!;
      },
      error: (error) => {
        this.errorMsg = this.errorService.errorHandler(error);
      }
    });
    this.startTripDate = this.trip.startDate!;
    this.lastTripDay = this.trip.endDate!;
    this.getActivity();
    this.address = this.activity.address!;
    this.getDay();
    this.generateDatesBetween();
    this.date = this.day.date!
    this.startTime = this.activity.startTime!;
    this.endTime = this.activity.endTime!;
    this.country = {name: this.address.country, currency: this.address.currency, iso2: this.address.countryCode};
    this.getCountries()
    this.city = {city: this.address.city};
    if (this.country.name !== null && this.country.name?.length! > 0)
    {
      this.getCities(this.country.name!);
    }
    this.provideTypeToButtons(this.activity.type!);
    this.activityUpdateRequest.activityId = this.activity.activityId!;
    this.activityUpdateRequest.activityTitle = this.activity.activityTitle!;
    this.activityUpdateRequest.dayId = this.day.dayId!;
    this.activityUpdateRequest.newDate = this.day.date!;
    this.activityUpdateRequest.oldDate = this.day.date!;
    this.activityUpdateRequest.startTime = this.activity.startTime!;
    this.activityUpdateRequest.dayTag = this.activity.dayTag!;
  }

  ngOnInit() {
  }

  provideTypeToButtons(type:string) {
    this.provideType.emit(type);
  }

  getActivity() {
    this.sharedService.getActivity().subscribe({
      next: (activity) => {
        this.activity = activity;
      },
      error: (error) => {
        this.errorMsg = this.errorService.errorHandler(error);
      }
    });
  }

  getDay() {
    this.sharedService.getDay().subscribe({
      next: (day) => {
        this.day = day;
      },
      error: (error) => {
        this.errorMsg = this.errorService.errorHandler(error);
      }
    });
  }

  onClose() {
    this.dialog.getDialogById('activity-edit-dialog')?.close();
  }

  receiveType(type: string) {
    this.activityUpdateRequest.type = type;
  }

  isValidTime(time: string): boolean {
    const regex = /^(?:[01]\d|2[0-3]):[0-5]\d$/;
    return regex.test(time);
  }

  private formatDateToJson(date: Date): string {
    return this.datePipe.transform(date, 'yyyy-MM-dd')!;
  }

  generateDatesBetween() {
    const currentDate = new Date(this.startTripDate);
    const checkOutDate = new Date(this.lastTripDay);

    if (isNaN(checkOutDate.getTime())) {
      throw new Error('Invalid end date format');
    }

    this.dates = [];
    currentDate.setDate(currentDate.getDate());
    while (currentDate <= checkOutDate) {
      this.dates.push(this.formatDateToJson(new Date(currentDate)));
      currentDate.setDate(currentDate.getDate() + 1);
    }
  }


  getCountries() {
    this.errorMsg = [];
    this.countryService.findAllCountryNames().subscribe({
      next: (response) => {
        this.countries = response;
      },
      error: (error) => {
        this.errorMsg = this.errorService.errorHandler(error);
      }
    });
  }

  getCities(country: string) {
    this.cities = [];
    this.address.city = '';
    if (this.cities.length == 0) {
      this.errorMsg = [];
      this.countryService.findAllCountryCities({country: country}).subscribe({
        next: (response) => {
          this.cities = response;
          if (response.length === 1) {
            this.address.city = response[0].city!;
          } else {
            this.address.city = '';
          }
        },
        error: (error) => {
          this.errorMsg = this.errorService.errorHandler(error);
        }
      });
    }
  }

  clearCities() {
    this.cities = [];
    this.city = {};
    this.address.city = '';
  }

  onCountryChange(country: Country) {
    this.address.country = country.name;
    this.address.countryCode = country.iso2;
    this.address.currency = country.currency;
    this.clearCities();
    this.getCities(this.address.country!);
  }

  onCountryChangeInsert(countryName: string) {
    if (countryName !== null) {
      const insertedCountry =
        this.countries.find(
          country =>
            country.name?.toLocaleUpperCase() === countryName?.toLocaleUpperCase());
      if (insertedCountry) {
        this.country = insertedCountry;
        this.address.country = insertedCountry.name;
        this.address.countryCode = insertedCountry.iso2;
        this.address.currency = insertedCountry.currency
        this.clearCities();
        this.getCities(this.address.country!);
      } else {
        this.country = {name: countryName};
        this.address.country = countryName;
        this.address.countryCode = '';
        this.setTripCurrency();
        this.clearCities();
        this.getCities(this.address.country!);
      }
    }
  }

  setTripCurrency(): void {
    if (this.address.currency === '' || this.address.currency === null) {
      this.sharedService.getTripCurrency().subscribe({
        next: (currency) => {
          this.address.currency = currency;
        },
        error: (error) => {
          this.errorMsg = this.errorService.errorHandler(error);
        }
      });
    }
  }

  onCityChange(city: City) {
    this.city = city;
    this.address.city = city.city;
  }

  onCityChangeInsert(cityName: string) {
    if(cityName !== null) {
      const insertedCity =
        this.cities.find(
          city =>
            city.city?.toLocaleUpperCase() === cityName?.toLocaleUpperCase());
      if (insertedCity) {
        this.city = insertedCity;
        this.address.city = insertedCity.city;
      } else {
        this.city = {city: cityName};
        this.address.city = cityName;
      }
    }
  }

  updateActivity() {
    this.activityUpdateRequest.addressRequest = this.address
    this.activityUpdateRequest.endTime = this.endTime;
    this.activityUpdateRequest.newDate = this.date;
    this.activityUpdateRequest.startTime = this.startTime;

    const params: UpdateActivity$Params = {body: this.activityUpdateRequest};
    this.activityService.updateActivity(params).subscribe({
      next: (respond) => {
        this.sharedService.setActivity(this.activityUpdateRequest);
        this.dialog.getDialogById('activity-edit-dialog')?.close();
        console.log(respond.message);
      },
      error: (error) => {
        this.errorMsg = this.errorService.errorHandlerWithJson(error);
      }
    });
  }
}
