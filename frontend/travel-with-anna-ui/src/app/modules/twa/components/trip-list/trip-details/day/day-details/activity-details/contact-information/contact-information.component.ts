import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCardContent} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatToolbarRow} from "@angular/material/toolbar";

@Component({
  selector: 'app-contact-information',
  standalone: true,
  imports: [
    FormsModule,
    MatCardContent,
    MatIcon,
    MatIconButton,
    MatToolbarRow,
    ReactiveFormsModule
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
