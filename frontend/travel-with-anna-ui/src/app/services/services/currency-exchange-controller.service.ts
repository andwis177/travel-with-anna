/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { CurrencyExchangeDto } from '../models/currency-exchange-dto';
import { getCurrencyExchangeRates } from '../fn/currency-exchange-controller/get-currency-exchange-rates';
import { GetCurrencyExchangeRates$Params } from '../fn/currency-exchange-controller/get-currency-exchange-rates';

@Injectable({ providedIn: 'root' })
export class CurrencyExchangeControllerService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `getCurrencyExchangeRates()` */
  static readonly GetCurrencyExchangeRatesPath = '/api/exchange/rates';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getCurrencyExchangeRates()` instead.
   *
   * This method doesn't expect any request body.
   */
  getCurrencyExchangeRates$Response(params?: GetCurrencyExchangeRates$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<CurrencyExchangeDto>>> {
    return getCurrencyExchangeRates(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getCurrencyExchangeRates$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getCurrencyExchangeRates(params?: GetCurrencyExchangeRates$Params, context?: HttpContext): Observable<Array<CurrencyExchangeDto>> {
    return this.getCurrencyExchangeRates$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<CurrencyExchangeDto>>): Array<CurrencyExchangeDto> => r.body)
    );
  }

}
