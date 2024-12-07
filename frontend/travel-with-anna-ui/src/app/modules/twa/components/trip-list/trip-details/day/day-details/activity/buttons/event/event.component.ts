import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatTooltip} from "@angular/material/tooltip";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-event',
  standalone: true,
  imports: [
    FormsModule,
    MatTooltip,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './event.component.html',
  styleUrl: './event.component.scss'
})
export class EventComponent implements OnInit, OnChanges  {
  buttonClasses: { [key: string]: string } = {};
  @Input()_type: string = '';
  @Output() provideBadge: EventEmitter<string> = new EventEmitter<string>();
  @Output() provideType: EventEmitter<string> = new EventEmitter<string>();
  @Output() afterEvent: EventEmitter<string> = new EventEmitter<string>();


  provideBadgeToParent(badge:string) {
    this.provideBadge.emit(badge);
  }

  provideTypeToParent(type:string) {
    this.provideType.emit(type);
  }

  createEvent() {
    this.afterEvent.emit();
  }

  ngOnInit(): void {
    this.updateButtonClasses();
  }

  ngOnChanges() {
    this.updateButtonClasses();
  }

  updateButtonClasses() {
    this.buttonClasses = {
      cinema: this._type === 'cinema' ? 'selected-button' : '',
      concert: this._type === 'concert' ? 'selected-button' : '',
      explore: this._type === 'explore' ? 'selected-button' : '',
      extreme: this._type === 'extreme' ? 'selected-button' : '',
      museum: this._type === 'museum' ? 'selected-button' : '',
      nightlife: this._type === 'nightlife' ? 'selected-button' : '',
      recreation: this._type === 'recreation' ? 'selected-button' : '',
      show: this._type === 'show' ? 'selected-button' : '',
      sport: this._type === 'sport' ? 'selected-button' : '',
      theater: this._type === 'theater' ? 'selected-button' : '',
      event: this._type === 'event' ? 'selected-button' : '',
    };
  }
}
