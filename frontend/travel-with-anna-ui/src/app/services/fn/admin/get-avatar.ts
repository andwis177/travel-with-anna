/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { UserAvatar } from '../../models/user-avatar';

export interface GetAvatar$Params {
  userId: number;
}

export function getAvatar(http: HttpClient, rootUrl: string, params: GetAvatar$Params, context?: HttpContext): Observable<StrictHttpResponse<UserAvatar>> {
  const rb = new RequestBuilder(rootUrl, getAvatar.PATH, 'get');
  if (params) {
    rb.path('userId', params.userId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<UserAvatar>;
    })
  );
}

getAvatar.PATH = '/admin/avatar/{userId}';
