/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { ItemResponse } from '../../models/item-response';

export interface GetAllItemsByBackpackId$Params {
  backpackId: number;
}

export function getAllItemsByBackpackId(http: HttpClient, rootUrl: string, params: GetAllItemsByBackpackId$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<ItemResponse>>> {
  const rb = new RequestBuilder(rootUrl, getAllItemsByBackpackId.PATH, 'get');
  if (params) {
    rb.path('backpackId', params.backpackId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<Array<ItemResponse>>;
    })
  );
}

getAllItemsByBackpackId.PATH = '/item/{backpackId}';
