import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatTooltip} from "@angular/material/tooltip";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-trek',
  standalone: true,
  imports: [
    FormsModule,
    MatTooltip,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './trek.component.html',
  styleUrl: './trek.component.scss'
})
export class TrekComponent {
  @Input()_type: string = '';
  @Output() provideBadge: EventEmitter<string> = new EventEmitter<string>();
  @Output() provideType: EventEmitter<string> = new EventEmitter<string>();
  @Output() afterTrek: EventEmitter<string> = new EventEmitter<string>();


  provideBadgeToParent(badge: string) {
    this.provideBadge.emit(badge);
  }

  provideTypeToParent(type: string) {
    this.provideType.emit(type);
  }

  createTrekking() {
    this.afterTrek.emit();
  }

  getButtonType(type: string) : string {
    if (this._type === type) {
      return 'selected-button';
    }
    return '';
  }
}
