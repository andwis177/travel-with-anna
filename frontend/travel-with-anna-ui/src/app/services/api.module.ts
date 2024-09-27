/* tslint:disable */
/* eslint-disable */
import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApiConfiguration, ApiConfigurationParams } from './api-configuration';

import { TripService } from './services/trip.service';
import { NoteService } from './services/note.service';
import { ExpanseService } from './services/expanse.service';
import { DayService } from './services/day.service';
import { BudgetService } from './services/budget.service';
import { AvatarService } from './services/avatar.service';
import { AuthenticationService } from './services/authentication.service';
import { UserService } from './services/user.service';
import { ItemService } from './services/item.service';
import { BackpackService } from './services/backpack.service';
import { AdminService } from './services/admin.service';
import { RoleService } from './services/role.service';
import { CurrencyExchangeControllerService } from './services/currency-exchange-controller.service';
import { CountryControllerService } from './services/country-controller.service';

/**
 * Module that provides all services and configuration.
 */
@NgModule({
  imports: [],
  exports: [],
  declarations: [],
  providers: [
    TripService,
    NoteService,
    ExpanseService,
    DayService,
    BudgetService,
    AvatarService,
    AuthenticationService,
    UserService,
    ItemService,
    BackpackService,
    AdminService,
    RoleService,
    CurrencyExchangeControllerService,
    CountryControllerService,
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
