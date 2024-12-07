import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {MatTooltip} from "@angular/material/tooltip";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-shop',
  standalone: true,
  imports: [
    FormsModule,
    MatTooltip,
    NgClass
  ],
  templateUrl: './shop.component.html',
  styleUrl: './shop.component.scss'
})
export class ShopComponent implements OnInit, OnChanges {
  buttonClasses: { [key: string]: string } = {};
  @Input()_type: string = '';
  @Output() provideBadge: EventEmitter<string> = new EventEmitter<string>();
  @Output() provideType: EventEmitter<string> = new EventEmitter<string>();
  @Output() afterShop: EventEmitter<string> = new EventEmitter<string>();


  provideBadgeToParent(badge:string) {
    this.provideBadge.emit(badge);
  }

  provideTypeToParent(type:string) {
    this.provideType.emit(type);
  }

  createShop() {
    this.afterShop.emit();
  }

  ngOnInit(): void {
    this.updateButtonClasses();
  }

  ngOnChanges() {
    this.updateButtonClasses();
  }

  updateButtonClasses() {
    this.buttonClasses = {
      fuel: this._type === 'fuel' ? 'selected-button' : '',
      grocery: this._type === 'grocery' ? 'selected-button' : '',
      souvenir: this._type === 'souvenir' ? 'selected-button' : '',
      clothes: this._type === 'clothes' ? 'selected-button' : '',
      shop: this._type === 'shop' ? 'selected-button' : '',
    };
  }
}
