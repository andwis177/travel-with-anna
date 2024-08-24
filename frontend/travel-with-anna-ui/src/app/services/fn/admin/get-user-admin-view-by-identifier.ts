/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { UserAdminView } from '../../models/user-admin-view';

export interface GetUserAdminViewByIdentifier$Params {
  identifier: string;
}

export function getUserAdminViewByIdentifier(http: HttpClient, rootUrl: string, params: GetUserAdminViewByIdentifier$Params, context?: HttpContext): Observable<StrictHttpResponse<UserAdminView>> {
  const rb = new RequestBuilder(rootUrl, getUserAdminViewByIdentifier.PATH, 'get');
  if (params) {
    rb.path('identifier', params.identifier, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<UserAdminView>;
    })
  );
}

getUserAdminViewByIdentifier.PATH = '/admin/user/{identifier}';
