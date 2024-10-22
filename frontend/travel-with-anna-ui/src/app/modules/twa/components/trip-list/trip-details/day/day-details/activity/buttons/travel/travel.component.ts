import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {MatTooltip} from "@angular/material/tooltip";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-travel',
  standalone: true,
  imports: [
    FormsModule,
    MatTooltip,
    NgClass
  ],
  templateUrl: './travel.component.html',
  styleUrl: './travel.component.scss'
})
export class TravelComponent {
  @Input()_type: string = '';
  @Output() provideBadge: EventEmitter<string> = new EventEmitter<string>();
  @Output() provideType: EventEmitter<string> = new EventEmitter<string>();
  @Output() afterTravel: EventEmitter<string> = new EventEmitter<string>();


  provideBadgeToParent(badge: string) {
    this.provideBadge.emit(badge);
  }

  provideTypeToParent(type: string) {
    this.provideType.emit(type);
  }

  createTravel() {
    this.afterTravel.emit();
  }

  getButtonType(type: string) : string {
    if (this._type === type) {
      return 'selected-button';
    }
    return '';
  }
}
