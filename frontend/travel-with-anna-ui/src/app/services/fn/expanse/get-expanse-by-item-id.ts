/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { ExpanseResponse } from '../../models/expanse-response';

export interface GetExpanseByItemId$Params {
  itemId: number;
}

export function getExpanseByItemId(http: HttpClient, rootUrl: string, params: GetExpanseByItemId$Params, context?: HttpContext): Observable<StrictHttpResponse<ExpanseResponse>> {
  const rb = new RequestBuilder(rootUrl, getExpanseByItemId.PATH, 'get');
  if (params) {
    rb.path('itemId', params.itemId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<ExpanseResponse>;
    })
  );
}

getExpanseByItemId.PATH = '/expanse/{itemId}/item';
