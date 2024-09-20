/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { PageResponseUserAdminResponse } from '../../models/page-response-user-admin-response';

export interface GetAllUsers$Params {
  page?: number;
  size?: number;
}

export function getAllUsers(http: HttpClient, rootUrl: string, params?: GetAllUsers$Params, context?: HttpContext): Observable<StrictHttpResponse<PageResponseUserAdminResponse>> {
  const rb = new RequestBuilder(rootUrl, getAllUsers.PATH, 'get');
  if (params) {
    rb.query('page', params.page, {});
    rb.query('size', params.size, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<PageResponseUserAdminResponse>;
    })
  );
}

getAllUsers.PATH = '/admin/users';
