/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { CountryCurrency } from '../models/country-currency';
import { CountryName } from '../models/country-name';
import { findAllCountryCurrencies } from '../fn/country-controller/find-all-country-currencies';
import { FindAllCountryCurrencies$Params } from '../fn/country-controller/find-all-country-currencies';
import { findAllCountryNames } from '../fn/country-controller/find-all-country-names';
import { FindAllCountryNames$Params } from '../fn/country-controller/find-all-country-names';

@Injectable({ providedIn: 'root' })
export class CountryControllerService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `findAllCountryNames()` */
  static readonly FindAllCountryNamesPath = '/api/country/names-all';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `findAllCountryNames()` instead.
   *
   * This method doesn't expect any request body.
   */
  findAllCountryNames$Response(params?: FindAllCountryNames$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<CountryName>>> {
    return findAllCountryNames(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `findAllCountryNames$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  findAllCountryNames(params?: FindAllCountryNames$Params, context?: HttpContext): Observable<Array<CountryName>> {
    return this.findAllCountryNames$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<CountryName>>): Array<CountryName> => r.body)
    );
  }

  /** Path part for operation `findAllCountryCurrencies()` */
  static readonly FindAllCountryCurrenciesPath = '/api/country/currencies-all';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `findAllCountryCurrencies()` instead.
   *
   * This method doesn't expect any request body.
   */
  findAllCountryCurrencies$Response(params?: FindAllCountryCurrencies$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<CountryCurrency>>> {
    return findAllCountryCurrencies(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `findAllCountryCurrencies$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  findAllCountryCurrencies(params?: FindAllCountryCurrencies$Params, context?: HttpContext): Observable<Array<CountryCurrency>> {
    return this.findAllCountryCurrencies$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<CountryCurrency>>): Array<CountryCurrency> => r.body)
    );
  }

}
