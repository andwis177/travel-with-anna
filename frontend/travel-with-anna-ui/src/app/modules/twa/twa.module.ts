import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {MatButtonModule} from '@angular/material/button';
import { TwaRoutingModule } from './twa-routing.module';
import {provideHttpClient} from "@angular/common/http";
import {NgxMaterialTimepickerModule} from "ngx-material-timepicker";


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    TwaRoutingModule,
    MatButtonModule,
    MatMenuModule,
    MatIconModule,
    NgxMaterialTimepickerModule.setOpts(
      'HH:mm',
    ),
  ],
  providers: [provideHttpClient()]
})
export class TwaModule {

}
