/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { getAllRoleNames } from '../fn/role/get-all-role-names';
import { GetAllRoleNames$Params } from '../fn/role/get-all-role-names';

@Injectable({ providedIn: 'root' })
export class RoleService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `getAllRoleNames()` */
  static readonly GetAllRoleNamesPath = '/role/all-names';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getAllRoleNames()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllRoleNames$Response(params?: GetAllRoleNames$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<string>>> {
    return getAllRoleNames(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getAllRoleNames$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getAllRoleNames(params?: GetAllRoleNames$Params, context?: HttpContext): Observable<Array<string>> {
    return this.getAllRoleNames$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<string>>): Array<string> => r.body)
    );
  }

}
