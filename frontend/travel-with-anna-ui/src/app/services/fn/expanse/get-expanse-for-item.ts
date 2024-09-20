/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { ExpanseResponse } from '../../models/expanse-response';

export interface GetExpanseForItem$Params {
  itemId: number;
}

export function getExpanseForItem(http: HttpClient, rootUrl: string, params: GetExpanseForItem$Params, context?: HttpContext): Observable<StrictHttpResponse<ExpanseResponse>> {
  const rb = new RequestBuilder(rootUrl, getExpanseForItem.PATH, 'get');
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

getExpanseForItem.PATH = '/expanse/{itemId}/expanse';
