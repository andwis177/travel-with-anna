/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';


export interface GetExchangeRate$Params {
  currencyFrom: string;
  currencyTo: string;
}

export function getExchangeRate(http: HttpClient, rootUrl: string, params: GetExchangeRate$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
  const rb = new RequestBuilder(rootUrl, getExchangeRate.PATH, 'get');
  if (params) {
    rb.query('currencyFrom', params.currencyFrom, {});
    rb.query('currencyTo', params.currencyTo, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return (r as HttpResponse<any>).clone({ body: parseFloat(String((r as HttpResponse<any>).body)) }) as StrictHttpResponse<number>;
    })
  );
}

getExchangeRate.PATH = '/expanse/exchange';
