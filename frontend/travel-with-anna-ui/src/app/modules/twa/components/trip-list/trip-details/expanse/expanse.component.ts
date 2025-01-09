import {Component, Inject, OnInit} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {MatToolbarRow} from "@angular/material/toolbar";
import {MatIcon} from "@angular/material/icon";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatOption, MatSelect, MatSelectTrigger} from "@angular/material/select";
import {MatIconButton} from "@angular/material/button";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {ExpanseService} from "../../../../../../services/services/expanse.service";
import {FormsModule} from "@angular/forms";
import {MatInput} from "@angular/material/input";
import {CountryControllerService} from "../../../../../../services/services/country-controller.service";
import {CountryCurrency} from "../../../../../../services/models/country-currency";
import {MatTooltip} from "@angular/material/tooltip";
import {GetExchangeRate$Params} from "../../../../../../services/fn/expanse/get-exchange-rate";
import {GetTripCurrencyValues$Params} from "../../../../../../services/fn/expanse/get-trip-currency-values";
import {ExpanseResponse} from "../../../../../../services/models/expanse-response";
import {ErrorService} from "../../../../../../services/error/error.service";
import {CreateOrUpdateExpanse$Params} from "../../../../../../services/fn/expanse/create-or-update-expanse";
import {ExpanseRequest} from "../../../../../../services/models/expanse-request";
import {SharedService} from "../../../../../../services/shared/shared.service";
import {TripCurrencyValuesRequest} from "../../../../../../services/models/trip-currency-values-request";

@Component({
  selector: 'app-expanse',
  standalone: true,
  imports: [
    NgIf,
    NgForOf,
    MatToolbarRow,
    MatIcon,
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption,
    MatIconButton,
    FormsModule,
    MatInput,
    MatTooltip,
    MatSuffix,
    NgClass,
    MatSelectTrigger
  ],
  templateUrl: './expanse.component.html',
  styleUrl: './expanse.component.scss'
})
export class ExpanseComponent implements OnInit {
  errorMsg: Array<string> = [];
  currencyList: CountryCurrency[] = [];
  tripCurrency: string = '';
  currency: string = '';

  expanseRequest: ExpanseRequest = {
    tripId: -1,
    entityId: -1,
    expanseId: -1,
    entityType: '',
    expanseName: '',
    currency: '',
    price: 0,
    paid: 0 ,
    exchangeRate: 1,
    priceInTripCurrency:0,
    paidInTripCurrency: 0,
    expanseCategory:''
  };

  expanseResponse: ExpanseResponse = {
    expanseId: -1,
    expanseName: '',
    currency: '',
    price: 0,
    paid: 0,
    exchangeRate: 1,
    priceInTripCurrency:0,
    paidInTripCurrency: 0
  };
  price: any = '';
  paid: any =''
  leftToPay: number = 0;

  constructor(public dialog: MatDialog,
              private expanseService: ExpanseService,
              private sharedService: SharedService,
              private errorService: ErrorService,
              private countryControllerService: CountryControllerService,
              @Inject(MAT_DIALOG_DATA) public data: {
                currency: string,
                tripId: number,
                entityId: number,
                expanseId: number,
                entityType: string,
                expanse: ExpanseResponse,
                expanseCategory: string,
                date: string
              }) {
  }

  ngOnInit(): void {
    this.expanseRequest.entityId = this.data.entityId;
    this.expanseRequest.tripId = this.data.tripId;
    this.expanseRequest.entityType = this.data.entityType;
    this.expanseRequest.expanseCategory = this.data.expanseCategory;
    this.expanseRequest.date = this.data.date
    if (this.data.expanse) {
      this.expanseResponse = this.data.expanse;
      this.expanseRequest.currency = this.data.expanse.currency!;
      this.expanseRequest.exchangeRate = this.data.expanse.exchangeRate!;
      this.expanseRequest.expanseId = this.data.expanse.expanseId!;
      this.expanseRequest.expanseName = this.data.expanse.expanseName!;
      this.expanseRequest.paid = this.data.expanse.paid!;
      this.expanseRequest.paidInTripCurrency = this.data.expanse.paidInTripCurrency!;
      this.expanseRequest.price = this.data.expanse.price!;
      this.expanseRequest.priceInTripCurrency = this.data.expanse.priceInTripCurrency!;
      this.expanseRequest.expanseId = this.data.expanse.expanseId! as number;
      this.expanseRequest.expanseCategory = this.data.expanse.expanseCategory!;
      this.expanseRequest.date = this.data.expanse.date
    } else {
      this.expanseRequest.currency = this.data.currency;
    }
    this.getCurrency()
    this.getTripCurrency();
    this.calculateLeftToPay()
    if (this.expanseRequest.currency !== null && this.expanseRequest.currency.length > 0) {
      this.currency = this.tripCurrency;
    }
    if (this.expanseRequest.exchangeRate === 1) {
      this.getExchangeRate();
    }
    if (this.expanseRequest.price > 0) {
      this.price = this.expanseRequest.price;
    }
    if (this.expanseRequest.paid > 0) {
      this.paid = this.expanseRequest.paid;
    }
  }

  getTripCurrency(): void  {
    this.sharedService.getTripCurrency().subscribe(
      {
        next: (currency) => {
          this.tripCurrency = currency;
        },
        error: (error) => {
          this.errorMsg.push(error);
        }
      }
    )
  }

  calculateLeftToPay() {
      this.errorMsg = []
      this.leftToPay = this.expanseRequest.price - this.expanseRequest.paid!;
  }

  getCurrency() {
    this.countryControllerService.getAllCountryCurrencies().subscribe( {
      next: (currency) => {
        this.currencyList = currency;
      },
      error: (error) => {
        this.errorMsg.push(error);
      }
    });
  }

  getExchangeRate() {
    this.errorMsg = [];
    if (this.expanseRequest.currency === null || this.expanseRequest.currency.length === 0) {
      this.expanseRequest.currency = this.tripCurrency;
    }
    if (this.expanseRequest.currency !== null && this.expanseRequest.currency.length > 0) {
      const params: GetExchangeRate$Params = {
        currencyFrom: this.expanseRequest.currency!,
        currencyTo: this.tripCurrency!
      };
      this.expanseService.getExchangeRate(params)
        .subscribe({
          next: (rate) => {
            this.expanseRequest.exchangeRate = rate.exchangeRate!;
            if (rate.errorMsg?.length! > 0) {
              this.errorMsg.push(rate.errorMsg!);
            }
            this.calculateTripValue();
          },
          error: (err) => {
            this.errorMsg = this.errorService.errorHandler(err);
          }
        });
    }
  }

  setAsPaid() {
    this.errorMsg = [];
  this.priceValidation();
  this.paid = this.price;
  this.paidValidation();
  this.calculateLeftToPay();

  }

  calculateTripValue() {
    this.errorMsg = [];
    const tripCurrencyValuesRequest: TripCurrencyValuesRequest = {
      exchangeRate: this.expanseRequest.exchangeRate!,
      paid: this.expanseRequest.paid!,
      price: this.expanseRequest.price!,
    }
    const params: GetTripCurrencyValues$Params = {body : tripCurrencyValuesRequest}
    this.expanseService.getTripCurrencyValues(params)
      .subscribe({
        next: (tripCurrencyValue) => {
          this.expanseRequest.priceInTripCurrency = tripCurrencyValue.price;
          this.expanseRequest.paidInTripCurrency = tripCurrencyValue.paid
        },
        error: (err) => {
          this.errorMsg = this.errorService.errorHandler(err);
        }
      });
  }

  saveExpanse() {
    this.errorMsg = [];
    console.log(this.expanseRequest);
    const params: CreateOrUpdateExpanse$Params = {
      body:  this.expanseRequest }
    this.expanseService.createOrUpdateExpanse(params)
      .subscribe({
        next: () => {
          this.onClose();
        },
        error: (err) => {
          this.errorMsg = this.errorService.errorHandler(err);
        }
      });
  }

  onClose() {
    const dialogRef = this.dialog.getDialogById('expanse-dialog');
    if (dialogRef) {
      dialogRef.close();
    } else {
      console.warn('Dialog is not open.');
    }
  }

  getColorAmount(amount: number): string {
    if (amount < 0) {
      return 'negative-color';
    } else {
      return 'positive-color';
    }
  }

  formatAmount(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'decimal',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(amount)
  }

  priceValidation() {
    if (this.price == null || this.price < 0) {
      this.expanseRequest.price = 0;
    } else {
      this.expanseRequest.price = this.price;
    }
    this.calculateLeftToPay();
  }

  paidValidation() {
    if (this.paid == null || this.paid < 0) {
      this.expanseRequest.paid = 0;
    } else {
      this.expanseRequest.paid = this.paid;
    }
    this.calculateLeftToPay();
  }
}


