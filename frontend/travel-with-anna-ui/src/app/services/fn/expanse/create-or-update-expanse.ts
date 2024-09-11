/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { ExpanseItem } from '../../models/expanse-item';
import { ExpanseItemCreator } from '../../models/expanse-item-creator';

export interface CreateOrUpdateExpanse$Params {
      body: ExpanseItemCreator
}

export function createOrUpdateExpanse(http: HttpClient, rootUrl: string, params: CreateOrUpdateExpanse$Params, context?: HttpContext): Observable<StrictHttpResponse<ExpanseItem>> {
  const rb = new RequestBuilder(rootUrl, createOrUpdateExpanse.PATH, 'post');
  if (params) {
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<ExpanseItem>;
    })
  );
}

createOrUpdateExpanse.PATH = '/expanse/save';
