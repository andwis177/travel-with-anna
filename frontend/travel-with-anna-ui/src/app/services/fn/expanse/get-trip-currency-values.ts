/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { ExpanseInTripCurrency } from '../../models/expanse-in-trip-currency';
import { TripCurrencyValuesRequest } from '../../models/trip-currency-values-request';

export interface GetTripCurrencyValues$Params {
      body: TripCurrencyValuesRequest
}

export function getTripCurrencyValues(http: HttpClient, rootUrl: string, params: GetTripCurrencyValues$Params, context?: HttpContext): Observable<StrictHttpResponse<ExpanseInTripCurrency>> {
  const rb = new RequestBuilder(rootUrl, getTripCurrencyValues.PATH, 'post');
  if (params) {
    rb.body(params.body, 'application/json');
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
