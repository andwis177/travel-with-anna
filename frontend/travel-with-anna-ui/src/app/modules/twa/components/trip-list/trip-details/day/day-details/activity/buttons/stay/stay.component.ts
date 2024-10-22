import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatDivider} from "@angular/material/divider";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatToolbarRow} from "@angular/material/toolbar";
import {DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatTooltip} from "@angular/material/tooltip";
import {MatCardContent} from "@angular/material/card";
import {MatFormField, MatFormFieldModule, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput, MatInputModule} from "@angular/material/input";
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerModule,
  MatDatepickerToggle
} from "@angular/material/datepicker";
import {MatNativeDateModule, MatOption, provideNativeDateAdapter} from "@angular/material/core";
import {MatSelect, MatSelectTrigger} from "@angular/material/select";

@Component({
  selector: 'app-stay',
  standalone: true,
  imports: [
    MatDivider,
    MatIcon,
    MatIconButton,
    MatToolbarRow,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    MatTooltip,
    FormsModule,
    MatCardContent,
    MatFormField,
    MatInput,
    MatLabel,
    MatSuffix,
    MatDatepickerToggle,
    MatDatepicker,
    MatDatepickerInput,
    MatDatepickerModule,
    FormsModule,
    MatInputModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatOption,
    MatSelect,
    MatSelectTrigger,
    NgClass,
  ],
  providers: [provideNativeDateAdapter(), DatePipe],
  templateUrl: './stay.component.html',
  styleUrls: ['./stay.component.scss'],
})
export class StayComponent {
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

  getButtonType(type: string) : string {
    if (this._type === type) {
      return 'selected-button';
    }
    return '';
  }
}
