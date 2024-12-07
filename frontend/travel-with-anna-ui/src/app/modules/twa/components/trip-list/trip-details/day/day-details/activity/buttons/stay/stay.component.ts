import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
import {DatePipe, NgClass} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatTooltip} from "@angular/material/tooltip";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule, provideNativeDateAdapter} from "@angular/material/core";

@Component({
  selector: 'app-stay',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatTooltip,
    FormsModule,
    MatDatepickerModule,
    FormsModule,
    MatInputModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatNativeDateModule,
    NgClass,
  ],
  providers: [provideNativeDateAdapter(), DatePipe],
  templateUrl: './stay.component.html',
  styleUrls: ['./stay.component.scss'],
})
export class StayComponent implements OnInit, OnChanges {
  buttonClasses: { [key: string]: string } = {};
  @Input()_type: string = '';
  @Output() provideBadge: EventEmitter<string> = new EventEmitter<string>();
  @Output() provideType: EventEmitter<string> = new EventEmitter<string>();
  @Output() provideFirstStatus: EventEmitter<string> = new EventEmitter<string>();
  @Output() provideSecondStatus: EventEmitter<string> = new EventEmitter<string>();
  @Output() afterStay: EventEmitter<string> = new EventEmitter<string>();

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

  createStay() {
    this.afterStay.emit();
  }

  ngOnInit(): void {
    this.updateButtonClasses();
  }

  ngOnChanges() {
    this.updateButtonClasses();
  }

  updateButtonClasses() {
    this.buttonClasses = {
      hotel: this._type === 'hotel' ? 'selected-button' : '',
      camping: this._type === 'camping' ? 'selected-button' : '',
      airbnb: this._type === 'airbnb' ? 'selected-button' : '',
      friends: this._type === 'friends' ? 'selected-button' : '',
      stay: this._type === 'stay' ? 'selected-button' : '',
    };
  }
}
