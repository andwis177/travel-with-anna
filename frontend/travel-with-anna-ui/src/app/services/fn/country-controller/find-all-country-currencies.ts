/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { CountryCurrency } from '../../models/country-currency';

export interface FindAllCountryCurrencies$Params {
}

export function findAllCountryCurrencies(http: HttpClient, rootUrl: string, params?: FindAllCountryCurrencies$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<CountryCurrency>>> {
  const rb = new RequestBuilder(rootUrl, findAllCountryCurrencies.PATH, 'get');
  if (params) {
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<Array<CountryCurrency>>;
    })
  );
}

findAllCountryCurrencies.PATH = '/api/country/currencies';
