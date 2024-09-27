/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { ExpanseRequest } from '../../models/expanse-request';
import { ExpanseResponse } from '../../models/expanse-response';

export interface CreateOrUpdateExpanse$Params {
      body: ExpanseRequest
}

export function createOrUpdateExpanse(http: HttpClient, rootUrl: string, params: CreateOrUpdateExpanse$Params, context?: HttpContext): Observable<StrictHttpResponse<ExpanseResponse>> {
  const rb = new RequestBuilder(rootUrl, createOrUpdateExpanse.PATH, 'post');
  if (params) {
    rb.body(params.body, 'application/json');
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

createOrUpdateExpanse.PATH = '/expanse/save';
