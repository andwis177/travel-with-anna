/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { City } from '../models/city';
import { Country } from '../models/country';
import { CountryCurrency } from '../models/country-currency';
import { getAllCountryCurrencies } from '../fn/country-controller/get-all-country-currencies';
import { GetAllCountryCurrencies$Params } from '../fn/country-controller/get-all-country-currencies';
import { getAllCountryNames } from '../fn/country-controller/get-all-country-names';
import { GetAllCountryNames$Params } from '../fn/country-controller/get-all-country-names';
import { getCountryCities } from '../fn/country-controller/get-country-cities';
import { GetCountryCities$Params } from '../fn/country-controller/get-country-cities';

@Injectable({ providedIn: 'root' })
export class CountryControllerService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `getCountryCities()` */
  static readonly GetCountryCitiesPath = '/api/country/cities';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getCountryCities()` instead.
   *
   * This method doesn't expect any request body.
   */
  getCountryCities$Response(params: GetCountryCities$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<City>>> {
    return getCountryCities(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getCountryCities$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getCountryCities(params: GetCountryCities$Params, context?: HttpContext): Observable<Array<City>> {
    return this.getCountryCities$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<City>>): Array<City> => r.body)
    );
  }

  /** Path part for operation `getAllCountryNames()` */
  static readonly GetAllCountryNamesPath = '/api/country/names';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getAllCountryNames()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllCountryNames$Response(params?: GetAllCountryNames$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<Country>>> {
    return getAllCountryNames(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getAllCountryNames$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllCountryNames(params?: GetAllCountryNames$Params, context?: HttpContext): Observable<Array<Country>> {
    return this.getAllCountryNames$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<Country>>): Array<Country> => r.body)
    );
  }

  /** Path part for operation `getAllCountryCurrencies()` */
  static readonly GetAllCountryCurrenciesPath = '/api/country/currencies';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getAllCountryCurrencies()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllCountryCurrencies$Response(params?: GetAllCountryCurrencies$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<CountryCurrency>>> {
    return getAllCountryCurrencies(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getAllCountryCurrencies$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllCountryCurrencies(params?: GetAllCountryCurrencies$Params, context?: HttpContext): Observable<Array<CountryCurrency>> {
    return this.getAllCountryCurrencies$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<CountryCurrency>>): Array<CountryCurrency> => r.body)
    );
  }

}
