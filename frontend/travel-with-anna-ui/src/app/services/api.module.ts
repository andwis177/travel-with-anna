/* tslint:disable */
/* eslint-disable */
import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApiConfiguration, ApiConfigurationParams } from './api-configuration';

import { TripService } from './services/trip.service';
import { PdfDocService } from './services/pdf-doc.service';
import { NoteService } from './services/note.service';
import { ExpanseService } from './services/expanse.service';
import { DayService } from './services/day.service';
import { BudgetService } from './services/budget.service';
import { BackpackService } from './services/backpack.service';
import { AvatarService } from './services/avatar.service';
import { AuthenticationService } from './services/authentication.service';
import { UserService } from './services/user.service';
import { AdminService } from './services/admin.service';
import { RoleService } from './services/role.service';
import { CountryApiControllerService } from './services/country-api-controller.service';

/**
 * Module that provides all services and configuration.
 */
@NgModule({
  imports: [],
  exports: [],
  declarations: [],
  providers: [
    TripService,
    PdfDocService,
    NoteService,
    ExpanseService,
    DayService,
    BudgetService,
    BackpackService,
    AvatarService,
    AuthenticationService,
    UserService,
    AdminService,
    RoleService,
    CountryApiControllerService,
    ApiConfiguration
  ],
})
export class ApiModule {
  static forRoot(params: ApiConfigurationParams): ModuleWithProviders<ApiModule> {
    return {
      ngModule: ApiModule,
      providers: [
        {
          provide: ApiConfiguration,
          useValue: params
        }
      ]
    }
  }

  constructor( 
    @Optional() @SkipSelf() parentModule: ApiModule,
    @Optional() http: HttpClient
  ) {
    if (parentModule) {
      throw new Error('ApiModule is already loaded. Import in your base AppModule only.');
    }
    if (!http) {
      throw new Error('You need to import the HttpClientModule in your AppModule! \n' +
      'See also https://github.com/angular/angular/issues/20575');
    }
  }
}
