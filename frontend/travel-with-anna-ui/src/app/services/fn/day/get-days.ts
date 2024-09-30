/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { DayResponse } from '../../models/day-response';

export interface GetDays$Params {
  tripId: number;
}

export function getDays(http: HttpClient, rootUrl: string, params: GetDays$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<DayResponse>>> {
  const rb = new RequestBuilder(rootUrl, getDays.PATH, 'get');
  if (params) {
    rb.path('tripId', params.tripId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<Array<DayResponse>>;
    })
  );
}

getDays.PATH = '/day/{tripId}';
