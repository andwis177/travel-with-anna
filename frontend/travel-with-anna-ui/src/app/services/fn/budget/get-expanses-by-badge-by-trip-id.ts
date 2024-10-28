/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { ExpanseTotalByBadge } from '../../models/expanse-total-by-badge';

export interface GetExpansesByBadgeByTripId$Params {
  tripId: number;
}

export function getExpansesByBadgeByTripId(http: HttpClient, rootUrl: string, params: GetExpansesByBadgeByTripId$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<ExpanseTotalByBadge>>> {
  const rb = new RequestBuilder(rootUrl, getExpansesByBadgeByTripId.PATH, 'get');
  if (params) {
    rb.path('tripId', params.tripId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<Array<ExpanseTotalByBadge>>;
    })
  );
}

getExpansesByBadgeByTripId.PATH = '/budget/calculate/{tripId}';
