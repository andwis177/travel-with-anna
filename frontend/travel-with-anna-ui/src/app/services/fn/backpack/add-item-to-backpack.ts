/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { ItemWithExpanseRequest } from '../../models/item-with-expanse-request';

export interface AddItemToBackpack$Params {
  backpackId: number;
      body: ItemWithExpanseRequest
}

export function addItemToBackpack(http: HttpClient, rootUrl: string, params: AddItemToBackpack$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
  const rb = new RequestBuilder(rootUrl, addItemToBackpack.PATH, 'patch');
  if (params) {
    rb.path('backpackId', params.backpackId, {});
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'text', accept: '*/*', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return (r as HttpResponse<any>).clone({ body: undefined }) as StrictHttpResponse<void>;
    })
  );
}

addItemToBackpack.PATH = '/backpack/{backpackId}/item-add';
