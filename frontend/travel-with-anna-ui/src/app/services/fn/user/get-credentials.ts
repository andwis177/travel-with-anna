/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { UserCredentialsResponse } from '../../models/user-credentials-response';

export interface GetCredentials$Params {
}

export function getCredentials(http: HttpClient, rootUrl: string, params?: GetCredentials$Params, context?: HttpContext): Observable<StrictHttpResponse<UserCredentialsResponse>> {
  const rb = new RequestBuilder(rootUrl, getCredentials.PATH, 'get');
  if (params) {
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<UserCredentialsResponse>;
    })
  );
}

getCredentials.PATH = '/user/credentials';
