import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {EatComponent} from "../../activity/buttons/eat/eat.component";
import {EventComponent} from "../../activity/buttons/event/event.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCardContent} from "@angular/material/card";
import {MatDivider} from "@angular/material/divider";
import {MatError, MatLabel} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatOption} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";
import {MatToolbarRow} from "@angular/material/toolbar";
import {NgForOf, NgIf} from "@angular/common";
import {RentComponent} from "../../activity/buttons/rent/rent.component";
import {StayComponent} from "../../activity/buttons/stay/stay.component";
import {TravelComponent} from "../../activity/buttons/travel/travel.component";
import {TrekComponent} from "../../activity/buttons/trek/trek.component";

@Component({
  selector: 'app-contact-information',
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
    ReactiveFormsModule,
    RentComponent,
    StayComponent,
    TravelComponent,
    TrekComponent
  ],
  templateUrl: './contact-information.component.html',
  styleUrl: './contact-information.component.scss'
})
export class ContactInformationComponent {
  phone: string = '';
  email: string = '';
  website: string = '';


  constructor(public dialog: MatDialog,
              @Inject(MAT_DIALOG_DATA) public data: {
                phone: string,
                email: string,
                website: string
              })
  {
    this.phone = data.phone;
    this.email = data.email;
    this.website = data.website;
  }

  onClose() {
    this.dialog.getDialogById('contact-information-dialog')?.close();
  }
}
