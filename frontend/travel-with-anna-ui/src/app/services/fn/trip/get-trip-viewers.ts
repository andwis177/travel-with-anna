/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { ViewerDto } from '../../models/viewer-dto';

export interface GetTripViewers$Params {
  tripId: number;
}

export function getTripViewers(http: HttpClient, rootUrl: string, params: GetTripViewers$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<ViewerDto>>> {
  const rb = new RequestBuilder(rootUrl, getTripViewers.PATH, 'get');
  if (params) {
    rb.path('tripId', params.tripId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<Array<ViewerDto>>;
    })
  );
}

getTripViewers.PATH = '/trip/{tripId}/viewers';
