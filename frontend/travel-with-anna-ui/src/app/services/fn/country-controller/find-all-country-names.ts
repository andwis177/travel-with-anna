/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { CountryNameResponse } from '../../models/country-name-response';

export interface FindAllCountryNames$Params {
}

export function findAllCountryNames(http: HttpClient, rootUrl: string, params?: FindAllCountryNames$Params, context?: HttpContext): Observable<StrictHttpResponse<CountryNameResponse>> {
  const rb = new RequestBuilder(rootUrl, findAllCountryNames.PATH, 'get');
  if (params) {
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<CountryNameResponse>;
    })
  );
}

findAllCountryNames.PATH = '/api/country/names-all';
