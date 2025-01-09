/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { UserAdminResponse } from '../../models/user-admin-response';

export interface GetUserByIdentifier$Params {
  identifier: string;
}

export function getUserByIdentifier(http: HttpClient, rootUrl: string, params: GetUserByIdentifier$Params, context?: HttpContext): Observable<StrictHttpResponse<UserAdminResponse>> {
  const rb = new RequestBuilder(rootUrl, getUserByIdentifier.PATH, 'get');
  if (params) {
    rb.path('identifier', params.identifier, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<UserAdminResponse>;
    })
  );
}

getUserByIdentifier.PATH = '/admin/user/{identifier}';
