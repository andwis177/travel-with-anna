import {Component, EventEmitter, Input, Output} from '@angular/core';
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
export class EventComponent {
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

  getButtonType(type: string) : string {
    if (this._type === type) {
      return 'selected-button';
    }
    return '';
  }
}
