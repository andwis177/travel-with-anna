import {Component} from '@angular/core';
import {MatTooltip} from "@angular/material/tooltip";

@Component({
  selector: 'app-day-details-buttons',
  standalone: true,
  imports: [
    MatTooltip
  ],
  templateUrl: './day-details-buttons.component.html',
  styleUrl: './day-details-buttons.component.scss'
})
export class DayDetailsButtonsComponent {

  changeDate($event: MouseEvent) {

  }
}
