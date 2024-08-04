import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SharedService {
  private userName = new BehaviorSubject<string>('User Name');
  label$ = this.userName.asObservable();

  constructor() { }

  updateLabel(newLabel: string): void {
    this.userName.next(newLabel);
  }
}
