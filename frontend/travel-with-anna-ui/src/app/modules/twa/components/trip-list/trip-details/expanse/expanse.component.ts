import {Component, Inject, OnInit} from '@angular/core';
import {MatDivider} from "@angular/material/divider";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {MatToolbarRow} from "@angular/material/toolbar";
import {MatIcon} from "@angular/material/icon";
import {MatCard, MatCardActions} from "@angular/material/card";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatOption, MatSelect} from "@angular/material/select";
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
import {CreateOrUpdateExpanse$Params} from "../../../../../../services/fn/expanse/create-or-update-expanse";
import {ExpanseResponse} from "../../../../../../services/models/expanse-response";
import {GetExpanseByItemId$Params} from "../../../../../../services/fn/expanse/get-expanse-by-item-id";
import {GetExpanseById$Params} from "../../../../../../services/fn/expanse/get-expanse-by-id";
import {ErrorService} from "../../../../../../services/error/error.service";

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
    NgClass
  ],
  templateUrl: './expanse.component.html',
  styleUrl: './expanse.component.scss'
})
export class ExpanseComponent implements OnInit {
  errorMsg: Array<string> = [];
  currencyList: CountryCurrency[] = [];

  description: string = '';

  expanse: ExpanseResponse = {
    expanseName: '',
    currency: '',
    price: 0,
    paid: 0,
    exchangeRate: 1,
    priceInTripCurrency:0,
    paidInTripCurrency: 0
  };
  leftToPay: number = 0;

  expanseId: number;
  itemId: number;
  tripId: number;
  activityId: number;
  currency: string = '';

  constructor(public dialog: MatDialog,
              private expanseService: ExpanseService,
              private errorService: ErrorService,
              private countryControllerService: CountryControllerService,
              @Inject(MAT_DIALOG_DATA) public data: { description: string,
                currency: string,
                tripId: number,
                itemId: number,
                expanseId: number,
                activityId: number,
              }) {
    this.expanseId = data.expanseId;
    this.description = data.description;
    this.itemId = data.itemId;
    this.tripId = data.tripId;
    this.activityId = data.activityId;
    this.currency = data.currency;
    this.expanse.expanseName = this.description;
    this.expanse.currency = this.currency;
  }

  ngOnInit(): void {
    this.getExpanse()
    this.getCurrency()
  }

  getExpanse() {
    if (this.expanseId > 0) {
      this.getExpanseById();
    } else {
      if (this.itemId > 0) {
        this.getExpanseByItemId();
      }
    }
  }

  getExpanseById() {
    this.errorMsg = [];
    const params: GetExpanseById$Params = {expanseId: this.expanseId};
    this.expanseService.getExpanseById(params)
      .subscribe({
        next: (data) => {
          if (data) {
            this.expanse = data;
            this.leftToPay = data.price! - data.paid!;
          }
        },
        error: (err) => {
          this.errorMsg = this.errorService.errorHandler(err);
        }
      });
  }

  getExpanseByItemId() {
    this.errorMsg = [];
    const params : GetExpanseByItemId$Params = {itemId: this.itemId};
    this.expanseService.getExpanseByItemId(params)
      .subscribe({
        next:(data) => {
          if (data) {
            this.expanse = data;
            this.expanseId = this.expanse.expanseId!;
            this.leftToPay = data.price! - data.paid!;
          }
        },
        error: (err) => {
          this.errorMsg = this.errorService.errorHandler(err);
        }
      });
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

  getExchangeRate(event: Event) {
    event.preventDefault();
    const params : GetExchangeRate$Params = {currencyFrom: this.expanse.currency!, currencyTo: this.currency};
    this.expanseService.getExchangeRate(params)
      .subscribe({
        next: (rate) => {
          this.expanse.exchangeRate = rate;
          this.calculateTripValue();
        },
        error: (err) => {
          this.errorMsg = this.errorService.errorHandler(err);
        }
      });
  }

  calculateTripValue() {
    const params: GetTripCurrencyValues$Params = {price: this.expanse.price!, paid: this.expanse.paid!, exchangeRate: this.expanse.exchangeRate!};
    this.expanseService.getTripCurrencyValues(params)
      .subscribe({
        next: (tripCurrencyValue) => {
          this.expanse.priceInTripCurrency = tripCurrencyValue.price;
          this.expanse.paidInTripCurrency = tripCurrencyValue.paid
        },
        error: (err) => {
          this.errorMsg = this.errorService.errorHandler(err);
        }
      });
  }

  saveItemExpanses() {
    this.errorMsg = [];
    const params: CreateOrUpdateExpanse$Params = {
      body: {
        activityId: this.activityId,
        currency: this.expanse.currency!,
        exchangeRate: this.expanse.exchangeRate!,
        expanseId: this.expanseId,
        expanseName: this.expanse.expanseName,
        itemId: this.itemId,
        paid: this.expanse.paid!,
        paidInTripCurrency: this.expanse.paidInTripCurrency,
        price: this.expanse.price!,
        priceInTripCurrency: this.expanse.priceInTripCurrency,
        tripId: this.tripId,
      },
    };
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
