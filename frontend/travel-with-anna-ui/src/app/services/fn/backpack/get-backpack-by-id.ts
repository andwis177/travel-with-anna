/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { BackpackRequest } from '../../models/backpack-request';

export interface GetBackpackById$Params {
  backpackId: number;
}

export function getBackpackById(http: HttpClient, rootUrl: string, params: GetBackpackById$Params, context?: HttpContext): Observable<StrictHttpResponse<BackpackRequest>> {
  const rb = new RequestBuilder(rootUrl, getBackpackById.PATH, 'get');
  if (params) {
    rb.path('backpackId', params.backpackId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<BackpackRequest>;
    })
  );
}

getBackpackById.PATH = '/backpack/{backpackId}';
