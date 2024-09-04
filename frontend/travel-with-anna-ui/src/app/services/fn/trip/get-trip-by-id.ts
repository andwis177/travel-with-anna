/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { TripDto } from '../../models/trip-dto';

export interface GetTripById$Params {
  tripId: number;
}

export function getTripById(http: HttpClient, rootUrl: string, params: GetTripById$Params, context?: HttpContext): Observable<StrictHttpResponse<TripDto>> {
  const rb = new RequestBuilder(rootUrl, getTripById.PATH, 'get');
  if (params) {
    rb.path('tripId', params.tripId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<TripDto>;
    })
  );
}

getTripById.PATH = '/trip/{tripId}';
