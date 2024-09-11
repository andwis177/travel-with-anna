import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatButtonModule} from "@angular/material/button";
import {MatMenuModule} from "@angular/material/menu";
import {MatIconModule} from "@angular/material/icon";
import {AdminRoutingModule} from "./admin-routing.module";



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    AdminRoutingModule,
    MatButtonModule,
    MatMenuModule,
    MatIconModule,
  ]
})
export class AdminModule {

  // @HostListener('window:beforeunload', ['$event'])
  // clearLocalStorage(event: any): void {
  //   localStorage.clear();
  // }
}
