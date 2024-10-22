import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatTooltip} from "@angular/material/tooltip";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-rent',
  standalone: true,
  imports: [
    FormsModule,
    MatTooltip,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './rent.component.html',
  styleUrl: './rent.component.scss'
})
export class RentComponent {
  @Input()_type: string = '';
  @Output() provideBadge: EventEmitter<string> = new EventEmitter<string>();
  @Output() provideType: EventEmitter<string> = new EventEmitter<string>();
  @Output() provideFirstStatus: EventEmitter<string> = new EventEmitter<string>();
  @Output() provideSecondStatus: EventEmitter<string> = new EventEmitter<string>();
  @Output() afterRent: EventEmitter<string> = new EventEmitter<string>();


  provideBadgeToParent(badge:string) {
    this.provideBadge.emit(badge);
  }

  provideTypeToParent(type:string) {
    this.provideType.emit(type);
  }

  provideFirstStatusToParent(type:string) {
    this.provideFirstStatus.emit(type);
  }

  provideSecondStatusToParent(type:string) {
    this.provideSecondStatus.emit(type);
  }

  createRent() {
    this.afterRent.emit();
  }

  getButtonType(type: string) : string {
    if (this._type === type) {
      return 'selected-button';
    }
    return '';
  }
}
