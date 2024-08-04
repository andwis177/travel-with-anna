/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { PasswordRequest } from '../../models/password-request';
import { UserRespond } from '../../models/user-respond';

export interface Delete$Params {
      body: PasswordRequest
}

export function delete$(http: HttpClient, rootUrl: string, params: Delete$Params, context?: HttpContext): Observable<StrictHttpResponse<UserRespond>> {
  const rb = new RequestBuilder(rootUrl, delete$.PATH, 'delete');
  if (params) {
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<UserRespond>;
    })
  );
}

delete$.PATH = '/user/delete';
