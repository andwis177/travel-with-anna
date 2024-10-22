/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { ActivityDetailedResponse } from '../../models/activity-detailed-response';

export interface FetchActivitiesByDayId$Params {
  dayId: number;
}

export function fetchActivitiesByDayId(http: HttpClient, rootUrl: string, params: FetchActivitiesByDayId$Params, context?: HttpContext): Observable<StrictHttpResponse<ActivityDetailedResponse>> {
  const rb = new RequestBuilder(rootUrl, fetchActivitiesByDayId.PATH, 'get');
  if (params) {
    rb.path('dayId', params.dayId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<ActivityDetailedResponse>;
    })
  );
}

fetchActivitiesByDayId.PATH = '/activity/get/{dayId}';
