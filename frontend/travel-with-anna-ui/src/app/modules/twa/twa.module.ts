import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {MatButtonModule} from '@angular/material/button';
import { TwaRoutingModule } from './twa-routing.module';
import {provideHttpClient} from "@angular/common/http";


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    TwaRoutingModule,
    MatButtonModule,
    MatMenuModule,
    MatIconModule,
  ],
  providers: [provideHttpClient()]
})
export class TwaModule {

}
