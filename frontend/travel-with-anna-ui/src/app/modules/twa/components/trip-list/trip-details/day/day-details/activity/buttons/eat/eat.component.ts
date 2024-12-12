import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatTooltip} from "@angular/material/tooltip";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-eat',
  standalone: true,
  imports: [
    FormsModule,
    MatTooltip,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './eat.component.html',
  styleUrl: './eat.component.scss'
})
export class EatComponent implements OnInit, OnChanges {
  buttonClasses: { [key: string]: string } = {};
  @Input()_type: string = '';
  @Output() provideBadge: EventEmitter<string> = new EventEmitter<string>();
  @Output() provideType: EventEmitter<string> = new EventEmitter<string>();
  @Output() afterEat: EventEmitter<string> = new EventEmitter<string>();

  provideBadgeToParent(badge:string) {
    this.provideBadge.emit(badge);
  }

  provideTypeToParent(type:string) {
    this.provideType.emit(type);
  }

  createEat() {
    this.afterEat.emit();
  }

  ngOnInit(): void {
    this.updateButtonClasses();
  }

  ngOnChanges() {
    this.updateButtonClasses();
  }

  updateButtonClasses() {
    this.buttonClasses = {
      breakfast: this._type === 'breakfast' ? 'selected-button' : '',
      lunch: this._type === 'lunch' ? 'selected-button' : '',
      dinner: this._type === 'dinner' ? 'selected-button' : '',
      supper: this._type === 'supper' ? 'selected-button' : '',
      snack: this._type === 'snack' ? 'selected-button' : '',
      eat: this._type === 'eat' ? 'selected-button' : '',
    };
  }
}
