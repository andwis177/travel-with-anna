import {Component, Inject, OnInit} from '@angular/core';
import {MatDivider} from "@angular/material/divider";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {MatToolbarRow} from "@angular/material/toolbar";
import {MatIcon} from "@angular/material/icon";
import {MatCard, MatCardActions} from "@angular/material/card";
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

@Component({
  selector: 'app-expanse',
  standalone: true,
  imports: [
    MatDivider,
    NgIf,
    NgForOf,
    MatToolbarRow,
    MatIcon,
    MatCard,
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption,
    MatCardActions,
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
    paid: 0,
    exchangeRate: 1,
    priceInTripCurrency:0,
    paidInTripCurrency: 0
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
  leftToPay: number = 0;

  constructor(public dialog: MatDialog,
              private expanseService: ExpanseService,
              private sharedService: SharedService,
              private errorService: ErrorService,
              private countryControllerService: CountryControllerService,
              @Inject(MAT_DIALOG_DATA) public data: { description: string,
                currency: string,
                tripId: number,
                entityId: number,
                expanseId: number,
                entityType: string,
                expanse: ExpanseResponse
              }) {
    if (data.expanse) {
      this.expanseResponse = data.expanse;
      this.expanseRequest.currency = data.expanse.currency!;
      this.expanseRequest.exchangeRate = data.expanse.exchangeRate!;
      this.expanseRequest.expanseId = data.expanse.expanseId!;
      this.expanseRequest.expanseName = data.expanse.expanseName!;
      this.expanseRequest.paid = data.expanse.paid!;
      this.expanseRequest.paidInTripCurrency = data.expanse.paidInTripCurrency!;
      this.expanseRequest.price = data.expanse.price!;
      this.expanseRequest.priceInTripCurrency = data.expanse.priceInTripCurrency!;
      this.expanseRequest.expanseId = data.expanse.expanseId! as number;
    } else {
      this.expanseRequest.expanseName = data.description;
      this.expanseRequest.currency = data.currency;
    }
    this.expanseRequest.entityId = data.entityId;
    this.expanseRequest.tripId = data.tripId;
    this.expanseRequest.entityType = data.entityType;
  }

  ngOnInit(): void {
    this.getCurrency()
    this.getTripCurrency();
    this.calculateLeftToPay()
    if (this.expanseRequest.currency !== null && this.expanseRequest.currency.length > 0) {
      this.currency = this.tripCurrency;
    }
    if (this.expanseRequest.exchangeRate === 1) {
      this.getExchangeRate();
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
    this.leftToPay = this.expanseRequest.price - this.expanseRequest.paid!;
  }

  getCurrency() {
    this.countryControllerService.findAllCountryCurrencies().subscribe( {
      next: (currency) => {
        this.currencyList = currency;
      },
      error: (error) => {
        this.errorMsg.push(error);
      }
    });
  }

  getExchangeRate() {
    const params : GetExchangeRate$Params = {
      currencyFrom: this.expanseRequest.currency!,
      currencyTo: this.tripCurrency!
    };
    this.expanseService.getExchangeRate(params)
      .subscribe({
        next: (rate) => {
          this.expanseRequest.exchangeRate = rate;
          this.calculateTripValue();
        },
        error: (err) => {
          this.errorMsg = this.errorService.errorHandler(err);
        }
      });
  }

  setAsPaid() {
    this.expanseRequest.paid = this.expanseRequest.price;
    this.calculateLeftToPay();
  }

  calculateTripValue() {
    const params: GetTripCurrencyValues$Params = {
      price: this.expanseRequest.price!,
      paid: this.expanseRequest.paid!,
      exchangeRate: this.expanseRequest.exchangeRate!
    };
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
      return 'negative';
    } else {
      return 'positive';
    }
  }
}
