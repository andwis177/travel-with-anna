import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
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
export class TravelComponent implements OnInit, OnChanges{
  buttonClasses: { [key: string]: string } = {};
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

  ngOnInit(): void {
    this.updateButtonClasses();
  }

  ngOnChanges() {
    this.updateButtonClasses();
  }

  updateButtonClasses() {
    this.buttonClasses = {
      car: this._type === 'car' ? 'selected-button' : '',
      plane: this._type === 'plane' ? 'selected-button' : '',
      bus: this._type === 'bus' ? 'selected-button' : '',
      train: this._type === 'train' ? 'selected-button' : '',
      camper: this._type === 'camper' ? 'selected-button' : '',
      ship: this._type === 'ship' ? 'selected-button' : '',
      travel: this._type === 'travel' ? 'selected-button' : '',
    };
  }
}
