import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatTooltip} from "@angular/material/tooltip";
import {NgClass, NgIf} from "@angular/common";

@Component({
  selector: 'app-eat',
  standalone: true,
  imports: [
    FormsModule,
    MatTooltip,
    ReactiveFormsModule,
    NgIf,
    NgClass
  ],
  templateUrl: './eat.component.html',
  styleUrl: './eat.component.scss'
})
export class EatComponent {
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

  getButtonType(type: string) : string {
    if (this._type === type) {
      return 'selected-button';
    }
    return '';
  }
}
