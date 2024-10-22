import {Component, Inject} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCardContent} from "@angular/material/card";
import {MatDivider} from "@angular/material/divider";
import {MatError, MatFormField, MatLabel} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatOption, provideNativeDateAdapter} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";
import {MatToolbarRow} from "@angular/material/toolbar";
import {MatTooltip} from "@angular/material/tooltip";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {AddressRequest} from "../../../../../../../../../services/models/address-request";
import {Country} from "../../../../../../../../../services/models/country";
import {ActivityRequest} from "../../../../../../../../../services/models/activity-request";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {ActivityService} from "../../../../../../../../../services/services/activity.service";
import {CountryControllerService} from "../../../../../../../../../services/services/country-controller.service";
import {ErrorService} from "../../../../../../../../../services/error/error.service";
import {CreateActivity$Params} from "../../../../../../../../../services/fn/activity/create-activity";
import {
  CreateAssociatedActivities$Params
} from "../../../../../../../../../services/fn/activity/create-associated-activities";
import {DayDetailsButtonsComponent} from "../../day-details-buttons/day-details-buttons.component";
import {StayComponent} from "../buttons/stay/stay.component";
import {TravelComponent} from "../buttons/travel/travel.component";
import {RentComponent} from "../buttons/rent/rent.component";
import {TrekComponent} from "../buttons/trek/trek.component";
import {EatComponent} from "../buttons/eat/eat.component";
import {EventComponent} from "../buttons/event/event.component";
import {City} from "../../../../../../../../../services/models/city";
import {SharedService} from "../../../../../../../../../services/shared/shared.service";
import {TripResponse} from "../../../../../../../../../services/models/trip-response";
import {ShopComponent} from "../buttons/shop/shop.component";

@Component({
  selector: 'app-activity',
  standalone: true,
  imports: [
    FormsModule,
    MatCardContent,
    MatDivider,
    MatError,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatOption,
    MatSelect,
    MatToolbarRow,
    MatTooltip,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    DayDetailsButtonsComponent,
    StayComponent,
    TravelComponent,
    RentComponent,
    TrekComponent,
    EatComponent,
    EventComponent,
    ShopComponent,
  ],
  providers: [provideNativeDateAdapter(), DatePipe],
  templateUrl: './activity.component.html',
  styleUrl: './activity.component.scss'

})
export class ActivityComponent {
  errorMsg: Array<string> = [];
  trip: TripResponse = {};
  startTime: string = '12:00';
  endTime: string = '12:00';
  startDate: string = '';
  endDate: string = '';
  lastTripDay: string = '';
  dates: Array<string> = [];
  addressRequest: AddressRequest = {
    place: '',
    country: '',
    countryCode: '',
    city: '',
    address: '',
    website: '',
    email: '',
    phone: '',
    currency: ''
  };
  country: Country = {};
  countries: Array<Country> = [];
  cities: Array<City> = [];
  city: City = {};
  badge: string = '';
  associated: boolean = false;
  start: ActivityRequest = {tripId: -1, activityTitle: '', dateTime:''};
  end: ActivityRequest = {tripId:-1 , activityTitle: '', dateTime:''};

  constructor(public dialog: MatDialog,
              private datePipe: DatePipe,
              private activityService: ActivityService,
              private countryService: CountryControllerService,
              private errorService: ErrorService,
              private sharedService: SharedService,
              @Inject(MAT_DIALOG_DATA) public data: {
                startDate: string,
                badge: string,
                associated: boolean,
                lastCountry: string,
                lastCountryCode: string,
                lastCity: string,
                lastCountryCurrency: string
              }
  ) {
    this.sharedService.getTrip().subscribe({
      next: (trip) => {
        this.trip = trip!;
      },
      error: (error) => {
        this.errorMsg = this.errorService.errorHandler(error);
      }
    });
    this.startDate = data.startDate;
    this.lastTripDay = this.trip.endDate!;
    this.badge = data.badge;
    this.associated = data.associated;
    this.country = {name: data.lastCountry,currency: data.lastCountryCurrency, iso2: data.lastCountryCode};
    this.city = {city: data.lastCity};
    this.addressRequest.country = this.country.name;
    this.addressRequest.countryCode = this.country.iso2;
    this.addressRequest.currency = this.country.currency;
    this.addressRequest.city = this.city.city;
    this.generateDatesBetween();
    this.getCountries();
    this.getCities(this.addressRequest.country!);
  }

  onClose() {
    this.dialog.getDialogById('activity-dialog')?.close();
  }

  receiveBadge(badge: string) {
    this.start.badge = badge;
    this.end.badge = badge;
  }

  receiveType(type: string) {
    this.start.type = type;
    this.end.type = type;
  }

  receiveFirstStatus(status: string) {
    this.start.status = status;
  }

  receiveSecondStatus(status: string) {
    this.end.status = status;
  }

  buildSingleActivity() {
    this.errorMsg = [];
    this.start.dateTime = this.startDate + 'T' + this.startTime;
    this.start.endTime = this.endTime;
    this.start.tripId = this.trip.tripId!;
    this.start.addressRequest = this.addressRequest;
    this.createSingleActivity(this.start);
    this.onClose();
  }

  buildAssociatedActivity() {
    this.errorMsg = [];
    if (this.endDate != '') {
      this.start.dateTime = this.startDate + 'T' + this.startTime;
      this.end.dateTime = this.endDate + 'T' + this.endTime;

      console.log(this.start.dateTime);
      console.log(this.end.dateTime);

      this.start.tripId = this.trip.tripId!;
      this.end.tripId = this.trip.tripId!;

      this.end.activityTitle = this.start.activityTitle;

      this.start.addressRequest = this.addressRequest;
      this.end.addressRequest = this.addressRequest;

      this.createAssociatedActivities(this.start, this.end);
    } else {
      this.errorMsg.push('Please select a check-out date');
    }
  }

  createSingleActivity(activityRequest: ActivityRequest){
    this.errorMsg = [];
    const params: CreateActivity$Params = {body: activityRequest}
    this.activityService.createActivity(params)
      .subscribe({
        next: () => {
          this.onClose();
        }, error: (error) => {
          this.errorMsg = this.errorService.errorHandlerWithJson(error);
        }
      })
  }

  createAssociatedActivities(firstActivityRequest: ActivityRequest, secondActivityRequest: ActivityRequest) {
    this.errorMsg = [];
    const params: CreateAssociatedActivities$Params = {
      body:{firstRequest:firstActivityRequest, secondRequest: secondActivityRequest}
    }
    this.activityService.createAssociatedActivities(params)
      .subscribe({
        next: () => {
          this.onClose();
        }, error: (error) => {
          this.errorMsg = this.errorService.errorHandlerWithJson(error);
        }
      })
  }

  private formatDateToJson(date: Date): string {
    return this.datePipe.transform(date, 'yyyy-MM-dd')!;
  }

  isValidTime(time: string): boolean {
    const regex = /^(?:[01]\d|2[0-3]):[0-5]\d$/;
    return regex.test(time);
  }

  generateDatesBetween() {
    const currentDate = new Date(this.startDate);
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
    this.addressRequest.city = '';
    if (this.cities.length == 0) {
      this.errorMsg = [];
      this.countryService.findAllCountryCities({country: country}).subscribe({
        next: (response) => {
          this.cities = response;
          if (response.length === 1) {
            this.addressRequest.city = response[0].city!;
          } else {
            this.addressRequest.city = '';
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
    this.addressRequest.city = '';
  }

  onCountryChange(country: Country) {
    this.addressRequest.country = country.name;
    this.addressRequest.countryCode = country.iso2;
    this.addressRequest.currency = country.currency;
    this.clearCities();
    this.getCities(this.addressRequest.country!);
  }

  onCountryChangeInsert(countryName: string) {
    if(countryName !== null) {
      const insertedCountry =
        this.countries.find(
          country =>
            country.name?.toLocaleUpperCase() === countryName?.toLocaleUpperCase());
      if (insertedCountry) {
        this.country = insertedCountry;
        this.addressRequest.country = insertedCountry.name;
        this.addressRequest.countryCode = insertedCountry.iso2;
        this.addressRequest.currency = insertedCountry.currency
        this.clearCities();
        this.getCities(this.addressRequest.country!);
      } else {
        this.country = {name: countryName};
        this.addressRequest.country =countryName;
        this.addressRequest.countryCode = '';
        this.setTripCurrency();
        this.clearCities();
        this.getCities(this.addressRequest.country!);
      }
    }
  }

  setTripCurrency(): void {
    this.sharedService.getTripCurrency().subscribe({
      next: (currency) => {
        this.addressRequest.currency = currency;
      },
      error: (error) => {
        this.errorMsg = this.errorService.errorHandler(error);
      }
    });
  }

  onCityChange(city: City) {
    this.city = city;
    this.addressRequest.city = city.city;
  }

  onCityChangeInsert(cityName: string) {
    if(cityName !== null) {
      const insertedCity =
        this.cities.find(
          city =>
            city.city?.toLocaleUpperCase() === cityName?.toLocaleUpperCase());
      console.log("Inserted City: ", insertedCity);
      if (insertedCity) {
        this.city =  insertedCity;
        this.addressRequest.city = insertedCity.city;
      } else {
        this.city = {city: cityName};
        this.addressRequest.city = cityName;
      }
    }
  }
}
