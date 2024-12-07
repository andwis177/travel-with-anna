import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
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
export class TrekComponent implements OnInit, OnChanges{
  buttonClasses: { [key: string]: string } = {};
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

  ngOnInit(): void {
    this.updateButtonClasses();
  }

  ngOnChanges() {
    this.updateButtonClasses();
  }

  updateButtonClasses() {
    this.buttonClasses = {
      walking: this._type === 'walking' ? 'selected-button' : '',
      hiking: this._type === 'hiking' ? 'selected-button' : '',
      climbing: this._type === 'climbing' ? 'selected-button' : '',
    };
  }
}
