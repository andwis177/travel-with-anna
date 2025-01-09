/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { RoleNameResponse } from '../../models/role-name-response';

export interface FetchRole$Params {
}

export function fetchRole(http: HttpClient, rootUrl: string, params?: FetchRole$Params, context?: HttpContext): Observable<StrictHttpResponse<RoleNameResponse>> {
  const rb = new RequestBuilder(rootUrl, fetchRole.PATH, 'get');
  if (params) {
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<RoleNameResponse>;
    })
  );
}

fetchRole.PATH = '/user/role';
