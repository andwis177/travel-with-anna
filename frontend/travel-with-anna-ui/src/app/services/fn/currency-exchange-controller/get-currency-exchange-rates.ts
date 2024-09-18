/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { CurrencyExchangeDto } from '../../models/currency-exchange-dto';

export interface GetCurrencyExchangeRates$Params {
}

export function getCurrencyExchangeRates(http: HttpClient, rootUrl: string, params?: GetCurrencyExchangeRates$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<CurrencyExchangeDto>>> {
  const rb = new RequestBuilder(rootUrl, getCurrencyExchangeRates.PATH, 'get');
  if (params) {
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<Array<CurrencyExchangeDto>>;
    })
  );
}

getCurrencyExchangeRates.PATH = '/api/exchange/rates';