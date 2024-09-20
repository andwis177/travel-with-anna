/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { createOrUpdateExpanse } from '../fn/expanse/create-or-update-expanse';
import { CreateOrUpdateExpanse$Params } from '../fn/expanse/create-or-update-expanse';
import { ExpanseInTripCurrency } from '../models/expanse-in-trip-currency';
import { ExpanseResponse } from '../models/expanse-response';
import { getExchangeRate } from '../fn/expanse/get-exchange-rate';
import { GetExchangeRate$Params } from '../fn/expanse/get-exchange-rate';
import { getExpanseForItem } from '../fn/expanse/get-expanse-for-item';
import { GetExpanseForItem$Params } from '../fn/expanse/get-expanse-for-item';
import { getTripCurrencyValues } from '../fn/expanse/get-trip-currency-values';
import { GetTripCurrencyValues$Params } from '../fn/expanse/get-trip-currency-values';

@Injectable({ providedIn: 'root' })
export class ExpanseService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `createOrUpdateExpanse()` */
  static readonly CreateOrUpdateExpansePath = '/expanse/save';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `createOrUpdateExpanse()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createOrUpdateExpanse$Response(params: CreateOrUpdateExpanse$Params, context?: HttpContext): Observable<StrictHttpResponse<ExpanseResponse>> {
    return createOrUpdateExpanse(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `createOrUpdateExpanse$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createOrUpdateExpanse(params: CreateOrUpdateExpanse$Params, context?: HttpContext): Observable<ExpanseResponse> {
    return this.createOrUpdateExpanse$Response(params, context).pipe(
      map((r: StrictHttpResponse<ExpanseResponse>): ExpanseResponse => r.body)
    );
  }

  /** Path part for operation `getExpanseForItem()` */
  static readonly GetExpanseForItemPath = '/expanse/{itemId}/expanse';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getExpanseForItem()` instead.
   *
   * This method doesn't expect any request body.
   */
  getExpanseForItem$Response(params: GetExpanseForItem$Params, context?: HttpContext): Observable<StrictHttpResponse<ExpanseResponse>> {
    return getExpanseForItem(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getExpanseForItem$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getExpanseForItem(params: GetExpanseForItem$Params, context?: HttpContext): Observable<ExpanseResponse> {
    return this.getExpanseForItem$Response(params, context).pipe(
      map((r: StrictHttpResponse<ExpanseResponse>): ExpanseResponse => r.body)
    );
  }

  /** Path part for operation `getTripCurrencyValues()` */
  static readonly GetTripCurrencyValuesPath = '/expanse/trip-currency-values';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getTripCurrencyValues()` instead.
   *
   * This method doesn't expect any request body.
   */
  getTripCurrencyValues$Response(params: GetTripCurrencyValues$Params, context?: HttpContext): Observable<StrictHttpResponse<ExpanseInTripCurrency>> {
    return getTripCurrencyValues(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getTripCurrencyValues$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getTripCurrencyValues(params: GetTripCurrencyValues$Params, context?: HttpContext): Observable<ExpanseInTripCurrency> {
    return this.getTripCurrencyValues$Response(params, context).pipe(
      map((r: StrictHttpResponse<ExpanseInTripCurrency>): ExpanseInTripCurrency => r.body)
    );
  }

  /** Path part for operation `getExchangeRate()` */
  static readonly GetExchangeRatePath = '/expanse/exchange';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getExchangeRate()` instead.
   *
   * This method doesn't expect any request body.
   */
  getExchangeRate$Response(params: GetExchangeRate$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
    return getExchangeRate(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getExchangeRate$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getExchangeRate(params: GetExchangeRate$Params, context?: HttpContext): Observable<number> {
    return this.getExchangeRate$Response(params, context).pipe(
      map((r: StrictHttpResponse<number>): number => r.body)
    );
  }

}
