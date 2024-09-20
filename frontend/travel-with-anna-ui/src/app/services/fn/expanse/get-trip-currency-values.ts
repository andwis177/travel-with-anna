/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { ExpanseInTripCurrency } from '../../models/expanse-in-trip-currency';

export interface GetTripCurrencyValues$Params {
  price: number;
  paid: number;
  exchangeRate: number;
}

export function getTripCurrencyValues(http: HttpClient, rootUrl: string, params: GetTripCurrencyValues$Params, context?: HttpContext): Observable<StrictHttpResponse<ExpanseInTripCurrency>> {
  const rb = new RequestBuilder(rootUrl, getTripCurrencyValues.PATH, 'get');
  if (params) {
    rb.query('price', params.price, {});
    rb.query('paid', params.paid, {});
    rb.query('exchangeRate', params.exchangeRate, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<ExpanseInTripCurrency>;
    })
  );
}

getTripCurrencyValues.PATH = '/expanse/trip-currency-values';
