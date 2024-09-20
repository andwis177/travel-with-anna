import { Component } from '@angular/core';
import {MatTooltip} from "@angular/material/tooltip";

@Component({
  selector: 'app-budget',
  standalone: true,
    imports: [
        MatTooltip
    ],
  templateUrl: './budget.component.html',
  styleUrl: './budget.component.scss'
})
export class BudgetComponent {

  openBudget($event: Event) {
    $event.stopPropagation();

  }
}
