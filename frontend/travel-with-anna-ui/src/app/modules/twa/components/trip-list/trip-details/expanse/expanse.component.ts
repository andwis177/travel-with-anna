import {Component, Inject, OnInit} from '@angular/core';
import {MatDivider} from "@angular/material/divider";
import {NgForOf, NgIf} from "@angular/common";
import {MatToolbarRow} from "@angular/material/toolbar";
import {MatIcon} from "@angular/material/icon";
import {MatCard, MatCardActions} from "@angular/material/card";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatOption, MatSelect} from "@angular/material/select";
import {MatIconButton} from "@angular/material/button";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {ExpanseService} from "../../../../../../services/services/expanse.service";
import {ExpanseItem} from "../../../../../../services/models/expanse-item";
import {FormsModule} from "@angular/forms";
import {MatInput} from "@angular/material/input";
import {GetExpanseForItem$Params} from "../../../../../../services/fn/expanse/get-expanse-for-item";
import {ErrorService} from "../../../../../../services/error/error.service";
import {CountryControllerService} from "../../../../../../services/services/country-controller.service";
import {CountryCurrency} from "../../../../../../services/models/country-currency";
import {MatTooltip} from "@angular/material/tooltip";
import {GetExchangeRate$Params} from "../../../../../../services/fn/expanse/get-exchange-rate";
import {GetTripCurrencyValues$Params} from "../../../../../../services/fn/expanse/get-trip-currency-values";
import {CreateOrUpdateExpanse$Params} from "../../../../../../services/fn/expanse/create-or-update-expanse";

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
    MatSuffix
  ],
  templateUrl: './expanse.component.html',
  styleUrl: './expanse.component.scss'
})
export class ExpanseComponent implements OnInit {
  errorMsg: Array<string> = [];
  currencyList: CountryCurrency[] = [];

  item: string = '';
  itemId: number = 0;

  expanse: ExpanseItem = {
    expanseName: '',
    currency: '',
    price: 0,
    paid: 0,
    exchangeRate: 0,
    priceInTripCurrency:0,
    paidInTripCurrency: 0
  };

  currency: string = '';
  tripId: number;

  constructor(public dialog: MatDialog,
              private expanseService: ExpanseService,
              private errorService: ErrorService,
              private countryControllerService: CountryControllerService,
              @Inject(MAT_DIALOG_DATA) public data: { item: string, currency: string, tripId: number, itemId: number }) {
    this.item = data.item;
    this.itemId = data.itemId;
    this.currency = data.currency;
    this.tripId = data.tripId;

    this.expanse.expanseName = this.item;
    this.expanse.currency = this.currency;
  }

  ngOnInit(): void {
    this.getExpanseItem();
    this.getCurrency()
  }

  getExpanseItem() {
    this.errorMsg = [];
    const params : GetExpanseForItem$Params = {itemId: this.itemId};
    console.log("Params: ", params);
    this.expanseService.getExpanseForItem(params)
      .subscribe({
        next:(data) => {
          console.log(data);
          if (data) {
            this.expanse = data;
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
    const params : GetExchangeRate$Params = {currencyFrom: this.expanse.currency, currencyTo: this.currency};
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
    const params: GetTripCurrencyValues$Params = {price: this.expanse.price, paid: this.expanse.paid, exchangeRate: this.expanse.exchangeRate};
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

  saveExpanses() {
    this.errorMsg = [];
    const params: CreateOrUpdateExpanse$Params = {
      body: {
        expanseItem: {
          currency: this.expanse.currency,
          exchangeRate: this.expanse.exchangeRate,
          expanseName: this.expanse.expanseName,
          paid: this.expanse.paid,
          price: this.expanse.price,
          paidInTripCurrency: this.expanse.paidInTripCurrency,
          priceInTripCurrency: this.expanse.priceInTripCurrency
        },
        itemId: this.itemId,
        tripId: this.tripId
      }};
    this.expanseService.createOrUpdateExpanse(params)
      .subscribe({
        next: (response) => {
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
      console.warn('Dialog with ID "expanse-dialog" is not open.');
    }
  }
}
