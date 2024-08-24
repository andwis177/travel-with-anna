/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { UserAdminDeleteRequest } from '../../models/user-admin-delete-request';
import { UserRespond } from '../../models/user-respond';

export interface DeleteUser$Params {
      body: UserAdminDeleteRequest
}

export function deleteUser(http: HttpClient, rootUrl: string, params: DeleteUser$Params, context?: HttpContext): Observable<StrictHttpResponse<UserRespond>> {
  const rb = new RequestBuilder(rootUrl, deleteUser.PATH, 'delete');
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

deleteUser.PATH = '/admin/delete/{userId}';
