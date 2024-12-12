import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
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
export class RentComponent implements OnInit, OnChanges {
  buttonClasses: { [key: string]: string } = {};
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

  ngOnInit(): void {
    this.updateButtonClasses();
  }

  ngOnChanges() {
    this.updateButtonClasses();
  }

  updateButtonClasses() {
    this.buttonClasses = {
      rent_car: this._type === 'rent-car' ? 'selected-button' : '',
      rent: this._type === 'rent' ? 'selected-button' : '',
    };
  }
}
