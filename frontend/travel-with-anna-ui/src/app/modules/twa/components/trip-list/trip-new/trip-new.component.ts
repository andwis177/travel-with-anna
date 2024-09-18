import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {MatCard, MatCardActions, MatCardHeader} from "@angular/material/card";
import {MatDivider} from "@angular/material/divider";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatToolbarRow} from "@angular/material/toolbar";
import {MatTooltip} from "@angular/material/tooltip";
import {NgForOf, NgIf} from "@angular/common";
import {TripCreatorRequest} from "../../../../../services/models/trip-creator-request";
import {Router} from "@angular/router";
import {TripService} from "../../../../../services/services/trip.service";
import {MatDialog} from "@angular/material/dialog";
import {MatOption, MatSelect} from "@angular/material/select";
import {CountryCurrency} from "../../../../../services/models/country-currency";
import {CountryControllerService} from "../../../../../services/services/country-controller.service";

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
    MatSelect
  ],
  templateUrl: './trip-new.component.html',
  styleUrl: './trip-new.component.scss'
})
export class TripNewComponent implements OnInit {
  errorMsg: Array<string> = [];
  tripCreatorRequest: TripCreatorRequest = {tripName:'', currency: '', toSpend: 0};
  currency: CountryCurrency[] = [];

  amount: number = 0;

  constructor(private router: Router,
              private tripService: TripService,
              private countryControllerService: CountryControllerService,
              public dialog: MatDialog) {

  }

  ngOnInit(): void {
    this.getCurrency()
  }

  createNewTrip() {
    this.errorMsg = [];
    console.log("tripCreatorRequest", this.tripCreatorRequest);
    this.tripService.createTrip({
      body: this.tripCreatorRequest
    }).subscribe({
      next: (tripId) => {
        this.onClose();
        this.router.navigate(['/twa/trip-details', tripId]).then();
        console.log("response", tripId);
      },
      error: (err) => {
        console.log("error message", err.error.errors || err.error.message);
        if (err.error.errors && err.error.errors.length > 0) {
          this.errorMsg = err.error.errors;
        } else {
          this.errorMsg.push('Unexpected error occurred');
        }
      }
    });
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